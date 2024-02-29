package lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import token.Token;

public class LexerTest {
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
    void eofShouldEqualEOF() {
        String input = "";
        var l = new Lexer(input);
        assertEquals(Token.EOF, l.nextToken().type);
    }

    @Test
    void letShouldEqualLET() {
        String input = "let";
        var l = new Lexer(input);
        assertEquals(Token.LET, l.nextToken().type);
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
    void fnShouldEqualFUNCTION() {
        String input = "fn";
        var l = new Lexer(input);
        assertEquals(Token.FUNCTION, l.nextToken().type);
    }

    @Test
    void addTwoNumbersLexerTestShouldPass() {
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
}
