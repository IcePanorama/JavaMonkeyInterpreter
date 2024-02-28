package lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import token.Token;

public class LexerTest {
    @Test
    void assignShouldEqualASSIGN() {
        String input = "=";
        var l = new Lexer(input);
        assertEquals(Token.ASSIGN, l.nextToken());
    }

    @Test
    void plusShouldEqualPLUS() {
        String input = "+";
        var l = new Lexer(input);
        assertEquals(Token.PLUS, l.nextToken());
    }

    @Test
    void lparenShouldEqualLPAREN() {
        String input = "(";
        var l = new Lexer(input);
        assertEquals(Token.LPAREN, l.nextToken());
    }

    @Test
    void rparenShouldEqualRPAREN() {
        String input = ")";
        var l = new Lexer(input);
        assertEquals(Token.RPAREN, l.nextToken());
    }

    @Test
    void commaShouldEqualCOMMA() {
        String input = ",";
        var l = new Lexer(input);
        assertEquals(Token.COMMA, l.nextToken());
    }

    @Test
    void semicolonShouldEqualSEMICOLON() {
        String input = ";";
        var l = new Lexer(input);
        assertEquals(Token.SEMICOLON, l.nextToken());
    }

    @Test
    void eofShouldEqualEOF() {
        String input = "";
        var l = new Lexer(input);
        assertEquals(Token.EOF, l.nextToken());
    }
}
