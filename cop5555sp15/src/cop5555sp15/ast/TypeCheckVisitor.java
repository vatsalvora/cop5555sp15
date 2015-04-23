package cop5555sp15.ast;

import cop5555sp15.TokenStream;
import cop5555sp15.TypeConstants;
import cop5555sp15.symbolTable.SymbolTable;

public class TypeCheckVisitor implements ASTVisitor, TypeConstants {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		ASTNode node;

		public TypeCheckException(String message, ASTNode node) {
			super(node.firstToken.lineNumber + ":" + message);
			this.node = node;
		}
	}

	SymbolTable symbolTable;

	public TypeCheckVisitor(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	boolean check(boolean condition, String message, ASTNode node)
			throws TypeCheckException {
		if (condition)
			return true;
		throw new TypeCheckException(message, node);
	}

	/**
	 * Ensure that types on left and right hand side are compatible.
	 */
	@Override
	public Object visitAssignmentStatement(
			AssignmentStatement assignmentStatement, Object arg)
			throws Exception {

		assignmentStatement.expression.visit(this,arg);
		assignmentStatement.lvalue.visit(this,arg);
		String ident = assignmentStatement.lvalue.firstToken.getText();
		String expression = assignmentStatement.expression.getType();
		String lvalue = assignmentStatement.lvalue.getType();
		//System.out.println(expression + " " +lvalue);
		if(assignmentStatement.lvalue.getType().equals(assignmentStatement.expression.getType()))
		{

			return assignmentStatement.lvalue.getType();
		}
		else if((assignmentStatement.lvalue.getType().contains("Ljava/util/List<Ljava/util/List")))
		{
			if(expression.contains("Ljava/util/ArrayList"))
			{
				return assignmentStatement.expression.getType();
			}
			else {
				throw new TypeCheckException("Incompatible Types!",assignmentStatement);
			}
		}
		else if((assignmentStatement.lvalue.getType().contains("Ljava/util/List")))
		{
			if((assignmentStatement.expression.getType().contains("Ljava/util/ArrayList")))
			{
				if(!expression.equals("Ljava/util/ArrayList")) {
					if (expression.contains("<I>") && lvalue.contains("<Ljava/lang/Integer;>")) {
						return assignmentStatement.expression.getType();
					} else if (expression.contains("<Z>") && lvalue.contains("<Ljava/lang/Boolean;>")) {
						return assignmentStatement.expression.getType();
					} else if (expression.contains("<Ljava/lang/String;>") && lvalue.contains("<Ljava/lang/String;>")) {
						return assignmentStatement.expression.getType();
					} else if (expression.contains("<Ljava/util/List") && lvalue.contains("<Ljava/util/List")) {
						return assignmentStatement.expression.getType();
					}
					else {
						throw new TypeCheckException("Incompatible Type!", assignmentStatement);
					}
				}
				else {
					return assignmentStatement.expression.getType();
				}
			}
			else
			{
				throw new TypeCheckException("Incompatible Types!",assignmentStatement);
			}
		}
		else if(lvalue.equals("["+intType+"]"))
		{
			if(expression.equals(intType)) {
				return assignmentStatement.expression.getType();
			}
			else {
				throw new TypeCheckException("Need Int Type!",assignmentStatement);
			}
		}
		else if(lvalue.equals("["+booleanType+"]"))
		{
			if(expression.equals(booleanType)) {
				return assignmentStatement.expression.getType();
			}
			else {
				throw new TypeCheckException("Need Boolean Type!",assignmentStatement);
			}
		}
		else if(lvalue.equals("["+stringType+"]"))
		{
			if(expression.equals(stringType)) {
				return assignmentStatement.expression.getType();
			}
			else {
				throw new TypeCheckException("Need String Type!",assignmentStatement);
			}
		}
		else {
			throw new TypeCheckException("Incompatible Types!",assignmentStatement);
		}
	}

	/**
	 * Ensure that both types are the same, save and return the result type
	 */
	@Override
	public Object visitBinaryExpression(BinaryExpression binExp,
			Object arg) throws Exception {
		Expression expo = binExp.expression0;
		Expression expl = binExp.expression1;
		expo.visit(this, arg);
		expl.visit(this,arg);
		if(expo.getType().equals(expl.getType())) {

			if(binExp.op.kind.equals(TokenStream.Kind.EQUAL) || binExp.op.kind.equals(TokenStream.Kind.LE)
					|| binExp.op.kind.equals(TokenStream.Kind.GE) || binExp.op.kind.equals(TokenStream.Kind.GT)
					|| binExp.op.kind.equals(TokenStream.Kind.LT))
			{
				binExp.setType(booleanType);
			}
			else if(binExp.op.kind.equals(TokenStream.Kind.NOTEQUAL))binExp.setType(booleanType);
			else binExp.setType(expo.getType());

			if(expo.getType().equals(intType) && expl.getType().equals(intType)) {
				if (binExp.op.kind.equals(TokenStream.Kind.AND)
						|| binExp.op.kind.equals(TokenStream.Kind.BAR)) {
					throw new TypeCheckException("Incompatible Types!", binExp);
				}
			}
			else if(expo.getType().equals(stringType) && expl.getType().equals(stringType)) {
				if (binExp.op.kind.equals(TokenStream.Kind.AND)
						|| binExp.op.kind.equals(TokenStream.Kind.BAR)
						|| binExp.op.kind.equals(TokenStream.Kind.TIMES)
						|| binExp.op.kind.equals(TokenStream.Kind.MINUS)
						|| binExp.op.kind.equals(TokenStream.Kind.DIV))
				{
					throw new TypeCheckException("Incompatible Types!", binExp);
				}
			}
			else if(expo.getType().equals(booleanType) && expl.getType().equals(booleanType)) {
				if (binExp.op.kind.equals(TokenStream.Kind.LT)
								|| binExp.op.kind.equals(TokenStream.Kind.LE)
								|| binExp.op.kind.equals(TokenStream.Kind.GT)
								|| binExp.op.kind.equals(TokenStream.Kind.GE)
								|| binExp.op.kind.equals(TokenStream.Kind.TIMES)
								|| binExp.op.kind.equals(TokenStream.Kind.MINUS)
								|| binExp.op.kind.equals(TokenStream.Kind.DIV))
				{
					throw new TypeCheckException("Incompatible Types!", binExp);
				}
			}

			return binExp.getType();
		}
		throw new TypeCheckException("Incompatible Types!", binExp);
	}

	/**
	 * Blocks define scopes. Check that the scope nesting level is the same at
	 * the end as at the beginning of block
	 */
	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		int numScopes = symbolTable.enterScope();
		// visit children
		for (BlockElem elem : block.elems) {
			elem.visit(this, arg);
		}
		int numScopesExit = symbolTable.leaveScope();
		check(numScopesExit > 0 && numScopesExit == numScopes,
				"unbalanced scopes", block);
		return null;
	}

	/**
	 * Sets the expressionType to booleanType and returns it
	 * 
	 * @param booleanLitExpression
	 * @param arg
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object visitBooleanLitExpression(
			BooleanLitExpression booleanLitExpression, Object arg)
			throws Exception {
		booleanLitExpression.setType(booleanType);
		return booleanType;
	}

	/**
	 * A closure defines a new scope Visit all the declarations in the
	 * formalArgList, and all the statements in the statementList construct and
	 * set the JVMType, the argType array, and the result type
	 * 
	 * @param closure
	 * @param arg
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object visitClosure(Closure closure, Object arg) throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	/**
	 * Make sure that the name has not already been declared and insert in
	 * symbol table. Visit the closure
	 */
	@Override
	public Object visitClosureDec(ClosureDec closureDec, Object arg) {
		throw new UnsupportedOperationException("not yet implemented");
	}

	/**
	 * Check that the given name is declared as a closure Check the argument
	 * types The type is the return type of the closure
	 */
	@Override
	public Object visitClosureEvalExpression(
			ClosureEvalExpression closureExpression, Object arg)
			throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Object visitClosureExpression(ClosureExpression closureExpression,
			Object arg) throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Object visitExpressionLValue(ExpressionLValue expressionLValue,
			Object arg) throws Exception {
		expressionLValue.expression.visit(this, arg);
		if(!expressionLValue.expression.getType().equals(intType))
		{
			throw new TypeCheckException("Incorrect Index Type!",expressionLValue);
		}
//		System.out.println(expressionLValue.expression.getType());
		String ident = expressionLValue.identToken.getText();
		VarDec d = (VarDec) symbolTable.lookup(ident);
		if( d != null)
		{
			String type = d.type.getJVMType();
			if(type.contains("Ljava/util/List")) {
				if(type.contains("<Ljava/lang/Integer;>"))
				{
					type = intType;
				}
				else if((type.contains("<Ljava/lang/Boolean;>")))
				{
					type = booleanType;
				}
				else if((type.contains("<"+stringType+">")))
				{
					type = stringType;
				}
				expressionLValue.setType("["+type+"]");
			}
			else
			{
				expressionLValue.setType(d.type.getJVMType());
			}
		}
		else {
			throw new TypeCheckException("Variable is already in scope!",expressionLValue);
		}
		return expressionLValue.getType();
	}

	@Override
	public Object visitExpressionStatement(
			ExpressionStatement expressionStatement, Object arg)
			throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	/**
	 * Check that name has been declared in scope Get its type from the
	 * declaration.
	 * 
	 */
	@Override
	public Object visitIdentExpression(IdentExpression identExpression,
			Object arg) throws Exception {
		String ident = identExpression.identToken.getText();
		VarDec d = (VarDec) symbolTable.lookup(ident);
		if(d != null)
		{
			identExpression.setType(d.type.getJVMType());
		}
		else {
			throw new TypeCheckException("Variable Not Declared!",identExpression);
		}
		return identExpression.getType();
	}

	@Override
	public Object visitIdentLValue(IdentLValue identLValue, Object arg)
			throws Exception {
		String ident = identLValue.identToken.getText();
		VarDec d = (VarDec) symbolTable.lookup(ident);
		if( d != null)
		{
			identLValue.setType(d.type.getJVMType());
		}
		else {
			throw new TypeCheckException("Variable is already in scope!",identLValue);
		}
		return identLValue.getType();
	}

	@Override
	public Object visitIfElseStatement(IfElseStatement ifElseStatement,
			Object arg) throws Exception {
		ifElseStatement.expression.visit(this,arg);
		ifElseStatement.ifBlock.visit(this,arg);
		ifElseStatement.elseBlock.visit(this,arg);
		if(!ifElseStatement.expression.getType().equals(booleanType)) {
			throw new TypeCheckException("Not a boolean expression!", ifElseStatement);
		}

		return ifElseStatement.expression.getType();
	}

	/**
	 * expression type is boolean
	 */
	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg)
			throws Exception {
		ifStatement.expression.visit(this,arg);
		ifStatement.block.visit(this, arg);
		if(ifStatement.expression.getType() != booleanType) {
			throw new TypeCheckException("Not a boolean expression!", ifStatement);
		}
		return ifStatement.expression.getType();

	}

	/**
	 * expression type is int
	 */
	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression,
			Object arg) throws Exception {
		intLitExpression.setType(intType);
		return intType;
	}

	@Override
	public Object visitKeyExpression(KeyExpression keyExpression, Object arg)
			throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Object visitKeyValueExpression(
			KeyValueExpression keyValueExpression, Object arg) throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Object visitKeyValueType(KeyValueType keyValueType, Object arg)
			throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	// visit the expressions (children) and ensure they are the same type
	// the return type is "Ljava/util/ArrayList<"+type0+">;" where type0 is the
	// type of elements in the list
	// this should handle lists of lists, and empty list. An empty list is
	// indicated by "Ljava/util/ArrayList;".
	@Override
	public Object visitListExpression(ListExpression listExpression, Object arg)
			throws Exception {
		if(listExpression.expressionList.size() == 0) {
			listExpression.setType(emptyList);
			return listExpression.getType();
		}
		else
		{
			listExpression.expressionList.get(0).visit(this,arg);
			String type = listExpression.expressionList.get(0).getType();
			for(int i=1; i<listExpression.expressionList.size(); i++)
			{
				listExpression.expressionList.get(i).visit(this,arg);
				if(!listExpression.expressionList.get(i).getType().equals(type))
				{
					throw new TypeCheckException("Incorrect Type!", listExpression);
				}
			}
			listExpression.setType(emptyList + "<" + type + ">;");
			return listExpression.getType();
		}
	}

	/** gets the type from the enclosed expression */
	@Override
	public Object visitListOrMapElemExpression(
			ListOrMapElemExpression listOrMapElemExpression, Object arg)
			throws Exception {
		listOrMapElemExpression.expression.visit(this,arg);
		if(!listOrMapElemExpression.expression.getType().equals(intType))
		{
			throw new TypeCheckException("Incorrect Index Type!",listOrMapElemExpression);
		}
		String ident = listOrMapElemExpression.identToken.getText();
		VarDec d = (VarDec) symbolTable.lookup(ident);
		if( d != null)
		{
			String type = d.type.getJVMType();
			if(type.contains("Ljava/util/List<Ljava/util/List"))
			{
				type = emptyList;
			}
			else if (type.contains("Ljava/lang/Integer;"))
			{
				type = intType;
			}
			else if(type.contains("Ljava/lang/Boolean;"))
			{
				type = booleanType;
			}
			else if (type.contains(stringType))
			{
				type = stringType;
			}
			else {
				throw new TypeCheckException("Wrong Type!!",listOrMapElemExpression);
			}
			listOrMapElemExpression.setType(type);

		}
		else {
			throw new TypeCheckException("Variable is already in scope!",listOrMapElemExpression);
		}
		return null;
	}

	@Override
	public Object visitListType(ListType listType, Object arg) throws Exception {
		listType.type.visit(this, arg);
		return listType.getJVMType();
	}

	@Override
	public Object visitMapListExpression(MapListExpression mapListExpression,
			Object arg) throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Object visitPrintStatement(PrintStatement printStatement, Object arg)
			throws Exception {
		printStatement.expression.visit(this, null);
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		if (arg == null) {
			program.JVMName = program.name;
		} else {
			program.JVMName = arg + "/" + program.name;
		}
		// ignore the import statement
		if (!symbolTable.insert(program.name, null)) {
			throw new TypeCheckException("name already in symbol table",
					program);
		}
		program.block.visit(this, true);
		return null;
	}

	@Override
	public Object visitQualifiedName(QualifiedName qualifiedName, Object arg) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Checks that both expressions have type int.
	 * 
	 * Note that in spite of the name, this is not in the Expression type
	 * hierarchy.
	 */
	@Override
	public Object visitRangeExpression(RangeExpression rangeExpression,
			Object arg) throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	// nothing to do here
	@Override
	public Object visitReturnStatement(ReturnStatement returnStatement,
			Object arg) throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Object visitSimpleType(SimpleType simpleType, Object arg)
			throws Exception {
		return simpleType.getJVMType();
	}

	@Override
	public Object visitSizeExpression(SizeExpression sizeExpression, Object arg)
			throws Exception {
		sizeExpression.expression.visit(this, arg);
		sizeExpression.setType(sizeExpression.expression.getType());
		if(sizeExpression.getType().equals("I")
				|| sizeExpression.getType().equals("Z")
				|| sizeExpression.getType().equals("Ljava/lang/String;"))
		{
			throw new TypeCheckException("Incompatible Types!",sizeExpression);
		}
		sizeExpression.setType("I");
		return sizeExpression.getType();
	}

	@Override
	public Object visitStringLitExpression(
			StringLitExpression stringLitExpression, Object arg)
			throws Exception {
		stringLitExpression.setType(stringType);
		return stringType;
	}

	/**
	 * if ! and boolean, then boolean else if - and int, then int else error
	 */
	@Override
	public Object visitUnaryExpression(UnaryExpression unaryExpression,
			Object arg) throws Exception {
		unaryExpression.expression.visit(this, arg);
		unaryExpression.setType(unaryExpression.expression.getType());
		if(unaryExpression.getType().equals(stringType))
		{
			throw new TypeCheckException("Operation not supported for Strings!", unaryExpression);
		}
		return unaryExpression.getType();
	}

	@Override
	public Object visitUndeclaredType(UndeclaredType undeclaredType, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"undeclared types not supported");
	}

	@Override
	public Object visitValueExpression(ValueExpression valueExpression,
			Object arg) throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	/**
	 * check that this variable has not already been declared in the same scope.
	 */
	@Override
	public Object visitVarDec(VarDec varDec, Object arg) throws Exception {
		//symbolTable.lookup(varDec.identToken)
		varDec.type.visit(this, arg);
		String ident = varDec.identToken.getText();
		varDec.identToken.getText();
		if(symbolTable.lookup(ident) == null) {
			symbolTable.insert(ident,varDec);
		}
		else {
			throw new TypeCheckException("Variable is already in scope!",varDec);
		}

		return varDec.type.getJVMType();
	}

	/**
	 * All checking will be done in the children since grammar ensures that the
	 * rangeExpression is a rangeExpression.
	 */
	@Override
	public Object visitWhileRangeStatement(
			WhileRangeStatement whileRangeStatement, Object arg)
			throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Object visitWhileStarStatement(
			WhileStarStatement whileStarStatement, Object arg) throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg)
			throws Exception {
		whileStatement.expression.visit(this,arg);
		whileStatement.block.visit(this,arg);
		if(whileStatement.expression.getType() != booleanType)
		{
			throw new TypeCheckException("Invalid Boolean statement!",whileStatement);
		}

		return whileStatement.expression.getType();
	}

}
