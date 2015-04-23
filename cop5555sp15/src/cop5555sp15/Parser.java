package cop5555sp15;

import cop5555sp15.ast.*;

import java.util.ArrayList;
import java.util.List;

import static cop5555sp15.TokenStream.Kind.*;
import static cop5555sp15.TokenStream.Kind.LSQUARE;
import static cop5555sp15.TokenStream.Kind.RSQUARE;

/**
 * Created by Vatsal on 2/28/2015.
 */
public class Parser {

    private String errors;

    public List<SyntaxException> getExceptionList() {
        return exceptions;
    }

    public String getErrors() {
        StringBuilder ret = new StringBuilder();
        for(SyntaxException e : exceptions)
        {
            ret.append(e.toString() +"\n");
        }
        return errors;
    }

    @SuppressWarnings("serial")
    public class SyntaxException extends Exception {
        TokenStream.Token t;
        TokenStream.Kind[] expected;
        String msg;

        SyntaxException(TokenStream.Token t, TokenStream.Kind expected) {
            this.t = t;
            msg = "";
            this.expected = new TokenStream.Kind[1];
            this.expected[0] = expected;

        }

        public SyntaxException(TokenStream.Token t, String msg) {
            this.t = t;
            this.msg = msg;
            this.expected = new TokenStream.Kind[0];
        }

        public SyntaxException(TokenStream.Token t, TokenStream.Kind[] expected) {
            this.t = t;
            msg = "";
            this.expected = expected;
        }

        public String getMessage() {
            StringBuilder sb = new StringBuilder();
            sb.append(" error at token ").append(t.toString()).append(" ")
                    .append(msg);
            sb.append(". Expected: ");
            for (TokenStream.Kind kind : expected) {
                sb.append(kind).append(" ");
            }
            return sb.toString();
        }
    }
    TokenStream tokens;
    TokenStream.Token t;
    List<SyntaxException> exceptions;

    public Parser(TokenStream tokens) {
        this.tokens = tokens;
        t = tokens.nextToken();
        exceptions = new ArrayList<SyntaxException>();
    }

    private TokenStream.Kind match(TokenStream.Kind kind) throws SyntaxException {
        if (isKind(kind)) {
            consume();
            return kind;
        }
        throw new SyntaxException(t, kind);
    }

    private TokenStream.Kind match(TokenStream.Kind... kinds) throws SyntaxException {
        TokenStream.Kind kind = t.kind;
        if (isKind(kinds)) {
            consume();
            return kind;
        }
        StringBuilder sb = new StringBuilder();
        for (TokenStream.Kind kind1 : kinds) {
            sb.append(kind1).append(" ");
        }
        throw new SyntaxException(t, "expected one of " + sb.toString());
    }

    private boolean isKind(TokenStream.Kind kind) {
        return (t.kind == kind);
    }

    private void consume() {
        if (t.kind != EOF)
            t = tokens.nextToken();
    }

    private boolean isKind(TokenStream.Kind... kinds) {
        for (TokenStream.Kind kind : kinds) {
            if (t.kind == kind)
                return true;
        }
        return false;
    }

    //This is a convenient way to represent fixed sets of
    //token kinds.  You can pass these to isKind.
    static final TokenStream.Kind[] REL_OPS = {BAR, AND, EQUAL, NOTEQUAL, LT, GT, LE, GE};
    static final TokenStream.Kind[] WEAK_OPS = {PLUS, MINUS};
    static final TokenStream.Kind[] STRONG_OPS = {TIMES, DIV};
    static final TokenStream.Kind[] VERY_STRONG_OPS = {LSHIFT, RSHIFT};
    static final TokenStream.Kind[] Type_Poss = {KW_INT, KW_BOOLEAN, KW_STRING, AT};
    static final TokenStream.Kind[] Simple_Type = {KW_INT, KW_BOOLEAN, KW_STRING};
    static final TokenStream.Kind[] Statement_PredictSet = {IDENT, KW_PRINT, KW_WHILE, KW_IF, MOD, KW_RETURN};
    static final TokenStream.Kind[] Factor_Kinds = {INT_LIT, BL_TRUE, BL_FALSE, STRING_LIT};
    static final TokenStream.Kind[] Factor_Predict_Set = {IDENT, INT_LIT, BL_TRUE, BL_FALSE, STRING_LIT, LPAREN, NOT, MINUS,
            KW_KEY, KW_SIZE, KW_VALUE, LCURLY, AT};
    static final TokenStream.Kind[] Error_Recovery_Kinds = {SEMICOLON,RCURLY,EOF};


