package cop5555sp15;

import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;
import org.junit.Test;

import java.util.ArrayList;

import static cop5555sp15.TokenStream.Kind.*;
import static org.junit.Assert.*;

public class TestScanner {

    private TokenStream scanInput(String input) {
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        System.out.println(stream);
        return stream;
    }

    // Creates an array containing the kinds of the tokens in the token list
    private Kind[] makeKindArray(TokenStream stream) {
        Kind[] kinds = new Kind[stream.tokens.size()];
        for (int i = 0; i < stream.tokens.size(); ++i) {
            kinds[i] = stream.tokens.get(i).kind;
        }
        return kinds;

    }

    // Creates an array containing the texts of the tokens in the token list
    private String[] makeTokenTextArray(TokenStream stream) {
        String[] kinds = new String[stream.tokens.size()];
        for (int i = 0; i < stream.tokens.size(); ++i) {
            kinds[i] = stream.tokens.get(i).getText();
        }
        return kinds;
    }

    @Test
    public void emptyInput() {
        System.out.println("Test: emptyInput");
        String input = "";
        TokenStream stream = scanInput(input);
        assertEquals(1, stream.tokens.size()); // creates EOF token
        assertEquals(EOF, stream.nextToken().kind);

    }

    @Test
    public void noWhiteSpace() {
        System.out.println("Test: noWhitespace");
        String input = "@%";
        TokenStream stream = scanInput(input);
        assertEquals(3, stream.tokens.size()); // one each for @ and %, plus the
        // eof
        // token
        assertEquals(AT, stream.nextToken().kind);
        assertEquals(MOD, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);

    }

    @Test
    public void errorToken() {
        System.out.println("Test: noWhitespace");
        String input = "@#  *";
        TokenStream stream = scanInput(input);
        assertEquals(4, stream.tokens.size()); // one each for @,#, and *, plus
        // the eof token
        assertEquals(AT, stream.nextToken().kind);
        assertEquals(ILLEGAL_CHAR, stream.nextToken().kind);
        assertEquals(TIMES, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);

    }

    @Test
    public void onlySpaces() {
        System.out.println("Test: onlySpaces");
        String input = "     "; // five spaces
        System.out.println("input is five spaces");
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        assertEquals(1, stream.tokens.size()); // creates EOF token
        Token t = stream.nextToken();
        System.out.println(stream);
        assertEquals(EOF, t.kind);
        assertEquals(5, t.beg);
    }

    @Test
    public void skipWhiteSpace() {
        System.out.println("skipWhiteSpace");
        String input = "   ;;;   %@%\n  \r   \r\n ;;;";
        TokenStream stream = scanInput(input);
        assertEquals(SEMICOLON, stream.nextToken().kind);
        assertEquals(SEMICOLON, stream.nextToken().kind);
        assertEquals(SEMICOLON, stream.nextToken().kind);
        assertEquals(MOD, stream.nextToken().kind);
        assertEquals(AT, stream.nextToken().kind);
        assertEquals(MOD, stream.nextToken().kind);
        assertEquals(SEMICOLON, stream.nextToken().kind);
        assertEquals(SEMICOLON, stream.nextToken().kind);
        Token t = stream.nextToken();
        assertEquals(SEMICOLON, t.kind);
        assertEquals(4,t.getLineNumber());
    }

