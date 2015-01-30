package cop5555sp15;

import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;

import java.util.HashMap;

import static cop5555sp15.TokenStream.Kind.*;

public class Scanner {


    private enum State {
        START, COMMENT, IDENT, INT_LITERAL, STRING_LITERAL, SEPERATOR,
        OPERATOR, END
    }

    private State state;
    private TokenStream stream;
    private int index;
    private int lineNum;
    private char ch;
    private HashMap<String, Kind> reservedWords;

    public Scanner(TokenStream stream) {
        this.stream = stream;
        this.index = 0;
        this.lineNum = 1;
        this.state = State.START;
        this.reservedWords = new HashMap<String, Kind>();
        this.reservedWords.put("int", KW_INT);
        this.reservedWords.put("string", KW_STRING);
        this.reservedWords.put("boolean", KW_BOOLEAN);
        this.reservedWords.put("import", KW_IMPORT);
        this.reservedWords.put("class", KW_CLASS);
        this.reservedWords.put("def", KW_DEF);
        this.reservedWords.put("while", KW_WHILE);
        this.reservedWords.put("if", KW_IF);
        this.reservedWords.put("else", KW_ELSE);
        this.reservedWords.put("return", KW_RETURN);
        this.reservedWords.put("print", KW_PRINT);
        this.reservedWords.put("true", BL_TRUE);
        this.reservedWords.put("false", BL_FALSE);
        this.reservedWords.put("null", NL_NULL);
    }


    //Get the next Char for the input and update the index
    //Return false if not possible
    private boolean getch(int i) {
        if (index + i < stream.inputChars.length) {
            index += i;
            ch = stream.inputChars[index];
            return true;
        } else {
            return false;
        }
    }

