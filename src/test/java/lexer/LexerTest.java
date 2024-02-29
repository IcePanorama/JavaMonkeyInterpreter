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
        String input = "five";
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
        
        let result = add(five,  ten)""";

        Token expectedOutput[] = {
            new Token(Token.LET, "let"),
            new Token(Token.IDENT, "five"),
            new Token(Token.ASSIGN, "="),
            new Token(Token.INT, "5"),
            new Token(Token.SEMICOLON, ";"),
            new Token(Token.LET, "let"),
            new Token(Token.IDENT, "ten"),
            new Token(Token.ASSIGN, "="),
            new Token(Token.INT, "10"),
            new Token(Token.SEMICOLON, ";"),
            new Token(Token.LET, "let"),
            new Token(Token.IDENT, "add"),
            new Token(Token.ASSIGN, "="),
            new Token(Token.FUNCTION, "fn"),
            new Token(Token.LPAREN, "("),
            new Token(Token.IDENT, "x"),
            new Token(Token.COMMA, ","),
            new Token(Token.IDENT, "y"),
            new Token(Token.RPAREN, ")"),
            new Token(Token.LBRACE, "{"),
            new Token(Token.IDENT, "x"),
            new Token(Token.PLUS, "+"),
            new Token(Token.IDENT, "y"),
            new Token(Token.SEMICOLON, ";"),
            new Token(Token.RBRACE, "}"),
            new Token(Token.SEMICOLON, ";"),
            new Token(Token.LET, "let"),
            new Token(Token.IDENT, "result"),
            new Token(Token.ASSIGN, "="),
            new Token(Token.IDENT, "add"),
            new Token(Token.LPAREN, "("),
            new Token(Token.IDENT, "five"),
            new Token(Token.COMMA, ","),
            new Token(Token.IDENT, "ten"),
            new Token(Token.RPAREN, ")"),
            new Token(Token.SEMICOLON, ";"),
            new Token(Token.EOF, "")
        };

        var l = new Lexer(input);
        for (int i = 0; i < expectedOutput.length; i++){
            Token tok = l.nextToken();
            assertEquals(expectedOutput[i], tok);
        }
    }
}
