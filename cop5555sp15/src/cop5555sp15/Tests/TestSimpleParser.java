package cop5555sp15.Tests;

import static org.junit.Assert.*;

import cop5555sp15.Scanner;
import cop5555sp15.TokenStream;
import org.junit.Rule;
import org.junit.Test;
import cop5555sp15.Tests.SimpleParser.SyntaxException;
import cop5555sp15.TokenStream.Kind;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

import static cop5555sp15.TokenStream.Kind.*;

public class TestSimpleParser {


    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public Timeout globalTimeout= new Timeout(1000);

    private void parseIncorrectInput(String input,
                                     Kind ExpectedIncorrectTokenKind) {
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        SimpleParser parser = new SimpleParser(stream);
        try {
            parser.parse();
            fail(); // should have thrown an exception
        } catch (SyntaxException e) {
            assertEquals(ExpectedIncorrectTokenKind, e.t.kind); // class is the incorrect token
        }
    }

    private void parseCorrectInput(String input) throws SyntaxException {
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();
    }


    @Test
    public void smallest() throws SyntaxException {
        String input = "class A { } ";
        parseCorrectInput(input);
    }

    @Test
    public void import1() throws SyntaxException {
        String input = "import X; class A { } ";
        parseCorrectInput(input);
    }

    @Test
    public void import2() throws SyntaxException {
        String input = "import X.Y.Z; import W.X.Y; class A { } ";
        parseCorrectInput(input);
    }

    @Test
    public void import3() throws SyntaxException {
        String input = "import class A { } "; // this input is wrong.
        Kind ExpectedIncorrectTokenKind = KW_CLASS;
        parseIncorrectInput(input, ExpectedIncorrectTokenKind);

    }

    @Test
    public void import4() throws SyntaxException {
        String input = "import X class A { } "; // this input is wrong.
        Kind ExpectedIncorrectTokenKind = KW_CLASS;
        parseIncorrectInput(input, ExpectedIncorrectTokenKind);

    }



    @Test
    public void def_simple_type1() throws SyntaxException {
        String input = "class A {def B:int; def C:boolean; def S: string;} ";
        parseCorrectInput(input);
    }

    @Test
    public void def_simple_type2() throws SyntaxException {
        String input = "class A {def B:int; def C:boolean; def S: string} ";
        parseIncorrectInput(input, RCURLY);
    }

    @Test
    public void def_key_value_type1() throws SyntaxException {
        String input = "class A {def C:@[string]; def S:@@[int:boolean];} ";
        parseCorrectInput(input);
    }

    @Test
    public void def_key_value_type2() throws SyntaxException {
        String input = "class A {def C:@[string]; def S:@@[int:@@[string:boolean]];} ";
        parseCorrectInput(input);
    }

    @Test
    public void def_key_value_type3() throws SyntaxException {
        String input = "class A {def C:@[@[int]]; def S:@@[int:@@[string:@[int]]];} ";
        parseCorrectInput(input);
    }

    @Test
    public void def_key_value_type4() throws SyntaxException {
        String input = "class A {def C; def S:@@[int:@[int]];} ";
        parseCorrectInput(input);
    }

    @Test
    public void def_key_value_type5() throws SyntaxException {
        String input = "class A { def S:@[int:string];} ";
        parseIncorrectInput(input, COLON);
    }

    @Test
    public void def_key_value_type6() throws SyntaxException {
        String input = "class A { def S:@@[int];} ";
        parseIncorrectInput(input, RSQUARE);
    }

    @Test
    public void def_closure1() throws SyntaxException {
        String input = "class A {def C={->};} ";
        parseCorrectInput(input);
    }

    @Test
    public void def_closure2() throws SyntaxException {
        String input = "class A {def C={->x=1;};  def z:string;} ";
        parseCorrectInput(input);
    }

    @Test
    public void def_closure3() throws SyntaxException {
        String input = "class A {def C={s:string,i:int->};} ";
        parseCorrectInput(input);
    }

    @Test
    public void def_closure4() throws SyntaxException {
        String input = "class A {def C={s:string,i:int->x=1;};} ";
        parseCorrectInput(input);
    }

    @Test
    public void def_closure5() throws SyntaxException {
        String input = "class A {def C={s:@@[string:string],i:@[int]->x=1;};} ";
        parseCorrectInput(input);
    }

    @Test
    public void factor1() throws SyntaxException {
        String input = "class A {def C={->x=y;};} ";
        parseCorrectInput(input);
    }


