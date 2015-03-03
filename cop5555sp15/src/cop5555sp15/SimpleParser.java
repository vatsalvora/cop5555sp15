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
    static final Kind[] REL_OPS = {BAR, AND, EQUAL, NOTEQUAL, LT, GT, LE, GE};
    static final Kind[] WEAK_OPS = {PLUS, MINUS};
    static final Kind[] STRONG_OPS = {TIMES, DIV};
    static final Kind[] VERY_STRONG_OPS = {LSHIFT, RSHIFT};
    static final Kind[] Type_Poss = {KW_INT, KW_BOOLEAN, KW_STRING, AT};
    static final Kind[] Simple_Type = {KW_INT, KW_BOOLEAN, KW_STRING};
    static final Kind[] Statement_PredictSet = {IDENT, KW_PRINT, KW_WHILE, KW_IF, MOD, KW_RETURN};
    static final Kind[] Factor_Kinds = {INT_LIT, BL_TRUE, BL_FALSE, STRING_LIT};
    static final Kind[] Factor_Predict_Set = {IDENT, INT_LIT, BL_TRUE, BL_FALSE, STRING_LIT, LPAREN, NOT, MINUS,
            KW_KEY, KW_SIZE, KW_VALUE, LCURLY, AT};


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
        while (!isKind(KW_CLASS)) {
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
        while (!isKind(RCURLY)) {
            if (isKind(KW_DEF)) {
                match(KW_DEF);
                if (isKind(IDENT)) {
                    match(IDENT);
                    if (isKind(COLON)) { // VarDec
                        match(COLON);
                        Type();
                    } else if (isKind(ASSIGN)) { // ClosureDec
                        match(ASSIGN);
                        Closure();
                    }
                    match(SEMICOLON);
                }
            } else {
                if (isKind(Statement_PredictSet)) {
                    Statement();
                }else
                {
                    match(SEMICOLON);
                }
            }
        }
        match(RCURLY);
    }

    private void VarDec() throws SyntaxException {
        if (isKind(IDENT)) {
            match(IDENT);
            if (isKind(COLON)) { // VarDec
                match(COLON);
                Type();
            }
            // else Empty
        }//else Empty
    }

    private void Statement() throws SyntaxException {
        if (isKind(IDENT)) {
            LValue();
            match(ASSIGN);
            Expression();
        } else if (isKind(KW_PRINT)) {
            match(KW_PRINT);
            Expression();
        } else if (isKind(KW_WHILE)) {
            match(KW_WHILE);
            if (isKind(TIMES)) {
                match(TIMES);
                match(LPAREN);
                Expression();
                if (isKind(RANGE)) // Range Expression
                {
                    match(RANGE);
                    Expression();
                }
                match(RPAREN);
                Block();
            } else {
                match(LPAREN);
                Expression();
                match(RPAREN);
                Block();
            }
        } else if (isKind(KW_IF)) {
            match(KW_IF);
            match(LPAREN);
            Expression();
            match(RPAREN);
            Block();
            if (isKind(KW_ELSE)) {
                match(KW_ELSE);
                Block();
            }
        } else if (isKind(MOD)) {
            match(MOD);
            Expression();
        } else if (isKind(KW_RETURN)) {
            match(KW_RETURN);
            Expression();
        }
        match(SEMICOLON);
    }

    private void Expression() throws SyntaxException {
        Term();
        while (isKind(REL_OPS)) {
            RelOP();
            Term();
        }
    }

    private void RelOP() throws SyntaxException {
        match(REL_OPS);
    }

    private void Elem() throws SyntaxException {
        Thing();
        while (isKind(STRONG_OPS)) {
            StrongOp();
            Thing();
        }
    }

    private void StrongOp() throws SyntaxException {
        match(STRONG_OPS);
    }

    private void Thing() throws SyntaxException {
        Factor();
        while (isKind(VERY_STRONG_OPS)) {
            VeryStrongOp();
            Factor();
        }
    }

    private void VeryStrongOp() throws SyntaxException {
        match(VERY_STRONG_OPS);
    }

    private void Factor() throws SyntaxException {
        if (isKind(IDENT)) {
            match(IDENT);
            if (isKind(LSQUARE)) {
                match(LSQUARE);
                Expression();
                match(RSQUARE);
            } else if (isKind(LPAREN)) // ClosureEvalExpression
            {
                match(LPAREN);
                ExpressionList();
                match(RPAREN);
            }
        } else if (isKind(Factor_Kinds)) {
            match(Factor_Kinds);
        } else if (isKind(LPAREN)) {
            match(LPAREN);
            Expression();
            match(RPAREN);
        } else if (isKind(NOT)) {
            match(NOT);
            Factor();
        } else if (isKind(MINUS)) {
            match(MINUS);
            Factor();
        } else if (isKind(KW_SIZE)) {
            match(KW_SIZE);
            Expression();
        } else if (isKind(KW_KEY)) {
            match(KW_KEY);
            match(LPAREN);
            Expression();
            match(RPAREN);
        } else if (isKind(KW_VALUE)) {
            match(KW_VALUE);
            match(LPAREN);
            Expression();
            match(RPAREN);
        } else if (isKind(LCURLY)) {
            Closure();
        } else if (isKind(AT)) // List
        {
            match(AT);
            if (isKind(AT)) //MapList
            {
                match(AT);
                match(LSQUARE);
                KeyValueList();
                match(RSQUARE);
            } else {
                match(LSQUARE);
                ExpressionList();
                match(RSQUARE);
            }
        } else {
            throw new SyntaxException(t, "Not a Factor Kind!!");
        }
    }

    private void KeyValueList() throws SyntaxException {
        if (isKind(Factor_Predict_Set)) {
            KeyValueExpression();
            while (isKind(COMMA)) {
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
        if (isKind(Factor_Predict_Set)) {
            Expression();
            while (isKind(COMMA)) {
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
        while (isKind(WEAK_OPS)) {
            WeakOp();
            Elem();
        }
    }

    private void LValue() throws SyntaxException {
        match(IDENT);
        if (isKind(LSQUARE)) {
            match(LSQUARE);
            Expression();
            match(RSQUARE);
        }
    }

    private void Closure() throws SyntaxException {
        match(LCURLY);
        if (!isKind(ARROW)) {
            VarDec();
        }
        while (isKind(COMMA)) {
            match(COMMA);
            VarDec();
        }
        match(ARROW);
        while (isKind(Statement_PredictSet)) {
            Statement();
        }
        match(RCURLY);
    }

    private void Type() throws SyntaxException {
        match(Type_Poss);
        if (isKind(AT)) {
            match(AT);
            match(LSQUARE);
            match(Simple_Type);
            match(COLON);
            Type();
            match(RSQUARE);
        } else if (isKind(LSQUARE)) {
            match(LSQUARE);
            Type();
            match(RSQUARE);
        }
    }
}
