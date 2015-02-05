package cop5555sp15;

import static cop5555sp15.TokenStream.Kind.*;
import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;

public class SimpleParser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;
		Kind[] expected;
		String msg;

		SyntaxException(Token t, Kind expected) {
			this.t = t;
			msg = "";
			this.expected = new Kind[1];
			this.expected[0] = expected;

		}

		public SyntaxException(Token t, String msg) {
			this.t = t;
			this.msg = msg;
            this.expected = new Kind[0];
		}

		public SyntaxException(Token t, Kind[] expected) {
			this.t = t;
			msg = "";
			this.expected = expected;
		}

		public String getMessage() {
			StringBuilder sb = new StringBuilder();
			sb.append(" error at token ").append(t.toString()).append(" ")
					.append(msg);
			sb.append(". Expected: ");
			for (Kind kind : expected) {
				sb.append(kind).append(" ");
			}
			return sb.toString();
		}
	}

	TokenStream tokens;
	Token t;

	SimpleParser(TokenStream tokens) {
		this.tokens = tokens;
		t = tokens.nextToken();
	}

	private Kind match(Kind kind) throws SyntaxException {
		if (isKind(kind)) {
			consume();
			return kind;
		}
		throw new SyntaxException(t, kind);
	}

	private Kind match(Kind... kinds) throws SyntaxException {
		Kind kind = t.kind;
		if (isKind(kinds)) {
			consume();
			return kind;
		}
		StringBuilder sb = new StringBuilder();
		for (Kind kind1 : kinds) {
			sb.append(kind1).append(" ");
		}
		throw new SyntaxException(t, "expected one of " + sb.toString());
	}

	private boolean isKind(Kind kind) {
		return (t.kind == kind);
	}

	private void consume() {
		if (t.kind != EOF)
			t = tokens.nextToken();
	}

	private boolean isKind(Kind... kinds) {
		for (Kind kind : kinds) {
			if (t.kind == kind)
				return true;
		}
		return false;
	}

	//This is a convenient way to represent fixed sets of
	//token kinds.  You can pass these to isKind.
	static final Kind[] REL_OPS = { BAR, AND, EQUAL, NOTEQUAL, LT, GT, LE, GE };
	static final Kind[] WEAK_OPS = { PLUS, MINUS };
	static final Kind[] STRONG_OPS = { TIMES, DIV };
	static final Kind[] VERY_STRONG_OPS = { LSHIFT, RSHIFT };
    static final Kind[] Type_Poss = {KW_INT,KW_BOOLEAN,KW_STRING,AT};
    static final Kind[] Simple_Type = {KW_INT,KW_BOOLEAN,KW_STRING};


	public void parse() throws SyntaxException {
		Program();
		match(EOF);
	}

	private void Program() throws SyntaxException {
		ImportList();
		match(KW_CLASS);
		match(IDENT);
		Block();
	}

	private void ImportList() throws SyntaxException {
        while(t.kind != KW_CLASS) {
            match(KW_IMPORT);

            match(IDENT);

            while (t.kind != SEMICOLON) {
                match(DOT);
                match(IDENT);
            }

            match(SEMICOLON);
        }

	}

	private void Block() throws SyntaxException {
		match(LCURLY);
		while(t.kind != RCURLY)
        {
            if(t.kind == KW_DEF) {
                match(KW_DEF);
                if (t.kind == IDENT) {
                    match(IDENT);
                    if (t.kind == COLON) { // VarDec
                        match(COLON);
                        Type();
                    } else if (t.kind == ASSIGN) { // ClosureDec
                        match(ASSIGN);
                        Closure();
                    }
                    match(SEMICOLON);
                }
            }
            else {
                if (t.kind == IDENT || t.kind == KW_WHILE || t.kind == KW_IF || t.kind == MOD
                        || t.kind == KW_RETURN) {
                    Statement();
                    match(SEMICOLON);
                    break;
                } else {
                    match(SEMICOLON);
                }
            }
        }
		match(RCURLY);
	}

    private void Statement() {
        //TODO implement
    }

    private void Closure() {
        //TODO implement
    }

    private void Type() throws SyntaxException{
        match(Type_Poss);
        if(t.kind == AT)
        {
            match(AT);
            match(LSQUARE);
            match(Simple_Type);
            match(COLON);
            Type();
            match(RSQUARE);
        }
        else if(t.kind == LSQUARE)
        {
            match(LSQUARE);
            Type();
            match(RSQUARE);
        }
    }



}
