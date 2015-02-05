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
    static final Kind[] Statement_Kinds = {IDENT,KW_WHILE,KW_IF,MOD,KW_RETURN};
    static final Kind[] Statement_PredictSet = {IDENT,KW_PRINT,KW_WHILE,KW_IF,MOD,KW_RETURN};
    static final Kind[] Factor_Kinds = {INT_LIT,BL_TRUE,BL_FALSE,STRING_LIT};


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
                if (isKind(Statement_Kinds)) {
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

    private void VarDec()  throws SyntaxException {
        if (t.kind == IDENT) {
            match(IDENT);
            if (t.kind == COLON) { // VarDec
                match(COLON);
                Type();
            }
            // else empty
        }
        // else empty
    }

    private void Statement() throws SyntaxException {
        //TODO implement
        if(t.kind == IDENT)
        {
            LValue();
            match(ASSIGN);
            Expression();
        }
        else if(t.kind == KW_PRINT)
        {
            match(KW_PRINT);
            Expression();
        }
        else if(t.kind == KW_WHILE)
        {
            match(KW_WHILE);
            if(t.kind == TIMES){
                match(TIMES);
                match(LPAREN);
                Expression();
                if(t.kind == RANGE) // Range Expression
                {
                    match(RANGE);
                    Expression();
                }
                match(RPAREN);
                Block();
            }
            else
            {
                match(LPAREN);
                Expression();
                match(RPAREN);
                Block();
            }
        }
        else if(t.kind == KW_IF)
        {
            match(KW_IF);
            match(LPAREN);
            Expression();
            match(RPAREN);
            Block();
            if(t.kind == KW_ELSE)
            {
                match(KW_ELSE);
                Block();
            }
        }
        else if(t.kind == MOD)
        {
            match(MOD);
            Expression();
        }
        else if(t.kind == KW_RETURN)
        {
            match(KW_RETURN);
            Expression();
        }
    }

    private void Expression() throws SyntaxException{
        //TODO implement
        Term();
        while(isKind(REL_OPS))
        {
            RelOP();
            Term();
        }
    }

    private void RelOP() throws SyntaxException {
        match(REL_OPS);
    }

    private void Elem() throws SyntaxException {
        Thing();
        while(isKind(STRONG_OPS))
        {
            StrongOp();
            Thing();
        }
    }

    private void StrongOp() throws SyntaxException {
        match(STRONG_OPS);
    }

    private void Thing() throws SyntaxException {
        Factor();
        while (isKind(VERY_STRONG_OPS)){
            VeryStrongOp();
            Factor();
        }
    }

    private void VeryStrongOp() throws SyntaxException {
        match(VERY_STRONG_OPS);
    }

    private void Factor() throws SyntaxException {
        if(t.kind == IDENT)
        {
            match(IDENT);
            if(t.kind == LSQUARE){
                match(LSQUARE);
                Expression();
                match(RSQUARE);
            }
            else if(t.kind == LPAREN) // ClosureEvalExpression
            {
                match(LPAREN);
                ExpressionList();
                match(RPAREN);
            }
        }
        else if(isKind(Factor_Kinds))
        {
            match(Factor_Kinds);
        }
        else if(t.kind == LPAREN)
        {
            match(LPAREN);
            Expression();
            match(RPAREN);
        }
        else if(t.kind == NOT)
        {
            match(NOT);
            Factor();
        }
        else if(t.kind == KW_SIZE)
        {
            match(KW_SIZE);
            Expression();
        }
        else if(t.kind == KW_KEY)
        {
            match(KW_KEY);
            match(LPAREN);
            Expression();
            match(RPAREN);
        }
        else if(t.kind == KW_VALUE)
        {
            match(KW_VALUE);
            match(LPAREN);
            Expression();
            match(RPAREN);
        }
        else if(t.kind == LCURLY)
        {
            Closure();
        }
        else if(t.kind == AT) // List
        {
            match(AT);
            if(t.kind == AT) //MapList
            {
                match(AT);
                match(LSQUARE);
                KeyValueList();
                match(RSQUARE);
            }
            else {
                match(LSQUARE);
                ExpressionList();
                match(RSQUARE);
            }
        }
    }

    private void KeyValueList() throws SyntaxException {
        if(t.kind != RSQUARE)
        {
            KeyValueExpression();
            while(t.kind != RSQUARE)
            {
                match(COMMA);
                KeyValueExpression();
            }
        }
    }

    private void KeyValueExpression() throws SyntaxException {
        Expression();
        match(COLON);
        Expression();
    }

    private void ExpressionList() throws SyntaxException {
        if(t.kind != RPAREN)
        {
            Expression();
            while(t.kind != RPAREN){
                match(COMMA);
                Expression();
            }
        }
    }

    private void WeakOp() throws SyntaxException {
        match(WEAK_OPS);
    }

    private void Term() throws SyntaxException {
        Elem();
        while (isKind(WEAK_OPS))
        {
            WeakOp();
            Elem();
        }
    }

    private void LValue() throws SyntaxException {
        match(IDENT);
        if(t.kind == LSQUARE)
        {
            match(LSQUARE);
            Expression();
            match(RSQUARE);
        }
    }

    private void Closure() throws SyntaxException {
        match(LCURLY);
        if(t.kind != ARROW) {
            VarDec();
        }
        while(t.kind != ARROW) {
            if (t.kind == COMMA) {
                match(COMMA);
                VarDec();
            }
        }
        match(ARROW);
        while(isKind(Statement_PredictSet)){
            Statement();
            match(SEMICOLON);
        }
        match(RCURLY);
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
