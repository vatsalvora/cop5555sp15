package cop5555sp15.ast;

import org.objectweb.asm.*;
import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TypeConstants;

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
		String ident = assignmentStatement.lvalue.firstToken.getText();
		mv.visitVarInsn(ALOAD, 0);
		assignmentStatement.expression.visit(this, arg);
		mv.visitFieldInsn(PUTFIELD,className,ident,assignmentStatement.lvalue.getType());
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
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
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
		mv.visitFieldInsn(GETFIELD,className,ident,identExpression.getType());
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identLValue, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitIfElseStatement(IfElseStatement ifElseStatement,
			Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
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
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitListOrMapElemExpression(
			ListOrMapElemExpression listOrMapElemExpression, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitListType(ListType listType, Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
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
		} else
			throw new UnsupportedOperationException(
					"printing list or map not yet implemented");
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
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitSizeExpression(SizeExpression sizeExpression, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
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
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
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
		fv = cw.visitField(0, varDec.identToken.getText(), varDec.type.getJVMType(), null, null);
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
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitUndeclaredType(UndeclaredType undeclaredType, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

}
