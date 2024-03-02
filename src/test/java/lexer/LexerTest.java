package lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import token.Token;

public class LexerTest {
    @Test
    void eofShouldEqualEOF() {
        String input = "";
        var l = new Lexer(input);
        assertEquals(Token.EOF, l.nextToken().type);
    }

    @Test
    void identFiveShouldEqualIDENT() {
        String input = "five";
        var l = new Lexer(input);
        assertEquals(Token.IDENT, l.nextToken().type);
    }

    @Test
    void numFiveShouldEqualINT() {
        String input = "5";
        var l = new Lexer(input);
        assertEquals(Token.INT, l.nextToken().type);
    }

    @Test
    void assignShouldEqualASSIGN() {
        String input = "=";
        var l = new Lexer(input);
        assertEquals(Token.ASSIGN, l.nextToken().type);
    }

    @Test
    void plusShouldEqualPLUS() {
        String input = "+";
        var l = new Lexer(input);
        assertEquals(Token.PLUS, l.nextToken().type);
    }

    @Test
    void minusShouldEqualMINUS() {
        String input = "-";
        var l = new Lexer(input);
        assertEquals(Token.MINUS, l.nextToken().type);
    }

    @Test
    void exclamationShouldEqualBang() {
        String input = "!";
        var l = new Lexer(input);
        assertEquals(Token.BANG, l.nextToken().type);
    }

    @Test
    void asteriskShouldEqualASTERISK() {
        String input = "*";
        var l = new Lexer(input);
        assertEquals(Token.ASTERISK, l.nextToken().type);
    }

    @Test
    void slashShouldEqualSLASH() {
        String input = "/";
        var l = new Lexer(input);
        assertEquals(Token.SLASH, l.nextToken().type);
    }

    @Test
    void commaShouldEqualCOMMA() {
        String input = ",";
        var l = new Lexer(input);
        assertEquals(Token.COMMA, l.nextToken().type);
    }

    @Test
    void semicolonShouldEqualSEMICOLON() {
        String input = ";";
        var l = new Lexer(input);
        assertEquals(Token.SEMICOLON, l.nextToken().type);
    }

    @Test
    void lparenShouldEqualLPAREN() {
        String input = "(";
        var l = new Lexer(input);
        assertEquals(Token.LPAREN, l.nextToken().type);
    }

    @Test
    void rparenShouldEqualRPAREN() {
        String input = ")";
        var l = new Lexer(input);
        assertEquals(Token.RPAREN, l.nextToken().type);
    }

    @Test
    void lbraceShouldEqualLBRACE() {
        String input = "{";
        var l = new Lexer(input);
        assertEquals(Token.LBRACE, l.nextToken().type);
    }

    @Test
    void rbraceShouldEqualRBRACE() {
        String input = "}";
        var l = new Lexer(input);
        assertEquals(Token.RBRACE, l.nextToken().type);
    }

    @Test
    void lessThanShouldEqualLT() {
        String input = "<";
        var l = new Lexer(input);
        assertEquals(Token.LT, l.nextToken().type);
    }

    @Test
    void greaterThanShouldEqualGT() {
        String input = ">";
        var l = new Lexer(input);
        assertEquals(Token.GT, l.nextToken().type);
    }

    @Test
    void letShouldEqualLET() {
        String input = "let";
        var l = new Lexer(input);
        assertEquals(Token.LET, l.nextToken().type);
    }

    @Test
    void fnShouldEqualFUNCTION() {
        String input = "fn";
        var l = new Lexer(input);
        assertEquals(Token.FUNCTION, l.nextToken().type);
    }

    @Test
    void addTwoNumbersLexerTestShouldParseSucessfully() {
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

        var l = new Lexer(input);
        for (int i = 0; i < expectedOutput.length; i++){
            Token tok = l.nextToken();
            assertEquals(expectedOutput[i], tok.type);
        }
    }

    @Test
    void ifElseLexerTextShouldParseSuccessfully() {
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
            Token.RBRACE
        };

        var l = new Lexer(input);
        for (int i = 0; i < expectedOutput.length; i++) {
            Token tok = l.nextToken();
            assertEquals(expectedOutput[i], tok.type);
        }
    }
}
