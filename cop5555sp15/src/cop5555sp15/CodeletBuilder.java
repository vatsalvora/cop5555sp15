package cop5555sp15;
import java.io.*;
import java.lang.reflect.Field;
import java.util.List;

import cop5555sp15.ast.ASTNode;
import cop5555sp15.ast.CodeGenVisitor;
import cop5555sp15.ast.Program;
import cop5555sp15.ast.TypeCheckVisitor;
import cop5555sp15.symbolTable.SymbolTable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class CodeletBuilder {
	public static class DynamicClassLoader extends ClassLoader {
		public DynamicClassLoader(ClassLoader parent) {
			super(parent);
		}

		public Class<?> define(String className, byte[] bytecode) {
			return super.defineClass(className, bytecode, 0, bytecode.length);
		}
	};
	private static ASTNode typeCheckCorrectAST(ASTNode ast) throws Exception {
		SymbolTable symbolTable = new SymbolTable();
		TypeCheckVisitor v = new TypeCheckVisitor(symbolTable);
		try {
			ast.visit(v, null);
		} catch (TypeCheckVisitor.TypeCheckException e) {
			System.out.println(e.getMessage());
			fail("no errors expected");
		}
		return ast;
	}
	public static void dumpBytecode(byte[] bytecode){
		int flags = ClassReader.SKIP_DEBUG;
		ClassReader cr;
		cr = new ClassReader(bytecode);
		cr.accept(new TraceClassVisitor(new PrintWriter(System.out)), flags);
	}
	private static byte[] generateByteCode(ASTNode ast) throws Exception {
		CodeGenVisitor v = new CodeGenVisitor();
		byte[] bytecode = (byte[]) ast.visit(v, null);
		dumpBytecode(bytecode);
		return bytecode;
	}
	private static ASTNode parseCorrectInput(String input) {
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		Parser parser = new Parser(stream);
		ASTNode ast = parser.parse();
		if (ast == null) {
			System.out.println("errors " + parser.getErrors());
		}
		assertNotNull(ast);
		return ast;
	}
	public static Codelet newInstance(String source) throws Exception{
	//TODO
		Program program = (Program) parseCorrectInput(source);
		assertNotNull(program);
		typeCheckCorrectAST(program);
		byte[] bytecode = generateByteCode(program);
		assertNotNull(bytecode);
		DynamicClassLoader loader = new DynamicClassLoader(Thread
				.currentThread().getContextClassLoader());
		String name = program.JVMName;
		Class<?> testClass = loader.define(name, bytecode);
		Codelet codelet = (Codelet) testClass.newInstance();
		return codelet;
	}
	public static Codelet newInstance(File file) throws Exception {
	//TODO
		DynamicClassLoader loader = new DynamicClassLoader(Thread
				.currentThread().getContextClassLoader());
		String name = file.getName();
		byte[] bytecode = new byte[(int) file.length()];
		FileInputStream fileInputStream = new FileInputStream(file);
		fileInputStream.read(bytecode);
		Class<?> testClass = loader.define(name, bytecode);
		Codelet codelet = (Codelet) testClass.newInstance();
	//	codelet.execute();
		return codelet;
	}
	@SuppressWarnings("rawtypes")
	public static List getList(Codelet codelet, String name) throws Exception{
	//TODO
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field f = codeletClass.getDeclaredField(name);
		f.setAccessible(true);
		List l = (List) f.get(codelet);
		return l;
	}
	public static int getInt(Codelet codelet, String name) throws Exception{
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field f = codeletClass.getDeclaredField(name);
		f.setAccessible(true);
		int i = (Integer) f.get(codelet);
		return i;

	}
	public static void setInt(Codelet codelet, String name, int value) throws
	Exception{
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field f = codeletClass.getDeclaredField(name);
		f.setAccessible(true);
		f.set(codelet, value);

	}
	public static String getString(Codelet codelet, String name) throws Exception{
	//TODO
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field f = codeletClass.getDeclaredField(name);
		f.setAccessible(true);
		String s = (String) f.get(codelet);
		return s;
	}
	public static void setString(Codelet codelet, String name, String value)
	throws Exception{
	//TODO
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field f = codeletClass.getDeclaredField(name);
		f.setAccessible(true);
		f.set(codelet, value);
	}
	public static boolean getBoolean(Codelet codelet, String name) throws
	Exception{
	//TODO
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field f = codeletClass.getDeclaredField(name);
		f.setAccessible(true);
		Boolean b = (Boolean) f.get(codelet);
		return b;
	}
	public static void setBoolean(Codelet codelet, String name, boolean value)
	throws Exception{
	//TODO
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field f = codeletClass.getDeclaredField(name);
		f.setAccessible(true);
		f.set(codelet, value);
	}
}