package lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import token.Token;

//TODO:refactor this whole file sometime
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
        assertEquals(new Token(Token.EOF, null), l.nextToken());
    }

    @Test
    void identFiveShouldEqualIDENT() {
        var l = new Lexer("five");
        assertEquals(new Token(Token.IDENT, "five"), l.nextToken());
    }

    @Test
    void numFiveShouldEqualINT() {
        var l = new Lexer("5");
        assertEquals(new Token(Token.INT, "5"), l.nextToken());
    }

    @Test
    void assignShouldEqualASSIGN() {
        var l = new Lexer("=");
        assertEquals(new Token(Token.ASSIGN, "="), l.nextToken());
    }

    @Test
    void plusShouldEqualPLUS() {
        var l = new Lexer("+");
        assertEquals(new Token(Token.PLUS, "+"), l.nextToken());
    }

    @Test
    void minusShouldEqualMINUS() {
        var l = new Lexer("-");
        assertEquals(new Token(Token.MINUS, "-"), l.nextToken());
    }

    @Test
    void exclamationShouldEqualBang() {
        var l = new Lexer("!");
        assertEquals(new Token(Token.BANG, "!"), l.nextToken());
    }

    @Test
    void asteriskShouldEqualASTERISK() {
        var l = new Lexer("*");
        assertEquals(new Token(Token.ASTERISK, "*"), l.nextToken());
    }

    @Test
    void slashShouldEqualSLASH() {
        var l = new Lexer("/");
        assertEquals(new Token(Token.SLASH, "/"), l.nextToken());
    }

    @Test
    void commaShouldEqualCOMMA() {
        var l = new Lexer(",");
        assertEquals(new Token(Token.COMMA, ","), l.nextToken());
    }

    @Test
    void colonShouldEqualCOLON() {
        var l = new Lexer(":");
        assertEquals(new Token(Token.COLON, ":"), l.nextToken());
    }

    @Test
    void semicolonShouldEqualSEMICOLON() {
        var l = new Lexer(";");
        assertEquals(new Token(Token.SEMICOLON, ";"), l.nextToken());
    }

    @Test
    void lparenShouldEqualLPAREN() {
        var l = new Lexer("(");
        assertEquals(new Token(Token.LPAREN, "("), l.nextToken());
    }

    @Test
    void rparenShouldEqualRPAREN() {
        var l = new Lexer(")");
        assertEquals(new Token(Token.RPAREN, ")"), l.nextToken());
    }

    @Test
    void lbraceShouldEqualLBRACE() {
        var l = new Lexer("{");
        assertEquals(new Token(Token.LBRACE, "{"), l.nextToken());
    }

    @Test
    void rbraceShouldEqualRBRACE() {
        var l = new Lexer("}");
        assertEquals(new Token(Token.RBRACE, "}"), l.nextToken());
    }

    @Test
    void lessThanShouldEqualLT() {
        var l = new Lexer("<");
        assertEquals(new Token(Token.LT, "<"), l.nextToken());
    }

    @Test
    void greaterThanShouldEqualGT() {
        var l = new Lexer(">");
        assertEquals(new Token(Token.GT, ">"), l.nextToken());
    }

    @Test
    void doubleEqualsShouldEqualEQ() {
        var l = new Lexer("==");
        assertEquals(new Token(Token.EQ, "=="), l.nextToken());
    }

    @Test
    void exclamationEqualsShouldEqualNOTEQ() {
        var l = new Lexer("!=");
        assertEquals(new Token(Token.NOTEQ, "!="), l.nextToken());
    }

    @Test
    void letShouldEqualLET() {
        var l = new Lexer("let");
        assertEquals(new Token(Token.LET, "let"), l.nextToken());
    }

    @Test
    void fnShouldEqualFUNCTION() {
        var l = new Lexer("fn");
        assertEquals(new Token(Token.FUNCTION, "fn"), l.nextToken());
    }

    @Test
    void trueShouldEqualTRUE() {
        var l = new Lexer("true");
        assertEquals(new Token(Token.TRUE, "true"), l.nextToken());
    }

    @Test
    void falseShouldEqualFALSE() {
        var l = new Lexer("false");
        assertEquals(new Token(Token.FALSE, "false"), l.nextToken());
    }

    @Test
    void ifShouldEqualIF() {
        var l = new Lexer("if");
        assertEquals(new Token(Token.IF, "if"), l.nextToken());
    }

    @Test
    void elseShouldEqualELSE() {
        var l = new Lexer("else");
        assertEquals(new Token(Token.ELSE, "else"), l.nextToken());
    }

    @Test
    void returnShouldEqualRETURN() {
        var l = new Lexer("return");
        assertEquals(new Token(Token.RETURN, "return"), l.nextToken());
    }

    @Test
    void lbracketShouldEqualLBRACKET() {
        var l = new Lexer("[");
        assertEquals(new Token(Token.LBRACKET, "["), l.nextToken());
    }

    @Test
    void rbracketShouldEqualRBRACKET() {
        var l = new Lexer("]");
        assertEquals(new Token(Token.RBRACKET, "]"), l.nextToken());
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

    @Test
    void quoteFooSpaceBarQuoteShouldReturnAStringToken() {
        String input = "\"foo bar\"";
        Token expectedOutput = new Token(Token.STRING, "foo bar");
        
        var l = new Lexer(input);
        Token tok = l.nextToken();
        assertEquals(expectedOutput, tok);
    }

    @Test
    void exampleArrayShouldBeReadCorrectly() {
        String input = "[1, 2];";
        Token expectedOutput[] = {
            new Token(Token.LBRACKET, "["),
            new Token(Token.INT, "1"),
            new Token(Token.COMMA, ","),
            new Token(Token.INT, "2"),
            new Token(Token.RBRACKET, "]"),
            new Token(Token.SEMICOLON, ";"),
            new Token(Token.EOF)
        };

        Lexer l = new Lexer(input);
        for (Token tok : expectedOutput) {
            assertEquals(tok, l.nextToken());
        }
    }

    @Test
    void examleHashLiteralShouldBeReadCorrectly() {
        String input = "{\"foo\": \"bar\"}";
        Token expectedOutput[] = {
            new Token(Token.LBRACE, "{"),
            new Token(Token.STRING, "foo"),
            new Token(Token.COLON, ":"),
            new Token(Token.STRING, "bar"),
            new Token(Token.RBRACE, "}"),
            new Token(Token.EOF)
        };

        Lexer l = new Lexer(input);
        for (Token tok : expectedOutput) {
            assertEquals(tok, l.nextToken());
        }
    }
}
