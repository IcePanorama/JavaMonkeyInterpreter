package lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import token.Token;

//FIXME: all these tests should be rewritten
public class LexerTest{
    public static void testMultilineInput(String output, String[] expectedOutput) {
        var l = new Lexer(output);
        for (int i = 0; i < expectedOutput.length; i++) {
            Token tok = l.nextToken();
            assertEquals(expectedOutput[i], tok.type);
        }
    }

    @Test
    void eofShouldEqualEOF() {
        var l = new Lexer("");
        assertEquals(Token.EOF, l.nextToken().type);
    }

    @Test
    void identFiveShouldEqualIDENT() {
        var l = new Lexer("five");
        assertEquals(Token.IDENT, l.nextToken().type);
    }

    @Test
    void numFiveShouldEqualINT() {
        var l = new Lexer("5");
        assertEquals(Token.INT, l.nextToken().type);
    }

    @Test
    void assignShouldEqualASSIGN() {
        var l = new Lexer("=");
        assertEquals(Token.ASSIGN, l.nextToken().type);
    }

    @Test
    void plusShouldEqualPLUS() {
        var l = new Lexer("+");
        assertEquals(Token.PLUS, l.nextToken().type);
    }

    @Test
    void minusShouldEqualMINUS() {
        var l = new Lexer("-");
        assertEquals(Token.MINUS, l.nextToken().type);
    }

    @Test
    void exclamationShouldEqualBang() {
        var l = new Lexer("!");
        assertEquals(Token.BANG, l.nextToken().type);
    }

    @Test
    void asteriskShouldEqualASTERISK() {
        var l = new Lexer("*");
        assertEquals(Token.ASTERISK, l.nextToken().type);
    }

    @Test
    void slashShouldEqualSLASH() {
        var l = new Lexer("/");
        assertEquals(Token.SLASH, l.nextToken().type);
    }

    @Test
    void commaShouldEqualCOMMA() {
        var l = new Lexer(",");
        assertEquals(Token.COMMA, l.nextToken().type);
    }

    @Test
    void semicolonShouldEqualSEMICOLON() {
        var l = new Lexer(";");
        assertEquals(Token.SEMICOLON, l.nextToken().type);
    }

    @Test
    void lparenShouldEqualLPAREN() {
        var l = new Lexer("(");
        assertEquals(Token.LPAREN, l.nextToken().type);
    }

    @Test
    void rparenShouldEqualRPAREN() {
        var l = new Lexer(")");
        assertEquals(Token.RPAREN, l.nextToken().type);
    }

    @Test
    void lbraceShouldEqualLBRACE() {
        var l = new Lexer("{");
        assertEquals(Token.LBRACE, l.nextToken().type);
    }

    @Test
    void rbraceShouldEqualRBRACE() {
        var l = new Lexer("}");
        assertEquals(Token.RBRACE, l.nextToken().type);
    }

    @Test
    void lessThanShouldEqualLT() {
        var l = new Lexer("<");
        assertEquals(Token.LT, l.nextToken().type);
    }

    @Test
    void greaterThanShouldEqualGT() {
        var l = new Lexer(">");
        assertEquals(Token.GT, l.nextToken().type);
    }

    @Test
    void doubleEqualsShouldEqualEQ() {
        var l = new Lexer("==");
        assertEquals(Token.EQ, l.nextToken().type);
    }

    @Test
    void exclamationEqualsShouldEqualNOTEQ() {
        var l = new Lexer("!=");
        assertEquals(Token.NOTEQ, l.nextToken().type);
    }

    @Test
    void letShouldEqualLET() {
        var l = new Lexer("let");
        assertEquals(Token.LET, l.nextToken().type);
    }

    @Test
    void fnShouldEqualFUNCTION() {
        var l = new Lexer("fn");
        assertEquals(Token.FUNCTION, l.nextToken().type);
    }