    private void newLineCheck(){
        if (ch == '\n') {
            lineNum++;
        } else if (ch == '\r') {
            if (index + 1 < stream.inputChars.length) {
                char next = stream.inputChars[index + 1];
                if (next == '\n') {
                    lineNum++;
                    getch(1);
                } else {
                    lineNum++;
                }
            } else {
                lineNum++;
            }
        }
    }
    //Returns the next token in the input
    private Token next() {
        Token t = null;
        do {
            switch (state) {
                case START:
                    if (index >= stream.inputChars.length) {
                        state = State.END;
                        break;
                    } else {
                        ch = stream.inputChars[index];
                    }
                    if (Character.isWhitespace(ch)) {
                        newLineCheck();
                        if (!getch(1)) {
                            state = State.END;
                            break;
                        } else {
                            state = State.START;
                        }
                    }
                    // Check for Comment
                    else if (ch == '/') {
                        if (index + 1 < stream.inputChars.length) {
                            char next = stream.inputChars[index + 1];
                            int beg = index;
                            if (next == '*') {
                                if (!getch(2)) {
                                    t = stream.new Token(UNTERMINATED_COMMENT, beg, beg + 2, lineNum);
                                    state = State.END;
                                    break;
                                } else {
                                    state = State.COMMENT;
                                }
                            } else state = State.OPERATOR;
                        } else state = State.OPERATOR;
                    } else if (ch == '=' || ch == '|' || ch == '&' || ch == '!' || ch == '<' || ch == '>' || ch == '+' ||
                            ch == '-' || ch == '*' || ch == '%' || ch == '@') {
                        state = State.OPERATOR;
                    } else if (ch == '.' || ch == ';' || ch == ',' || ch == '(' || ch == ')' || ch == '[' || ch == ']' ||
                            ch == '{' || ch == '}' || ch == ':' || ch == '?') {
                        state = State.SEPERATOR;
                    } else if (Character.isJavaIdentifierStart(ch)) {
                        state = State.IDENT;
                    } else if (Character.isDigit(ch)) {
                        state = State.INT_LITERAL;
                    } else if (ch == '"') {
                        if (!getch(1)) {
                            t = stream.new Token(UNTERMINATED_STRING, index, index + 1, lineNum);
                            state = State.END;
                            break;
                        }
                        state = State.STRING_LITERAL;
                    } else {
                        t = stream.new Token(ILLEGAL_CHAR, index, index + 1, lineNum);
                        if (!getch(1)) {
                            state = State.END;
                            break;
                        } else {
                            state = State.START;
                        }
                    }
                    break;
                case COMMENT:
                    int beg = index;
                    char next = ' ';
                    if (index + 1 < stream.inputChars.length) {
                        next = stream.inputChars[index + 1];
                    }
                    while (!(ch == '*' && next == '/')) {
                        newLineCheck();
                        if (!getch(1)) {
                            t = stream.new Token(UNTERMINATED_COMMENT, beg, index + 1, lineNum);
                            break;
                        }
                        if (index + 1 < stream.inputChars.length) {
                            next = stream.inputChars[index + 1];
                        }
                    }
                    if (!getch(2)) {
                        state = State.END;
                        break;
                    }
                    state = State.START;
                    break;
                case OPERATOR:
                    if (ch == '|') {
                        t = stream.new Token(BAR, index, index + 1, lineNum);
                    } else if (ch == '&') {
                        t = stream.new Token(AND, index, index + 1, lineNum);
                    } else if (ch == '+') {
                        t = stream.new Token(PLUS, index, index + 1, lineNum);
                    } else if (ch == '*') {
                        t = stream.new Token(TIMES, index, index + 1, lineNum);
                    } else if (ch == '/') {
                        t = stream.new Token(DIV, index, index + 1, lineNum);
                    } else if (ch == '%') {
                        t = stream.new Token(MOD, index, index + 1, lineNum);
                    } else if (ch == '@') {
                        t = stream.new Token(AT, index, index + 1, lineNum);
                    } else if (ch == '!') {
                        if (index + 1 < stream.inputChars.length) {
                            next = stream.inputChars[index + 1];
                            if (ch == '!' && next == '=') {
                                t = stream.new Token(NOTEQUAL, index, index + 2, lineNum);
                                getch(1);
                            }
                        }
                        if(t==null)t = stream.new Token(NOT, index, index + 1, lineNum);
                    } else if (ch == '=') {
                        if (index + 1 < stream.inputChars.length) {
                            next = stream.inputChars[index + 1];
                            if (ch == '=' && next == '=') {
                                t = stream.new Token(EQUAL, index, index + 2, lineNum);
                                getch(1);
                            }
                        }
                        if(t==null)t = stream.new Token(ASSIGN, index, index + 1, lineNum);
                    } else if (ch == '-') {
                        if (index + 1 < stream.inputChars.length) {
                            next = stream.inputChars[index + 1];
                            if (ch == '-' && next == '>') {
                                t = stream.new Token(ARROW, index, index + 2, lineNum);
                                getch(1);
                            }
                        }
                        if(t==null) t = stream.new Token(MINUS, index, index + 1, lineNum);

                    } else if (ch == '<') {
                        if (index + 1 < stream.inputChars.length) {
                            next = stream.inputChars[index + 1];
                            if (ch == '<' && next == '=') {
                                t = stream.new Token(LE, index, index + 2, lineNum);
                                getch(1);
                            } else if (ch == '<' && next == '<') {
                                t = stream.new Token(LSHIFT, index, index + 2, lineNum);
                                getch(1);
                            }
                        }
                        if(t==null) t = stream.new Token(LT, index, index + 1, lineNum);


                    } else if (ch == '>') {
                        if (index + 1 < stream.inputChars.length) {
                            next = stream.inputChars[index + 1];
                            if (ch == '>' && next == '=') {
                                t = stream.new Token(GE, index, index + 2, lineNum);
                                getch(1);
                            } else if (ch == '>' && next == '>') {
                                t = stream.new Token(RSHIFT, index, index + 2, lineNum);
                                getch(1);
                            }
                        }
                        if(t==null) t = stream.new Token(GT, index, index + 1, lineNum);


                    }

                    if (!getch(1)) {
                        state = State.END;
                        break;
                    } else {
                        state = State.START;
                    }
                    break;
                case SEPERATOR:
                    if (ch == '.') {
                        if (index + 1 < stream.inputChars.length) {
                            next = stream.inputChars[index + 1];
                            if (ch == '.' && next == '.') {
                                t = stream.new Token(RANGE, index, index + 2, lineNum);
                                getch(1);
                            }
                        }
                        if(t==null) t = stream.new Token(DOT, index, index + 1, lineNum);


                    } else if (ch == ';') {
                        t = stream.new Token(SEMICOLON, index, index + 1, lineNum);
                    } else if (ch == ',') {
                        t = stream.new Token(COMMA, index, index + 1, lineNum);
                    } else if (ch == '(') {
                        t = stream.new Token(LPAREN, index, index + 1, lineNum);
                    } else if (ch == ')') {
                        t = stream.new Token(RPAREN, index, index + 1, lineNum);
                    } else if (ch == '[') {
                        t = stream.new Token(LSQUARE, index, index + 1, lineNum);
                    } else if (ch == ']') {
                        t = stream.new Token(RSQUARE, index, index + 1, lineNum);
                    } else if (ch == '{') {
                        t = stream.new Token(LCURLY, index, index + 1, lineNum);
                    } else if (ch == '}') {
                        t = stream.new Token(RCURLY, index, index + 1, lineNum);
                    } else if (ch == ':') {
                        t = stream.new Token(COLON, index, index + 1, lineNum);
                    } else if (ch == '?') {
                        t = stream.new Token(QUESTION, index, index + 1, lineNum);
                    }

                    if (!getch(1)) {
                        state = State.END;
                        break;
                    } else {
                        state = State.START;
                    }
                    break;
                case IDENT:
                    beg = index;
                    while (Character.isJavaIdentifierStart(ch) || Character.isJavaIdentifierPart(ch)) {
                        if (!getch(1)) {
                            state = State.END;
                            index++;
                            break;
                        }
                    }
                    String check = String.valueOf(stream.inputChars, beg, index - beg);
                    if (reservedWords.containsKey(check)) {
                        t = stream.new Token(reservedWords.get(check), beg, index, lineNum);
                    } else {
                        t = stream.new Token(IDENT, beg, index, lineNum);
                    }

                    state = State.START;
                    break;
                case STRING_LITERAL:
                    beg = index - 1;
                    while (ch != '"') {
                        newLineCheck();
                        if(ch == '\\')
                        {
                            if (index + 1 < stream.inputChars.length) {
                                next = stream.inputChars[index + 1];
                                if (next == '\"') {
                                    getch(1);
                                }
                            }
                        }
                        if (!getch(1)) {
                            t = stream.new Token(UNTERMINATED_STRING, beg, index+1, lineNum);
                            state = State.END;
                            index++;
                            break;
                        }
                    }
                    if(t==null) t = stream.new Token(STRING_LIT, beg, index + 1, lineNum);

                    if (!getch(1)) {
                        state = State.END;
                        break;
                    } else {
                        state = State.START;
                    }
                    break;
                case INT_LITERAL:
                    if (ch == '0') {
                        t = stream.new Token(INT_LIT, index, index + 1, lineNum);
                        if (!getch(1)) {
                            state = State.END;
                            break;
                        }
                    } else {
                        beg = index;
                        while (Character.isDigit(ch)) {
                            if (!getch(1)) {
                                state = State.END;
                                index++;
                                break;
                            }
                        }
                        t = stream.new Token(INT_LIT, beg, index, lineNum);
                    }
                    state = State.START;
                    break;
                case END:
                    t = stream.new Token(EOF, stream.inputChars.length, stream.inputChars.length, lineNum);
                    break;
                default:
                    break;
            }

        } while (t == null);
        return t;
    }
    // Fills in the stream.tokens list with recognized tokens from the input
    public void scan() {
        Token t;
        do {
            t = next();
            stream.tokens.add(t);
        } while (!t.kind.equals(EOF));
    }
}

