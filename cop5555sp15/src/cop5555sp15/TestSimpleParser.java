package cop5555sp15;

import static org.junit.Assert.*;

import org.junit.Test;
import cop5555sp15.SimpleParser.SyntaxException;
import cop5555sp15.TokenStream.Kind;
import static cop5555sp15.TokenStream.Kind.*;

public class TestSimpleParser {

	
	private void parseIncorrectInput(String input,Kind ExpectedIncorrectTokenKind) {
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		SimpleParser parser = new SimpleParser(stream);
		System.out.println(stream);
		try {
			parser.parse();
			fail("expected syntax error");
		} catch (SyntaxException e) {
			assertEquals(ExpectedIncorrectTokenKind, e.t.kind); // class is the incorrect token
		}
	}
	
	private void parseCorrectInput(String input) throws SyntaxException {
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		System.out.println(stream);
		SimpleParser parser = new SimpleParser(stream);
		parser.parse();
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
	public void import1() throws SyntaxException {
		System.out.println("import1");
		String input = "import X; class A { } ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void import2() throws SyntaxException {
		System.out.println("import2");
		String input = "import X.Y.Z; import W.X.Y; class A { } ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void import3() throws SyntaxException {
		System.out.println("import2");
		String input = "import class A { } "; // this input is wrong.
		System.out.println(input);
		Kind ExpectedIncorrectTokenKind = KW_CLASS;
		parseIncorrectInput(input, ExpectedIncorrectTokenKind);
	}

    @Test
    public void empty_Vardec() throws SyntaxException {
        System.out.println("empty_Vardec");
        String input = "class A {def B;} ";
        System.out.println(input);
        parseCorrectInput(input);
    }

	@Test
	public void def_simple_type1() throws SyntaxException {
		System.out.println("def_simple_type1");
		String input = "class A {def B:int; def C:boolean; def S: string;} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void def_simple_type2() throws SyntaxException {
		System.out.println("def_simple_type2");
		String input = "class A {def B:int; def C:boolean; def S: string} ";
		System.out.println(input);
		parseIncorrectInput(input, RCURLY);
	}
	
	@Test
	public void def_key_value_type1() throws SyntaxException {
		System.out.println("def_key_value_type1");
		String input = "class A {def C:@[string]; def S:@@[int:boolean];} ";
		System.out.println(input);
		parseCorrectInput(input);
	}	
	
	@Test
	public void def_key_value_type2() throws SyntaxException {
		System.out.println("def_key_value_type1");
		String input = "class A {def C:@[string]; def S:@@[int:@@[string:boolean]];} ";
		System.out.println(input);
		parseCorrectInput(input);
	}	
	
	@Test
	public void def_closure1() throws SyntaxException {
		System.out.println("def_closure1");
		String input = "class A {def C={->};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}

	@Test
	public void def_closure2() throws SyntaxException {
		System.out.println("def_closure2");
		String input = "class A {def C={->};  def z:string;} ";
		System.out.println(input);
		parseCorrectInput(input);
	}
	
	@Test
	public void factor1() throws SyntaxException {
		System.out.println("factor1");
		String input = "class A {def C={->x=y;};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}
	
	
	@Test
	public void factor2() throws SyntaxException {
		System.out.println("factor2");
		String input = "class A {def C={->x=y[z];};  def D={->x=y[1];};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}
	
	@Test
	public void factor3() throws SyntaxException {
		System.out.println("factor3");
		String input = "class A {def C={->x=3;};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}
	
	@Test
	public void factor4() throws SyntaxException {
		System.out.println("factor4");
		String input = "class A {def C={->x=\"hello\";};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}
	
	@Test
	public void factor5() throws SyntaxException {
		System.out.println("factor5");
		String input = "class A {def C={->x=true; z = false;};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}
	
	
	@Test
	public void factor6() throws SyntaxException {
		System.out.println("factor6");
		String input = "class A {def C={->x=-y; z = !y;};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}
	
	@Test
	public void factor7() throws SyntaxException {
		System.out.println("factor7");
		String input = "class A {def C={->x= &y; z = !y;};} ";
		System.out.println(input);
		parseIncorrectInput(input,AND);
	}
	
	@Test
	public void expressions1() throws SyntaxException {
		System.out.println("expressions1");
		String input = "class A {def C={->x=x+1; z = 3-4-5;};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}
	
	@Test
	public void expressions2() throws SyntaxException {
		System.out.println("expressions2");
		String input = "class A {def C={->x=x+1/2*3--4; z = 3-4-5;};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}
	
	@Test
	public void expressions3() throws SyntaxException {
		System.out.println("expressions3");
		String input = "class A {def C={->x=x+1/2*3--4; z = 3-4-5;};} ";
		System.out.println(input);
		parseCorrectInput(input);
	}
	
	@Test
	public void expressions4() throws SyntaxException {
		System.out.println("expressions4");
		String input = "class A {x = a<<b; c = b>>z;} ";
		System.out.println(input);
		parseCorrectInput(input);
	}
	
	@Test
	public void statements1()throws SyntaxException {
		System.out.println("statements1");
		String input = "class A {x = y; z[1] = b; print a+b; print (x+y-z);} ";
		System.out.println(input);
		parseCorrectInput(input);
	}
	
	@Test
	public void statements2()throws SyntaxException {
		System.out.println("statements2");
		String input = "class A  {\n while (x) {};  \n while* (1..4){}; } ";
		System.out.println(input);
		parseCorrectInput(input);
	} 
	
	@Test
	public void statements3()throws SyntaxException {
		System.out.println("statements3");
		String input = "class A  {\n if (x) {};  \n if (y){} else {}; \n if (x) {} else {if (z) {} else {};} ; } ";
		System.out.println(input);
		parseCorrectInput(input);
	} 
	
	@Test
	public void emptyStatement()throws SyntaxException {
		System.out.println("emptyStatement");
		String input = "class A  { ;;; } ";
		System.out.println(input);
		parseCorrectInput(input);
	} 	
	
	@Test
	public void statements4()throws SyntaxException {
		System.out.println("statements4");
		String input = "class A  { %a(1,2,3); } ";
		System.out.println(input);
		parseCorrectInput(input);
	} 	
	
	@Test
	public void statements5()throws SyntaxException {
		System.out.println("statements5");
		String input = "class A  { x = a(1,2,3); } ";
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
	public void list1()throws SyntaxException {
		System.out.println("list1");
		String input = "class A  { \n x = @[a,b,c]; \n y = @[d,e,f]+x; \n } ";
		System.out.println(input);
		parseCorrectInput(input);
	} 	
	
	@Test
	public void maplist1()throws SyntaxException {
		System.out.println("maplist1");
		String input = "class A  { x = @@[x:y]; y = @@[x:y,4:5]; } ";
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
