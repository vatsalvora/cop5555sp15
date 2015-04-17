package cop5555sp15.ast;

import org.objectweb.asm.*;
import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TypeConstants;

import java.util.Arrays;
import java.util.List;

public class CodeGenVisitor implements ASTVisitor, Opcodes, TypeConstants {

	ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
	// Because we used the COMPUTE_FRAMES flag, we do not need to
	// insert the mv.visitFrame calls that you will see in some of the
	// asmifier examples. ASM will insert those for us.
	// FYI, the purpose of those instructions is to provide information
	// about what is on the stack just before each branch target in order
	// to speed up class verification.
	FieldVisitor fv;
	String className;
	String classDescriptor;

	// This class holds all attributes that need to be passed downwards as the
	// AST is traversed. Initially, it only holds the current MethodVisitor.
	// Later, we may add more attributes.
	static class InheritedAttributes {
		public InheritedAttributes(MethodVisitor mv) {
			super();
			this.mv = mv;
		}

		MethodVisitor mv;
	}

	@Override
	public Object visitAssignmentStatement(
			AssignmentStatement assignmentStatement, Object arg)
			throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		mv.visitVarInsn(ALOAD, 0);
		assignmentStatement.expression.visit(this, arg);
		assignmentStatement.lvalue.visit(this, arg);