    @Test
    void trueShouldEqualTRUE() {
        var l = new Lexer("true");
        assertEquals(Token.TRUE, l.nextToken().type);
    }

    @Test
    void falseShouldEqualFALSE() {
        var l = new Lexer("false");
        assertEquals(Token.FALSE, l.nextToken().type);
    }

    @Test
    void ifShouldEqualIF() {
        var l = new Lexer("if");
        assertEquals(Token.IF, l.nextToken().type);
    }

    @Test
    void elseShouldEqualELSE() {
        var l = new Lexer("else");
        assertEquals(Token.ELSE, l.nextToken().type);
    }

    @Test
    void returnShouldEqualRETURN() {
        var l = new Lexer("return");
        assertEquals(Token.RETURN, l.nextToken().type);
    }

    @Test
    void addTwoNumbersLexerTestShouldReadSuccessfully() {
        String input = """
        let five = 5;
        let ten = 10;
        
        let add = fn (x, y) {
            x + y;
        };
        
        let result = add(five,  ten);""";

        String expectedOutput[] = {
            Token.LET,
            Token.IDENT,
            Token.ASSIGN,
            Token.INT,
            Token.SEMICOLON,
            Token.LET,
            Token.IDENT,
            Token.ASSIGN,
            Token.INT,
            Token.SEMICOLON,
            Token.LET,
            Token.IDENT,
            Token.ASSIGN,
            Token.FUNCTION,
            Token.LPAREN,
            Token.IDENT,
            Token.COMMA,
            Token.IDENT,
            Token.RPAREN,
            Token.LBRACE,
            Token.IDENT,
            Token.PLUS,
            Token.IDENT,
            Token.SEMICOLON,
            Token.RBRACE,
            Token.SEMICOLON,
            Token.LET,
            Token.IDENT,
            Token.ASSIGN,
            Token.IDENT,
            Token.LPAREN,
            Token.IDENT,
            Token.COMMA,
            Token.IDENT,
            Token.RPAREN,
            Token.SEMICOLON,
            Token.EOF
        };

        testMultilineInput(input, expectedOutput);
    }

    @Test
    void ifElseLexerTextShouldReadSuccessfully() {
        String input = """
        !-/*5;
        5 < 10 > 5;
        
        if (5 < 10) {
            return true;
        } else {
            return false;
        }
        """;
        String expectedOutput[] = {
            Token.BANG,
            Token.MINUS,
            Token.SLASH,
            Token.ASTERISK,
            Token.INT,
            Token.SEMICOLON,
            Token.INT,
            Token.LT,
            Token.INT,
            Token.GT,
            Token.INT,
            Token.SEMICOLON,
            Token.IF,
            Token.LPAREN,
            Token.INT,
            Token.LT,
            Token.INT,
            Token.RPAREN,
            Token.LBRACE,
            Token.RETURN,
            Token.TRUE,
            Token.SEMICOLON,
            Token.RBRACE,
            Token.ELSE,
            Token.LBRACE,
            Token.RETURN,
            Token.FALSE,
            Token.SEMICOLON,
            Token.RBRACE,
            Token.EOF
        };

        testMultilineInput(input, expectedOutput);
    }

    @Test
    void equalityLexerTestShouldBeReadSuccessfully(){
        String input = """
            10 == 10;
            10 != 9;""";
        String expectedOutput[] = {
            Token.INT,
            Token.EQ,
            Token.INT,
            Token.SEMICOLON,
            Token.INT,
            Token.NOTEQ,
            Token.INT,
            Token.SEMICOLON,
            Token.EOF
        };

        testMultilineInput(input, expectedOutput);
    }

    @Test
    void quoteFoobarQuoteShouldReturnAStringToken() {
        String input = "\"foobar\"";
        Token expectedOutput = new Token(Token.STRING, "foobar");
        
        var l = new Lexer(input);
        Token tok = l.nextToken();
        assertEquals(expectedOutput, tok);
    }
}