    public ASTNode parse(){
        ASTNode tree = null;
        try
        {
            tree = Program();
            match(EOF);
        }
        catch(SyntaxException e){
            exceptions.add(e);
        }
        return tree;
    }

    private ASTNode Program() throws SyntaxException {
        TokenStream.Token firstToken = t;
        List<QualifiedName> imports = ImportList();
        match(KW_CLASS);
        String name = t.getText();
        match(IDENT);
        Block block = Block();
        ASTNode program = null;
        if(block != null && imports != null) {
            program = new Program(firstToken, imports, name, block);
        }
        return program;
    }

    private List<QualifiedName> ImportList() throws SyntaxException {
        List<QualifiedName> imports = null;
        try {
            imports = new ArrayList<QualifiedName>();
            while (!isKind(KW_CLASS)) {
                TokenStream.Token firstToken = t;

                match(KW_IMPORT);
                StringBuilder name = new StringBuilder();
                name.append(t.getText());
                match(IDENT);

                while (t.kind != SEMICOLON) {
                    name.append("/");
                    match(DOT);
                    name.append(t.getText());
                    match(IDENT);
                }
                match(SEMICOLON);
                imports.add(new QualifiedName(firstToken, name.toString()));
            }
        }
        catch (SyntaxException e)
        {
            while(!(isKind(SEMICOLON) || isKind(KW_CLASS))) {
                consume();
            }
            if(isKind(SEMICOLON)){
                match(SEMICOLON);
                exceptions.add(e);
                while(!isKind(KW_CLASS))
                {
                    ImportList();
                }
            }
            else if(isKind(KW_CLASS)){
                exceptions.add(e);
                return null;
            }
            else
            {
                throw e;
            }
        }
        return imports;
    }

    private Block Block() throws SyntaxException{
        match(LCURLY);
        return BlockLogic();
    }
    private Block BlockLogic() throws SyntaxException {
        Block block = null;
        try {
            TokenStream.Token firstToken = t;
            List<BlockElem> blockElem = new ArrayList<BlockElem>();
            while (!isKind(RCURLY)) {
                if (isKind(KW_DEF)) {
                    TokenStream.Token firstTokenBlockElem = t;
                    match(KW_DEF);
                    if (isKind(IDENT)) {
                        TokenStream.Token identToken = t;
                        match(IDENT);
                        if (isKind(COLON)) { // VarDec
                            match(COLON);
                            Type type = Type();
                            blockElem.add(new VarDec(firstTokenBlockElem, identToken, type));
                        } else if (isKind(ASSIGN)) { // ClosureDec
                            match(ASSIGN);
                            Closure closure = Closure();
                            if(closure != null) {
                                blockElem.add(new ClosureDec(firstTokenBlockElem, identToken, closure));
                            }
                        }
                        else //Undeclared Type
                        {
                            Type type = new UndeclaredType(firstTokenBlockElem);
                            blockElem.add(new VarDec(firstTokenBlockElem, identToken, type));
                        }
                        match(SEMICOLON);
                    }
                } else {
                    if (isKind(Statement_PredictSet)) {
                        try {
                            Statement statement = Statement();
                            blockElem.add(statement);
                        } catch (SyntaxException e) {
                            while (!isKind(Error_Recovery_Kinds)) {
                                consume();
                            }
                            if (isKind(SEMICOLON)) {
                                match(SEMICOLON);
                                exceptions.add(e);
                                while (isKind(Statement_PredictSet)) {
                                    Statement();
                                }
                            } else if (isKind(RCURLY)) {
                                match(RCURLY);
                                exceptions.add(e);
                                return null;
                            } else {
                                throw e;
                            }
                        }
                    } else {
                        match(SEMICOLON);
                    }
                }
            }
            match(RCURLY);
            block = new Block(firstToken, blockElem);
        }
        catch (SyntaxException e)
        {
            while(!isKind(Error_Recovery_Kinds)) {
                consume();
            }
            if(isKind(SEMICOLON)){
                match(SEMICOLON);
                exceptions.add(e);
                while(!isKind(EOF))
                {
                    BlockLogic();
                }
            }
            else if(isKind(RCURLY)){
                match(RCURLY);
                exceptions.add(e);
                return null;
            }
            else
            {
                throw e;
            }
        }
        if(exceptions.size() > 0) return null;
        return block;
    }