    @Test
    public void factor2() throws SyntaxException {
        String input = "class A {def C={->x=y[z];};  def D={->x=y[1];};} ";
        parseCorrectInput(input);
    }

    @Test
    public void factor3() throws SyntaxException {
        String input = "class A {def C={->x=3;};} ";
        parseCorrectInput(input);
    }

    @Test
    public void factor4() throws SyntaxException {
        String input = "class A {def C={->x=\"hello\";};} ";
        parseCorrectInput(input);
    }

    @Test
    public void factor5() throws SyntaxException {
        String input = "class A {def C={->x=true; z = false;};} ";
        parseCorrectInput(input);
    }


    @Test
    public void factor6() throws SyntaxException {
        String input = "class A {def C={->x=-y; z = !y;};} ";
        parseCorrectInput(input);
    }

    @Test
    public void factor7() throws SyntaxException {
        String input = "class A {def C={->x= &y; z = !y;};} ";
        parseIncorrectInput(input,AND);
    }

    @Test
    public void expressions1() throws SyntaxException {
        String input = "class A {def C={->x=x+1; z = 3-4-5;};} ";
        parseCorrectInput(input);
    }

    @Test
    public void expressions2() throws SyntaxException {
        String input = "class A {def C={->x=x+1/2*3--4; z = 3-4-5;};} ";
        parseCorrectInput(input);
    }

    @Test
    public void expressions3() throws SyntaxException {
        String input = "class A {def C={->x=x+1/2*3-!4; z = 3-(4-5);};} ";
        parseCorrectInput(input);
    }

    @Test
    public void expressions4() throws SyntaxException {
        String input = "class A {x = a<<b; c = b>>z;} ";
        parseCorrectInput(input);
    }

    @Test
    public void expressions5() throws SyntaxException {
        String input = "class A {x = a<<b+b>>a; c = a/x+!b>>z;} ";
        parseCorrectInput(input);
    }

    @Test
    public void statements1()throws SyntaxException {
        String input = "class A {x = y; z[1] = b; print a+b; print (x+y-z);} ";
        parseCorrectInput(input);
    }

    @Test
    public void statements2()throws SyntaxException {
        String input = "class A  {\n while (x) {};  \n while* (1..4){}; } ";
        parseCorrectInput(input);
    }

    @Test
    public void statements6()throws SyntaxException {
        String input = "class A  {%5; return (a+b+c);} ";
        parseCorrectInput(input);
    }


    @Test
    public void statements3()throws SyntaxException {
        String input = "class A  {\n if (x) {};  \n if (y){} else {}; \n if (x) {} else {if (z) {} else {};} ; } ";
        parseCorrectInput(input);
    }


    @Test
    public void statements8()throws SyntaxException {
        String input = "class A  {\n if (x) { def X={->}; def x:int; x=2; }; }";
        parseCorrectInput(input);
    }

    @Test
    public void statements9()throws SyntaxException {
        String input = "class A  {\n if (x) {} else { def X={->}; def x:int; x=2; }; }";
        parseCorrectInput(input);
    }

    @Test
    public void statements10()throws SyntaxException {
        String input = "class A  {\n if (x) {} else { def X={->}; def x:int; x=2; while* (1..x) { while (x) { return x; }; print aabcc;}; }; }";
        parseCorrectInput(input);
    }



    @Test
    public void emptyStatement()throws SyntaxException {
        String input = "class A  { ;;; } ";
        parseCorrectInput(input);
    }

    @Test
    public void statements4()throws SyntaxException {
        String input = "class A  { %a(1,2,3); } ";
        parseCorrectInput(input);
    }

    @Test
    public void statements5()throws SyntaxException {
        String input = "class A  { x = a(1,2,3); x[z] = a(1,2,3); } ";
        parseCorrectInput(input);
    }

    @Test
    public void testRelOp()throws SyntaxException {
        String input = "class A  { b=a|x; c=a&b|d; c=b==d; c=c!=a<s; d=d<=b>=s; x=q!=d>s; }";
        parseCorrectInput(input);
    }

    @Test
    public void list1()throws SyntaxException {
        String input = "class A  { \n x = @[a,b,c]; \n y = @[d,e,f]+x; \n } ";
        parseCorrectInput(input);
    }

    @Test
    public void list2()throws SyntaxException {
        String input = "class A  {x = @[a+b, !b<<s, {->}]; y=@[ @@[a+b:{s:string->a=b;}]];} ";
        parseCorrectInput(input);
    }