		return null;
	}


	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression,
			Object arg) throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		binaryExpression.expression0.visit(this, arg);
		binaryExpression.expression1.visit(this, arg);
		Kind op = binaryExpression.op.kind;
		switch(op){
			case PLUS:
				if(binaryExpression.getType() == intType) {
					mv.visitInsn(IADD);
				}
				if(binaryExpression.getType() == stringType) {
					mv.visitVarInsn(ASTORE, 2);
					mv.visitVarInsn(ASTORE, 1);
					mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
					mv.visitInsn(DUP);
					mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
					mv.visitVarInsn(ALOAD, 2);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
				}
				break;
			case MINUS:
				if(binaryExpression.getType() == intType)
				{
					mv.visitInsn(ISUB);
				}
				break;
			case TIMES:
				if(binaryExpression.getType() == intType)
				{
					mv.visitInsn(IMUL);
				}
				break;
			case DIV:
				if(binaryExpression.getType() == intType)
				{
					mv.visitInsn(IDIV);
				}
				break;
			case AND:
				if(binaryExpression.getType() == booleanType) {
					mv.visitInsn(IAND);
				}
				break;
			case BAR: // OR
				if(binaryExpression.getType() == booleanType) {
					mv.visitInsn(IOR);
				}
				break;
			case EQUAL:
				if(binaryExpression.expression0.getType() == stringType)
				{
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);

				}
				else {
					Label L1 = new Label();
					mv.visitJumpInsn(IF_ICMPEQ, L1);
					mv.visitLdcInsn(false);
					Label L2 = new Label();
					mv.visitJumpInsn(GOTO, L2);
					mv.visitLabel(L1);
					mv.visitLdcInsn(true);
					mv.visitLabel(L2);
				}
				break;
			case NOTEQUAL:
				if(binaryExpression.expression0.getType() == stringType)
				{
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
					Label L2 = new Label();
					mv.visitJumpInsn(IFEQ, L2);
					mv.visitLdcInsn(false);
					Label L3 = new Label();
					mv.visitJumpInsn(GOTO, L3);
					mv.visitLabel(L2);
					mv.visitLdcInsn(true);
					mv.visitLabel(L3);
				}
				else
				{
					Label L3 = new Label();
					mv.visitJumpInsn(IF_ICMPNE, L3);
					mv.visitLdcInsn(false);
					Label L4 = new Label();
					mv.visitJumpInsn(GOTO, L4);
					mv.visitLabel(L3);
					mv.visitLdcInsn(true);
					mv.visitLabel(L4);
				}
				break;
			case GE:
				if(binaryExpression.expression0.getType() == stringType) {

				}
				else {
					Label L1 = new Label();
					mv.visitJumpInsn(IF_ICMPGE, L1);
					mv.visitLdcInsn(false);
					Label L2 = new Label();
					mv.visitJumpInsn(GOTO, L2);
					mv.visitLabel(L1);
					mv.visitLdcInsn(true);
					mv.visitLabel(L2);
				}
				break;
			case GT:
				if(binaryExpression.expression0.getType() == stringType) {

				}
				else {
					Label L3 = new Label();
					mv.visitJumpInsn(IF_ICMPGT, L3);
					mv.visitLdcInsn(false);
					Label L4 = new Label();
					mv.visitJumpInsn(GOTO, L4);
					mv.visitLabel(L3);
					mv.visitLdcInsn(true);
					mv.visitLabel(L4);
				}
				break;
			case LE:
				if(binaryExpression.expression0.getType() == stringType) {

				}
				else {
					Label L1 = new Label();
					mv.visitJumpInsn(IF_ICMPLE, L1);
					mv.visitLdcInsn(false);
					Label L2 = new Label();
					mv.visitJumpInsn(GOTO, L2);
					mv.visitLabel(L1);
					mv.visitLdcInsn(true);
					mv.visitLabel(L2);
				}
				break;
			case LT:
				if(binaryExpression.expression0.getType() == stringType) {

				}
				else {
					Label L3 = new Label();
					mv.visitJumpInsn(IF_ICMPLT, L3);
					mv.visitLdcInsn(false);
					Label L4 = new Label();
					mv.visitJumpInsn(GOTO, L4);
					mv.visitLabel(L3);
					mv.visitLdcInsn(true);
					mv.visitLabel(L4);
				}
				break;
			default:
				break;
		}
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		for (BlockElem elem : block.elems) {
			elem.visit(this, arg);
		}
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(
			BooleanLitExpression booleanLitExpression, Object arg)
			throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		mv.visitLdcInsn(booleanLitExpression.value);
		return null;
	}

	@Override
	public Object visitClosure(Closure closure, Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitClosureDec(ClosureDec closureDeclaration, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitClosureEvalExpression(
			ClosureEvalExpression closureExpression, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitClosureExpression(ClosureExpression closureExpression,
			Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitExpressionLValue(ExpressionLValue expressionLValue,
			Object arg) throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		String ident = expressionLValue.identToken.getText();
		String type = expressionLValue.getType();
		if(type.contains(intType) || type.contains(booleanType))
		{
			mv.visitVarInsn(ISTORE, 1);
		}
		else {
			mv.visitVarInsn(ASTORE, 1);
		}
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, className, ident, "Ljava/util/List;");
		mv.visitInsn(DUP);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ASTORE, 3);
		Label L0 = new Label();
		mv.visitLabel(L0);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I", true);
		expressionLValue.expression.visit(this, arg);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ISTORE, 2);
		//Compare if index is larger than size
		Label L1 = new Label();
		mv.visitJumpInsn(IF_ICMPGT, L1);
		//Increase size of List
		mv.visitVarInsn(ALOAD, 3);
		if(type.contains(intType) || type.contains(booleanType)) {
			mv.visitVarInsn(ILOAD, 1);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ISTORE, 1);
		}
		else {
			mv.visitVarInsn(ALOAD, 1);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ASTORE, 1);
		}
		if(type.contains(intType)) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		}
		else if (type.contains(booleanType))
		{
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
		}
		else if(type.contains(stringType))
		{
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
		}
		else {
			throw new UnsupportedOperationException(
					"Unsupported Type!!");
		}
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
		mv.visitInsn(POP);
		mv.visitJumpInsn(GOTO,L0);
		mv.visitLabel(L1);

		mv.visitVarInsn(ILOAD, 2);
		if(type.contains(intType) || type.contains(booleanType))
		{
			mv.visitVarInsn(ILOAD, 1);
		}
		else {
			mv.visitVarInsn(ALOAD, 1);
		}
		if(type.contains(stringType))
		{
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
		}
		else if (type.contains(booleanType))
		{
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
		}
		else if((type.contains(intType)))
		{
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		}

		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "set", "(ILjava/lang/Object;)Ljava/lang/Object;", true);
		mv.visitInsn(POP);