    private VarDec VarDec(TokenStream.Token firstToken) throws SyntaxException {
        TokenStream.Token identToken = null;
        Type type = new UndeclaredType(firstToken);
        if (isKind(IDENT)) {
            identToken = t;
            match(IDENT);
            if (isKind(COLON)) { // VarDec
                match(COLON);
                type = Type();
            }
            // else Empty
        }//else Empty

        return new VarDec(firstToken,identToken,type);
    }

    private Statement Statement() throws SyntaxException {
        Statement statement = null;
        if (isKind(IDENT)) { //Assignment Statement
            TokenStream.Token firstToken = t;
            LValue lValue = LValue();
            match(ASSIGN);
            Expression expression = Expression();
            statement = new AssignmentStatement(firstToken, lValue, expression);
        } else if (isKind(KW_PRINT)) { // PrintStatement
            TokenStream.Token firstToken = t;
            match(KW_PRINT);
            Expression expression = Expression();
            statement = new PrintStatement(firstToken, expression);
        } else if (isKind(KW_WHILE)) {
            TokenStream.Token firstToken = t;
            match(KW_WHILE);
            if (isKind(TIMES)) {//whileStarStatement
                match(TIMES);
                match(LPAREN);
                TokenStream.Token RangeExpressionStart = t;
                Expression expression = Expression(); //ExpressionLower for RangeExpression
                if (isKind(RANGE)) // While Range Expression
                {
                    match(RANGE);
                    Expression expressionUpper = Expression();
                    match(RPAREN);
                    Block block = Block();
                    RangeExpression rangeExpression = new RangeExpression(RangeExpressionStart, expression, expressionUpper);
                    statement = new WhileRangeStatement(firstToken, rangeExpression, block);
                } else {
                    match(RPAREN);
                    Block block = Block();
                    statement = new WhileStarStatement(firstToken, expression, block);
                }
            } else { //whileStatement
                match(LPAREN);
                Expression expression = Expression();
                match(RPAREN);
                Block block = Block();
                statement = new WhileStatement(firstToken, expression, block);
            }
        } else if (isKind(KW_IF)) { //IFStatement
            TokenStream.Token firstToken = t;
            match(KW_IF);
            match(LPAREN);
            Expression expression = Expression();
            match(RPAREN);
            Block block = Block(); // block if block
            if (isKind(KW_ELSE)) {//IFElse Statement
                match(KW_ELSE);
                Block elseBlock = Block();
                statement = new IfElseStatement(firstToken, expression, block, elseBlock);
            } else {
                statement = new IfStatement(firstToken, expression, block);
            }
        } else if (isKind(MOD)) {//Expression Statement
            TokenStream.Token firstToken = t;
            match(MOD);
            Expression expression = Expression();
            statement = new ExpressionStatement(firstToken, expression);
        } else if (isKind(KW_RETURN)) {//Return Statment
            TokenStream.Token firstToken = t;
            match(KW_RETURN);
            Expression expression = Expression();
            statement = new ReturnStatement(firstToken, expression);
        }
        match(SEMICOLON);
        return statement;
    }

    private Expression Expression() throws SyntaxException {
        TokenStream.Token firstToken = t;
        Expression expression = Term();
        while (isKind(REL_OPS)) {
            TokenStream.Token op = t;
            RelOP();
            Expression secondExpression = Term();
            expression = new BinaryExpression(firstToken, expression,op,secondExpression);
        }

        return expression;
    }

    private void RelOP() throws SyntaxException {
        match(REL_OPS);
    }

    private void WeakOp() throws SyntaxException {
        match(WEAK_OPS);
    }