    @Test
    public void maplist1()throws SyntaxException {
        String input = "class A  { x = @@[x:y]; y = @@[x:y,4:5]; z= @@[x:y, {->}:3+a]; a = @@[@[a,b] : @[a(a+b),d]];} ";
        parseCorrectInput(input);
    }

    @Test
    public void factorKeyValSize()throws SyntaxException {
        String input = "class A  { s=key({->}); d=value(a); s=size(a(10)); } ";
        parseCorrectInput(input);
    }

    /**This is an example of testing correct input
	 * Just call parseCorrectInput
	 * @throws SyntaxException
	 */
	@Test
	public void almostEmpty() throws SyntaxException {
		System.out.println("almostEmpty");
		String input = "class A { } ";
		System.out.println(input);
		parseCorrectInput(input);
	}
	
	
	/**This is an example of testing incorrect input.
	 * The second parameter to parseIncorrectInput is
	 * the Kind of the erroneous token.
	 * For example, in this test, the ] should be a },
	 * so the parameter is RSQUARE

	 * @throws SyntaxException
	 */
	@Test
	public void almostEmptyIncorrect() throws SyntaxException {
		System.out.println("almostEmpty");
		String input = "class A { ] ";
		System.out.println(input);
		parseIncorrectInput(input,RSQUARE);		
	}

    @Test
    public void empty_Vardec() throws SyntaxException {
        System.out.println("empty_Vardec");
        String input = "class A {def B;} ";
        System.out.println(input);
        parseCorrectInput(input);
    }


	
	@Test
	public void closureEval()throws SyntaxException {
		System.out.println("closureEva");
		String input = "class A  { x[z] = a(1,2,3); } ";
		System.out.println(input);
		parseCorrectInput(input);
	} 	
	
	@Test
    public void incompleteBlock() throws SyntaxException{
        System.out.println("incompleteBlock");
        String input = "class A  {";
        System.out.println(input);
        parseIncorrectInput(input, EOF);
    }

    @Test
    public void statementComplete()throws SyntaxException {
        System.out.println("statementComplete");
        String input = "import X.Y.Z; class A  { A = true; print A; while(A == true) {%B[C];};" +
                " while*(!A == false){A = A<<2;}; while*(A == 1..2){A = A<<2;};" +
                " if(A > 0){A = A-1;}; if(A > 2){A = A-2;}else{A = A+2;};" +
                " %B[C]; return A;}";
        System.out.println(input);
        parseCorrectInput(input);
    }

    @Test
    public void FactorComplete()throws SyntaxException {
        System.out.println("FactorComplete");
        String input = "import X.Y.Z; class A  { A = key(B); print value(A); A = size(B);" +
                " A={A->print C;}; A=C; A=C[A]; A = 2; A = true; A = false; A = \"Test\";" +
                " A = \"Test\"+\"2\"; A = (1*2); A=!B; A = -A; A = A(A,B[C],@[D]);" +
                " A = @[D]; A = @@[A:A[B],B:false,C:2];" +
                " return A;}";
        System.out.println(input);
        parseCorrectInput(input);
    }

    @Test
    public void incompleteVarDec() throws SyntaxException{
        System.out.println("incompleteVarDec");
        String input = "class A  {def A:}";
        System.out.println(input);
        parseIncorrectInput(input, RCURLY);
    }

    @Test
    public void multipleFormalArgs() throws SyntaxException{
        System.out.println("multipleFormalArgs");
        String input = "class A  {def A={A,B:boolean,C->%B[C];};}";
        System.out.println(input);
        parseCorrectInput(input);
    }

    @Test
    public void allOPS()throws SyntaxException {
        System.out.println("allOPS");
        String input = "import X.Y.Z; class A  { A = key(B)<<B; print value(A)>>C; A = size(B)*B;" +
                " A={A->print C;}; A=C; A=C[A]; A = 2; A = true; A = false; A = \"Test\";" +
                " A = \"Test\"+\"2\"; A = (1*2)/2; A=!B+2; A = -A-1; A = A(A,B[C],@[D])|2;" +
                " A = @[D]&2; A = @@[A:A[B],B:false,C:2]==0; A =0|((B!=0)+2*3); B=C>0; C=B<0;" +
                " D=2*(C>=0); B=(D<=0)/5; return A;}";
        System.out.println(input);
        parseCorrectInput(input);
    }
}