    @Test
    public void dotsAndRanges() {
        System.out.println("dotsAndRanges");
        String input = ".\n..\n.. . . ..\n...\n";
        TokenStream stream = scanInput(input);
        assertEquals(DOT, stream.nextToken().kind);
        assertEquals(RANGE, stream.nextToken().kind);
        assertEquals(RANGE, stream.nextToken().kind);
        assertEquals(DOT, stream.nextToken().kind);
        assertEquals(DOT, stream.nextToken().kind);
        assertEquals(RANGE, stream.nextToken().kind);
        assertEquals(RANGE, stream.nextToken().kind);
        assertEquals(DOT, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
        assertEquals(3, stream.tokens.get(5).getLineNumber());// 5th token is on
        // line 3
    }

    @Test
    public void firstPartAtEndOfInput() {
        System.out.println("firstPartATEndOfInput");
        String input = "!";
        TokenStream stream = scanInput(input);
        assertEquals(NOT, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }

    @Test
    public void twoStateTokens() {
        System.out.println("twoStateTokens");
        String input = "= == =\n= ! != - -> -! =!!";
        TokenStream stream = scanInput(input);
        assertEquals(ASSIGN, stream.nextToken().kind);
        assertEquals(EQUAL, stream.nextToken().kind);
        assertEquals(ASSIGN, stream.nextToken().kind);
        assertEquals(ASSIGN, stream.nextToken().kind);
        assertEquals(NOT, stream.nextToken().kind);
        assertEquals(NOTEQUAL, stream.nextToken().kind);
        assertEquals(MINUS, stream.nextToken().kind);
        assertEquals(ARROW, stream.nextToken().kind);
        assertEquals(MINUS, stream.nextToken().kind);
        assertEquals(NOT, stream.nextToken().kind);
        assertEquals(ASSIGN, stream.nextToken().kind);
        assertEquals(NOT, stream.nextToken().kind);
        assertEquals(NOT, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }

    // This test constructs the exptected token list and compares to the one
    // created by the Scanner
    @Test
    public void compareTokenList() {
        System.out.println("compareTokenList");
        String input = "= ==";
        TokenStream stream = scanInput(input);
        Token t0 = stream.new Token(ASSIGN, 0, 1, 1);
        Token t1 = stream.new Token(EQUAL, 2, 4, 1);
        Token t2 = stream.new Token(EOF, 4, 4, 1);
        ArrayList<Token> expected_tokens = new ArrayList<Token>();
        expected_tokens.add(t0);
        expected_tokens.add(t1);
        expected_tokens.add(t2);
        assertArrayEquals(expected_tokens.toArray(), stream.tokens.toArray());
    }

    @Test
    public void lessAndGreater() {
        System.out.println("lessAndGreater");
        String input = " < << <= > >> >= -> <>";
        TokenStream stream = scanInput(input);
        assertEquals(LT, stream.nextToken().kind);
        assertEquals(LSHIFT, stream.nextToken().kind);
        assertEquals(LE, stream.nextToken().kind);
        assertEquals(GT, stream.nextToken().kind);
        assertEquals(RSHIFT, stream.nextToken().kind);
        assertEquals(GE, stream.nextToken().kind);
        assertEquals(ARROW, stream.nextToken().kind);
        assertEquals(LT, stream.nextToken().kind);
        assertEquals(GT, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }

    @Test
    public void intLiterals() {
        System.out.println("lessAndGreater");
        String input = "0 1 23 45+ 67<=9";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { INT_LIT, INT_LIT, INT_LIT, INT_LIT, PLUS,
                INT_LIT, LE, INT_LIT, EOF };
        String[] expectedTexts = { "0", "1", "23", "45", "+", "67", "<=", "9",
                "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void stringLiterals() {
        System.out.println("stringLiterals");
        String input = " \"abc\" \"def\" \"ghijk\" \"123\" \"&^%$\" ";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { STRING_LIT, STRING_LIT, STRING_LIT,
                STRING_LIT, STRING_LIT, EOF };
        String[] expectedTexts = { "abc", "def", "ghijk", "123", "&^%$", "" }; // need
        // empty
        // string
        // for
        // eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void identifiers() {
        System.out.println("identifiers");
        String input = " abc ddef ghijk 123 a234 32a";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { IDENT, IDENT, IDENT, INT_LIT, IDENT, INT_LIT,
                IDENT, EOF };
        String[] expectedTexts = { "abc", "ddef", "ghijk", "123", "a234", "32",
                "a", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void keywords() {
        System.out.println("keywords");
        String input = " int  string  boolean import  class  def  while if  else  return  print aaa";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { KW_INT, KW_STRING, KW_BOOLEAN, KW_IMPORT,
                KW_CLASS, KW_DEF, KW_WHILE, KW_IF, KW_ELSE, KW_RETURN,
                KW_PRINT, IDENT, EOF };
        String[] expectedTexts = { "int", "string", "boolean", "import",
                "class", "def", "while", "if", "else", "return", "print",
                "aaa", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void boolAndNullLiterals() {
        System.out.println("boolAndNullLiterals");
        String input = " true false\n null";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { BL_TRUE, BL_FALSE, NL_NULL, EOF };
        String[] expectedTexts = { "true", "false", "null", "" }; // need empty
        // string
        // for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void multiLineString() {
        System.out.println("multiLineString");
        String input = " \"true false\n null\" ";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { STRING_LIT, EOF };
        String[] expectedTexts = { "true false\n null", "" }; // need empty
        // string for
        // eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));

    }

    @Test
    public void comments() {
        System.out.println("comments");
        String input = "/**/ 0 1 45+ 67<=9";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { INT_LIT, INT_LIT, INT_LIT, PLUS, INT_LIT, LE,
                INT_LIT, EOF };
        String[] expectedTexts = { "0", "1", "45", "+", "67", "<=", "9", "" }; // need
        // empty
        // string
        // for
        // eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void comments2() {
        System.out.println("comments2");
        String input = "/**/ 0 1 /** ***/ 45+ 67<=9";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { INT_LIT, INT_LIT, INT_LIT, PLUS, INT_LIT, LE,
                INT_LIT, EOF };
        String[] expectedTexts = { "0", "1", "45", "+", "67", "<=", "9", "" }; // need
        // empty
        // string
        // for
        // eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void comments3() {
        System.out.println("comments3");
        String input = "/**/ 0 1 /** ***/ 45+ 67<=9/*";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { INT_LIT, INT_LIT, INT_LIT, PLUS, INT_LIT, LE,
                INT_LIT, UNTERMINATED_COMMENT, EOF };
        String[] expectedTexts = { "0", "1", "45", "+", "67", "<=", "9", "/*",
                "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void onlyComment() {
        System.out.println("onlyComment");
        String input = "/**/";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { EOF };
        String[] expectedTexts = { "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void singleSlash(){
        System.out.println("singleSlash");
        String input = "/";
        TokenStream stream = scanInput(input);
        assertEquals(DIV, stream.nextToken().kind);

    }


    // Added By Nakul

    @Test
    public void separators1(){
        String input = ",()[]";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { COMMA, LPAREN, RPAREN, LSQUARE, RSQUARE, EOF };
        String[] expectedTexts = { ",", "(", ")", "[", "]", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void separators2(){
        String input = "{}:?";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { LCURLY, RCURLY, COLON, QUESTION, EOF };
        String[] expectedTexts = { "{", "}", ":", "?", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void operators1(){
        String input = "|&<> ";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { BAR, AND, LT, GT, EOF };
        String[] expectedTexts = { "|", "&", "<", ">", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void operators2(){
        String input = ">= << >>";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { GE, LSHIFT, RSHIFT, EOF };
        String[] expectedTexts = { ">=", "<<", ">>", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void twoCharOperators1(){
        String input = "<< < <<<";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { LSHIFT, LT, LSHIFT, LT, EOF };
        String[] expectedTexts = { "<<", "<", "<<", "<", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void twoCharOperators2(){
        String input = ">>> > >>";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { RSHIFT, GT, GT, RSHIFT, EOF };
        String[] expectedTexts = { ">>", ">", ">", ">>", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void twoCharOperators3(){
        String input = ">>>==== ";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { RSHIFT, GE, EQUAL, ASSIGN, EOF };
        String[] expectedTexts = { ">>", ">=", "==", "=", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void twoCharOperators4(){
        String input = "<<<<=!=!=";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { LSHIFT, LSHIFT, ASSIGN, NOTEQUAL, NOTEQUAL, EOF };
        String[] expectedTexts = { "<<", "<<", "=", "!=", "!=", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void twoCharSeparator1(){
        String input = ".......";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { RANGE, RANGE, RANGE, DOT, EOF };
        String[] expectedTexts = { "..", "..", "..", ".", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void almostKeywords1(){
        String input = "inta stringb booleanc importa classb defc ";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { IDENT, IDENT, IDENT, IDENT, IDENT, IDENT, EOF };
        assertArrayEquals(expectedKinds, makeKindArray(stream));
    }

    @Test
    public void almostKeywords2(){
        String input = "whiled ifc elseg returnb printm";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { IDENT, IDENT, IDENT, IDENT, IDENT, EOF };
        assertArrayEquals(expectedKinds, makeKindArray(stream));
    }

    @Test
    public void almostKeywords3(){
        String input = "true$ false_ nulll";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { IDENT, IDENT, IDENT, EOF };
        assertArrayEquals(expectedKinds, makeKindArray(stream));
    }

    @Test
    public void illegalChars(){
        String input = "# ^ ~ ` '";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { ILLEGAL_CHAR, ILLEGAL_CHAR, ILLEGAL_CHAR, ILLEGAL_CHAR, ILLEGAL_CHAR, EOF };
        String[] expectedTexts = { "#", "^", "~", "`", "'", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void identsTest1(){
        String input = "$ $8 a$a $$$ aa$9";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { IDENT, IDENT, IDENT, IDENT, IDENT, EOF };
        String[] expectedTexts = {"$", "$8", "a$a", "$$$", "aa$9", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void identsTest2(){
        String input = "_ _8 a_a ___ aa_9";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { IDENT, IDENT, IDENT, IDENT, IDENT, EOF };
        String[] expectedTexts = {"_", "_8", "a_a", "___", "aa_9", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void commentsTest1(){
        String input = "X /* \n\r\n\r\n\r */ X";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { IDENT, IDENT, EOF };
        String[] expectedTexts = {"X", "X", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void commentsTest2(){
        String input = "X /* \n\r\n\r\n\r X";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { IDENT, UNTERMINATED_COMMENT, EOF };
        assertArrayEquals(expectedKinds, makeKindArray(stream));
    }

    @Test
    public void commentsTest3(){
        String input = "/* /* */ */";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { TIMES, DIV, EOF };
        String[] expectedTexts = {"*", "/", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));

    }

    @Test
    public void stringLiteralTest1(){
        String input = "\" a\n b \"";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { STRING_LIT, EOF };
        String[] expectedTexts = {" a\n b ", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void stringLiteralTest2(){
        String input = "\" a\r\n b \"";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { STRING_LIT, EOF };
        String[] expectedTexts = {" a\r\n b ", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void stringLiteralTest3(){
        String input = "\" a\\\" b \"";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { STRING_LIT, EOF };
        String[] expectedTexts = {" a\" b ", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void stringLiteralTest4(){
        String input = "\" a\\\n b \"";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { STRING_LIT, EOF };
        String[] expectedTexts = {" a\n b ", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void stringLiteralTest5(){
        String input = "\" a\\\r b \"";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { STRING_LIT, EOF };
        String[] expectedTexts = {" a\r b ", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void intLiteralTest1(){
        String input = "5 0 01 100 1251";
        TokenStream stream = scanInput(input);
        Kind[] expectedKinds = { INT_LIT, INT_LIT, INT_LIT, INT_LIT, INT_LIT, INT_LIT, EOF };
        String[] expectedTexts = {"5", "0", "0", "1", "100", "1251", "" }; // need empty string for eof
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
        assertArrayEquals(expectedKinds, makeKindArray(stream));
    }



    @Test
    public void oneKeyWord() {
        System.out.println("Test: Beginning KeyWord");
        String input = "true";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        System.out.println(stream);
        assertEquals(2, stream.tokens.size()); // one each for true, plus the eof token
        assertEquals(BL_TRUE, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }






    @Test
    public void comboIdentResWrds() {
        System.out.println("comboIdentResWrds");
        String input = "abc def";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        System.out.println(stream);
        Kind[] expectedKinds = { IDENT,KW_DEF,EOF };
        String[] expectedTexts = { "abc","def","" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void identEnd() {
        System.out.println("identEnd");
        String input = "true = .. \r\n ? 011234a4 def";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        System.out.println(stream);
        Kind[] expectedKinds = { BL_TRUE, ASSIGN, RANGE, QUESTION, INT_LIT,INT_LIT, IDENT, KW_DEF,EOF };
        String[] expectedTexts = { "true","=","..","?", "0", "11234", "a4", "def", "" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void sepEnd() {
        System.out.println("sepEnd");
        String input = "[Test for ending with a Seperator.]";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        System.out.println(stream);
        Kind[] expectedKinds = { LSQUARE,IDENT,IDENT,IDENT,IDENT,IDENT,IDENT,DOT,RSQUARE,EOF };
        String[] expectedTexts = { "[","Test","for","ending","with", "a", "Seperator", ".", "]","" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void intLitEnd() {
        System.out.println("intLitEnd");
        String input = "0101. 2+20==22";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        System.out.println(stream);
        Kind[] expectedKinds = { INT_LIT,INT_LIT,DOT,INT_LIT,PLUS,INT_LIT,EQUAL,INT_LIT,EOF };
        String[] expectedTexts = { "0","101",".","2", "+", "20", "==", "22","" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void operators() {
        System.out.println("operators");
        String input = "=|&==!=<><=>=+-*/%!<<>>->@";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        System.out.println(stream);
        Kind[] expectedKinds = { ASSIGN,BAR,AND,EQUAL,NOTEQUAL,LT,GT,LE,GE,PLUS,MINUS,TIMES,DIV,
                MOD,NOT,LSHIFT,RSHIFT,ARROW,AT,EOF };
        String[] expectedTexts = { "=","|","&","==","!=","<",">","<=",">=","+","-","*","/","%",
                "!","<<",">>","->","@",""}; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void separators() {
        System.out.println("separators");
        String input = "...;,()[]{}:?";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        System.out.println(stream);
        Kind[] expectedKinds = { RANGE,DOT,SEMICOLON,COMMA,LPAREN,RPAREN,LSQUARE,RSQUARE,
                LCURLY,RCURLY,COLON,QUESTION,EOF };
        String[] expectedTexts = { "..",".",";",",","(",")","[","]","{","}",":","?",""}; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void allResWords() {
        System.out.println("allResWords");
        String input = "int string boolean import class def while if else return print true false null key value size";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        System.out.println(stream);
        Kind[] expectedKinds = { KW_INT,KW_STRING,KW_BOOLEAN,KW_IMPORT,KW_CLASS,KW_DEF,KW_WHILE,KW_IF,
                KW_ELSE,KW_RETURN,KW_PRINT,BL_TRUE,BL_FALSE,NL_NULL, KW_KEY, KW_VALUE, KW_SIZE, EOF };
        String[] expectedTexts = { "int","string","boolean","import","class","def","while","if","else",
                "return","print","true","false","null","key", "value","size",""}; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void arbitraryInput() {
        System.out.println("arbitraryInput");
        String input = "`~\"Crash\"\"?\"?#\r";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        System.out.println(stream);
        Kind[] expectedKinds = { ILLEGAL_CHAR,ILLEGAL_CHAR,STRING_LIT,STRING_LIT,QUESTION,ILLEGAL_CHAR,EOF };
        String[] expectedTexts = { "`","~","Crash","?", "?", "#","" }; // need empty string for eof
        assertArrayEquals(expectedKinds, makeKindArray(stream));
        assertArrayEquals(expectedTexts, makeTokenTextArray(stream));
    }

    @Test
    public void singleZero(){
        System.out.println("singleZero");
        String input = "0";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        System.out.println(stream);
    }

    @Test
    public void justTesting(){
        System.out.println("justTesting");
        String input = "\"abc";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        System.out.println(stream);
    }

    @Test
    public void newLineAtEnd(){
        System.out.println("newLineAtEnd");
        String input = "\"abc\"\r\n";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        System.out.println(stream);
    }

    @Test
    public void untermStringAtBeg(){
        System.out.println("untermStringAtBeg");
        String input = "\"";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        System.out.println(stream);
    }

    @Test
    public void untermCommentAtBeg(){
        System.out.println("untermCommentAtBeg");
        String input = "/*abc";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        System.out.println(stream);
    }

    //Double Char Seperators and Operators at End
    @Test
    public void dotAtEnd(){
        System.out.println("dotAtEnd");
        String input = ".";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        assertEquals(DOT, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }
    @Test
    public void assignAtEnd(){
        System.out.println("assignAtEnd");
        String input = "=";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        assertEquals(ASSIGN, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }
    @Test
    public void notAtEnd(){
        System.out.println("notAtEnd");
        String input = "!";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        assertEquals(NOT, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }
    @Test
    public void ltAtEnd(){
        System.out.println("ltAtEnd");
        String input = "<";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        assertEquals(LT, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }
    @Test
    public void gtAtEnd(){
        System.out.println("gtAtEnd");
        String input = ">";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        assertEquals(GT, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }
    @Test
    public void minusAtEnd(){
        System.out.println("minusAtEnd");
        String input = "-";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        assertEquals(MINUS, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }

    //Single Characters at End
    @Test
    public void rangeAtEnd(){
        System.out.println("rangeAtEnd");
        String input = "..";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        assertEquals(RANGE, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }
    @Test
    public void equalsAtEnd(){
        System.out.println("equalsAtEnd");
        String input = "==";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        assertEquals(EQUAL, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }
    @Test
    public void neAtEnd(){
        System.out.println("neAtEnd");
        String input = "!=";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        assertEquals(NOTEQUAL, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }
    @Test
    public void leAtEnd(){
        System.out.println("leAtEnd");
        String input = "<=";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        assertEquals(LE, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }
    @Test
    public void geAtEnd(){
        System.out.println("geAtEnd");
        String input = ">=";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        assertEquals(GE, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }
    @Test
    public void lshiftAtEnd(){
        System.out.println("lshiftAtEnd");
        String input = "<<";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        assertEquals(LSHIFT, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }
    @Test
    public void rshiftAtEnd(){
        System.out.println("equalsAtEnd");
        String input = ">>";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        assertEquals(RSHIFT, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }
    @Test
    public void arrowAtEnd(){
        System.out.println("arrowAtEnd");
        String input = "->";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        assertEquals(ARROW, stream.nextToken().kind);
        assertEquals(EOF, stream.nextToken().kind);
    }

    @Test
    public void illegalCharAtEnd(){
        System.out.println("illegalCharAtEnd");
        String input = "#";
        System.out.println(input);
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        System.out.println(stream);
    }




}