    private Expression Term() throws SyntaxException {
        TokenStream.Token firstToken = t;
        Expression expression = Elem();
        while (isKind(WEAK_OPS)) {
            TokenStream.Token op = t;
            WeakOp();
            Expression secondExpression = Elem();
            expression = new BinaryExpression(firstToken, expression,op,secondExpression);
        }
        return expression;
    }

    private Expression Elem() throws SyntaxException {
        TokenStream.Token firstToken = t;
        Expression expression = Thing();
        while (isKind(STRONG_OPS)) {
            TokenStream.Token op = t;
            StrongOp();
            Expression secondExpression = Thing();
            expression = new BinaryExpression(firstToken, expression,op,secondExpression);
        }
        return expression;
    }

    private void StrongOp() throws SyntaxException {
        match(STRONG_OPS);
    }

    private Expression Thing() throws SyntaxException {
        TokenStream.Token firstToken = t;
        Expression expression = Factor();
        while (isKind(VERY_STRONG_OPS)) {
            TokenStream.Token op = t;
            VeryStrongOp();
            Expression secondExpression = Factor();
            expression = new BinaryExpression(firstToken, expression,op,secondExpression);
        }
        return expression;
    }

    private void VeryStrongOp() throws SyntaxException {
        match(VERY_STRONG_OPS);
    }

    private Expression Factor() throws SyntaxException {
        Expression expression = null;
        if (isKind(IDENT)) {
            TokenStream.Token firstToken = t;
            match(IDENT);
            if (isKind(LSQUARE)) {//ListorMapElemExpression
                match(LSQUARE);
                Expression elemExpression = Expression();
                match(RSQUARE);
                expression = new ListOrMapElemExpression(firstToken,firstToken,elemExpression);
            } else if (isKind(LPAREN)) // ClosureEvalExpression
            {
                match(LPAREN);
                List<Expression> expressionList = ExpressionList();
                match(RPAREN);
                expression = new ClosureEvalExpression(firstToken,firstToken, expressionList);
            }
            else //IdentExpression
            {
                expression = new IdentExpression(firstToken,firstToken);
            }
        } else if (isKind(Factor_Kinds)) {
            if(isKind(INT_LIT))
            {
                expression = new IntLitExpression(t,t.getIntVal());
            }
            else if(isKind(STRING_LIT))
            {
                expression = new StringLitExpression(t,t.getText());
            }
            else
            {
                expression = new BooleanLitExpression(t,t.getBooleanVal());
            }
            match(Factor_Kinds);
        } else if (isKind(LPAREN)) {
            match(LPAREN);
            expression = Expression();
            match(RPAREN);
        } else if (isKind(NOT)) {
            TokenStream.Token firstToken = t;
            match(NOT);
            Expression expressionResult = Factor();
            expression = new UnaryExpression(firstToken,firstToken,expressionResult);
        } else if (isKind(MINUS)) {
            TokenStream.Token firstToken = t;
            match(MINUS);
            Expression expressionResult = Factor();
            expression = new UnaryExpression(firstToken,firstToken,expressionResult);
        } else if (isKind(KW_SIZE)) {
            TokenStream.Token firstToken = t;
            match(KW_SIZE);
            Expression expressionResult = Expression();
            expression = new SizeExpression(firstToken, expressionResult);
        } else if (isKind(KW_KEY)) {
            TokenStream.Token firstToken = t;
            match(KW_KEY);
            match(LPAREN);
            Expression expressionResult = Expression();
            match(RPAREN);
            expression = new KeyExpression(firstToken, expressionResult);
        } else if (isKind(KW_VALUE)) {
            TokenStream.Token firstToken = t;
            match(KW_VALUE);
            match(LPAREN);
            Expression expressionResult = Expression();
            match(RPAREN);
            expression = new ValueExpression(firstToken, expressionResult);
        } else if (isKind(LCURLY)) {
            TokenStream.Token firstToken = t;
            Closure closure = Closure();
            expression = new ClosureExpression(firstToken, closure);
        } else if (isKind(AT))
        {
            TokenStream.Token firstToken = t;
            match(AT);
            if (isKind(AT)) //MapList
            {
                match(AT);
                match(LSQUARE);
                List<KeyValueExpression> mapList = KeyValueList();
                match(RSQUARE);
                expression = new MapListExpression(firstToken,mapList);
            } else { //List
                match(LSQUARE);
                List<Expression> expressionList = ExpressionList();
                match(RSQUARE);
                expression = new ListExpression(firstToken, expressionList);
            }
        } else {
            throw new SyntaxException(t, "Not a Factor Kind!!");
        }
        return expression;
    }

