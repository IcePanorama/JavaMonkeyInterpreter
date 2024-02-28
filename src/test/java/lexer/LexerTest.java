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
}