//		mv.visitLabel(L2);
		mv.visitFieldInsn(PUTFIELD,className,ident,"Ljava/util/List;");
		return null;
	}

	@Override
	public Object visitExpressionStatement(
			ExpressionStatement expressionStatement, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression,
			Object arg) throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		mv.visitVarInsn(ALOAD, 0);
		String ident = identExpression.identToken.getText();
		if(identExpression.getType().contains("List")) {
			mv.visitFieldInsn(GETFIELD, className, ident, "Ljava/util/List;");
		}else mv.visitFieldInsn(GETFIELD, className, ident, identExpression.getType());
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identLValue, Object arg)
			throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		String ident = identLValue.firstToken.getText();
		if(identLValue.getType().contains("Ljava/util/List"))
		{
			mv.visitFieldInsn(PUTFIELD,className,ident,"Ljava/util/List;");
		}
		else {
			mv.visitFieldInsn(PUTFIELD,className,ident,identLValue.getType());
		}
		return null;
	}

	@Override
	public Object visitIfElseStatement(IfElseStatement ifElseStatement,
			Object arg) throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		ifElseStatement.expression.visit(this, arg);
		Label L2 = new Label();
		mv.visitJumpInsn(IFEQ, L2);
		ifElseStatement.ifBlock.visit(this,arg);
		Label L3 = new Label();
		mv.visitJumpInsn(GOTO, L3);
		mv.visitLabel(L2);
		ifElseStatement.elseBlock.visit(this, arg);
		mv.visitLabel(L3);
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg)
			throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		ifStatement.expression.visit(this, arg);
		Label L2 = new Label();
		mv.visitJumpInsn(IFEQ, L2);
		ifStatement.block.visit(this, arg);
		Label L3 = new Label();
		mv.visitJumpInsn(GOTO, L3);
		mv.visitLabel(L2);
		mv.visitLabel(L3);
		return null;

	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression,
			Object arg) throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv; // this should be the
															// first statement
															// of all visit
															// methods that
															// generate
															// instructions
		mv.visitLdcInsn(intLitExpression.value);
		return null;
	}

	@Override
	public Object visitKeyExpression(KeyExpression keyExpression, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitKeyValueExpression(
			KeyValueExpression keyValueExpression, Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitKeyValueType(KeyValueType keyValueType, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitListExpression(ListExpression listExpression, Object arg)
			throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		String type = listExpression.getType();
		mv.visitTypeInsn(NEW, "java/util/ArrayList");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
		List<Expression> expressions = listExpression.expressionList;
		for(int i=0; i<expressions.size(); i++)
		{
			mv.visitInsn(DUP);
			Expression exp = expressions.get(i);
			exp.visit(this, arg);
			if(exp.getType().contains("Ljava/util/List<"))
			{
				System.out.println(exp);
				System.out.println(exp.getType());
				//
			}
			else if (exp.getType().equals(intType)) {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			}
			else if (exp.getType().equals(booleanType))
			{
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
			}
			else if (exp.getType().equals(stringType))
			{
				//No Value of
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
			}
			else {
				throw new UnsupportedOperationException(
						"Unsupported Type!!");
			}
			mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
			mv.visitInsn(POP);
		}

		return null;
	}

	@Override
	public Object visitListOrMapElemExpression(
			ListOrMapElemExpression listOrMapElemExpression, Object arg)
			throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		String ident = listOrMapElemExpression.identToken.getText();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, className, ident, "Ljava/util/List;");
		String type = listOrMapElemExpression.getType();
		String index = listOrMapElemExpression.expression.firstToken.getText();
		int i = Integer.parseInt(index);
		mv.visitLdcInsn(i);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;", true);
		if(type.equals("I")) {
			mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
		}
		else if (type.equals("Z"))
		{
			mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
		}
		else if(type.equals("Ljava/lang/String;"))
		{
			//No Value of
			mv.visitTypeInsn(CHECKCAST, "java/lang/String");
		}
		return null;
	}

	@Override
	public Object visitListType(ListType listType, Object arg) throws Exception {
		String[] args = {ListType.prefix(),listType.getJVMType()};
		return args;
	}

	@Override
	public Object visitMapListExpression(MapListExpression mapListExpression,
			Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitPrintStatement(PrintStatement printStatement, Object arg)
			throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(printStatement.firstToken.getLineNumber(), l0);
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
				"Ljava/io/PrintStream;");
		printStatement.expression.visit(this, arg); // adds code to leave value
													// of expression on top of
													// stack.
													// Unless there is a good
													// reason to do otherwise,
													// pass arg down the tree
		String etype = printStatement.expression.getType();
		if (etype.equals("I") || etype.equals("Z")
				|| etype.equals("Ljava/lang/String;")) {
			String desc = "(" + etype + ")V";
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
					desc, false);
		} else {
			String listDesc = "(Ljava/lang/Object;)V";
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
					listDesc, false);
		}
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		className = program.JVMName;
		classDescriptor = 'L' + className + ';';
		cw.visit(52, // version
				ACC_PUBLIC + ACC_SUPER, // access codes
				className, // fully qualified classname
				null, // signature
				"java/lang/Object", // superclass
				new String[] { "cop5555sp15/Codelet" } // implemented interfaces
		);
		cw.visitSource(null, null); // maybe replace first argument with source
									// file name

		// create init method
		{
			MethodVisitor mv;
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(3, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>",
					"()V", false);
			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", classDescriptor, null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}

		// generate the execute method
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "execute", // name of top
																	// level
																	// method
				"()V", // descriptor: this method is parameterless with no
						// return value
				null, // signature.  This is null for us, it has to do with generic types
				null // array of strings containing exceptions
				);
		mv.visitCode();
		Label lbeg = new Label();
		mv.visitLabel(lbeg);
		mv.visitLineNumber(program.firstToken.lineNumber, lbeg);
		program.block.visit(this, new InheritedAttributes(mv));
		mv.visitInsn(RETURN);
		Label lend = new Label();
		mv.visitLabel(lend);
		mv.visitLocalVariable("this", classDescriptor, null, lbeg, lend, 0);
		mv.visitMaxs(0, 0);  //this is required just before the end of a method. 
		                     //It causes asm to calculate information about the
		                     //stack usage of this method.
		mv.visitEnd();

		
		cw.visitEnd();
		return cw.toByteArray();
	}

	@Override
	public Object visitQualifiedName(QualifiedName qualifiedName, Object arg) {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitRangeExpression(RangeExpression rangeExpression,
			Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitReturnStatement(ReturnStatement returnStatement,
			Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitSimpleType(SimpleType simpleType, Object arg)
			throws Exception {
		String [] args = {simpleType.getJVMType()};
		return args;
	}

	@Override
	public Object visitSizeExpression(SizeExpression sizeExpression, Object arg)
			throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		String ident = sizeExpression.expression.firstToken.getText();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, className, ident, "Ljava/util/List;");
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I", true);
		return null;
	}

	@Override
	public Object visitStringLitExpression(
			StringLitExpression stringLitExpression, Object arg)
			throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		mv.visitLdcInsn(stringLitExpression.value);
		return null;
	}

	@Override
	public Object visitUnaryExpression(UnaryExpression unaryExpression,
			Object arg) throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		unaryExpression.expression.visit(this,arg);
		if(unaryExpression.getType().equals("I")) {
			mv.visitInsn(INEG);
		}
		else if(unaryExpression.getType().equals("Z"))
		{
			Label L2 = new Label();
			mv.visitJumpInsn(IFEQ, L2);
			mv.visitLdcInsn(false);
			Label L3 = new Label();
			mv.visitJumpInsn(GOTO, L3);
			mv.visitLabel(L2);
			mv.visitLdcInsn(true);
			mv.visitLabel(L3);
		}
		return null;
	}

	@Override
	public Object visitValueExpression(ValueExpression valueExpression,
			Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitVarDec(VarDec varDec, Object arg) throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		String [] args = (String[])varDec.type.visit(this,arg);
		if(args.length <2 ) {
			fv = cw.visitField(0, varDec.identToken.getText(), args[0], null, null);
		}
		else {
			fv = cw.visitField(0, varDec.identToken.getText(), args[0]+";", args[1], null);
		}
		fv.visitEnd();
		return null;
	}

	@Override
	public Object visitWhileRangeStatement(
			WhileRangeStatement whileRangeStatement, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitWhileStarStatement(WhileStarStatement whileStarStatment,
			Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg)
			throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		Label L2 = new Label();
		Label L4 = new Label();
		mv.visitLabel(L4);
		whileStatement.expression.visit(this, arg);
		mv.visitJumpInsn(IFEQ, L2);
		whileStatement.block.visit(this, arg);
		mv.visitJumpInsn(GOTO, L4);
		mv.visitLabel(L2);
		return null;
	}

	@Override
	public Object visitUndeclaredType(UndeclaredType undeclaredType, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

}