    private List<KeyValueExpression> KeyValueList() throws SyntaxException {
        List<KeyValueExpression> keyValueExpressionList = new ArrayList<KeyValueExpression>();
        if (isKind(Factor_Predict_Set)) {
            keyValueExpressionList.add(KeyValueExpression());
            while (isKind(COMMA)) {
                match(COMMA);
                keyValueExpressionList.add(KeyValueExpression());
            }
        }
        return keyValueExpressionList;
    }

    private KeyValueExpression KeyValueExpression() throws SyntaxException {
        TokenStream.Token firstToken = t;
        Expression key = Expression();
        match(COLON);
        Expression value = Expression();
        KeyValueExpression keyValueExpression = new KeyValueExpression(firstToken,key,value);
        return keyValueExpression;
    }

    private List<Expression> ExpressionList() throws SyntaxException {
        List<Expression> expressionList = new ArrayList<Expression>();
        if (isKind(Factor_Predict_Set)) {
            expressionList.add(Expression());
            while (isKind(COMMA)) {
                match(COMMA);
                expressionList.add(Expression());
            }
        }
        return expressionList;
    }

    private LValue LValue() throws SyntaxException {
        TokenStream.Token firstToken = t;
        LValue lValue = null;

        match(IDENT);
        if (isKind(LSQUARE)) {
            match(LSQUARE);
            Expression expression = Expression();
            match(RSQUARE);
            lValue = new ExpressionLValue(firstToken,firstToken, expression);//firstToken == IdentToken
        }
        else
        {
            lValue = new IdentLValue(firstToken,firstToken); //firstToken == IdentToken
        }
        return lValue;
    }

    private Closure Closure() throws SyntaxException {
        Closure closure = null;
        try {
            TokenStream.Token firstToken = t;
            match(LCURLY);
            List<VarDec> varDecList = new ArrayList<VarDec>();
            List<Statement> statementList = new ArrayList<Statement>();
            if (!isKind(ARROW)) {
                VarDec varDecElem = VarDec(firstToken);
                varDecList.add(varDecElem);
            }
            while (isKind(COMMA)) {
                match(COMMA);
                VarDec varDecElem = VarDec(firstToken);
                varDecList.add(varDecElem);
            }
            match(ARROW);
            while (isKind(Statement_PredictSet)) {
                Statement statement = Statement();
                statementList.add(statement);
            }
            closure = new Closure(firstToken, varDecList, statementList);
        }
        catch (SyntaxException e)
        {
            while(!isKind(Error_Recovery_Kinds)) {
                consume();
            }
            if(isKind(SEMICOLON)){
                match(SEMICOLON);
                exceptions.add(e);
                while(isKind(Statement_PredictSet))
                {
                    Statement();
                }
            }
            else if(isKind(RCURLY)){
                match(RCURLY);
                exceptions.add(e);
                return null;
            }
            else
            {
                throw e;
            }
        }
        match(RCURLY);
        return closure;
    }

    private Type Type() throws SyntaxException {
        TokenStream.Token firstToken = t;
        Type type = new SimpleType(firstToken,firstToken); // initialize
        match(Type_Poss);
        if (isKind(AT)) { //KeyValueType
            match(AT);
            match(LSQUARE);
            TokenStream.Token simpleTypeToken = t;
            match(Simple_Type);
            match(COLON);
            Type retType = Type();
            match(RSQUARE);
            type = new KeyValueType(firstToken, new SimpleType(simpleTypeToken,simpleTypeToken), retType);
        } else if (isKind(LSQUARE)) { //ListType
            match(LSQUARE);
            Type retType = Type();
            match(RSQUARE);
            type = new ListType(firstToken, retType);
        }
        else
        {
            type = new SimpleType(firstToken,firstToken);
        }

        return type;
    }
}
