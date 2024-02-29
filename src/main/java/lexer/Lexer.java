package lexer;

import token.Token;

public class Lexer {
    String input;
    int position;       // points to current char
    int readPosition;   // after current char
    char ch;

    Lexer (String input){
        this.input = input;
        readChar();
    }

    void readChar () {
        if (readPosition >= input.length())
            ch = '\0';
        else
            ch = input.charAt(readPosition);
        position = readPosition;
        readPosition++;
    }

    Token nextToken () {
        Token token = null;

        switch (ch) {
            case '=':
                token = new Token(Token.ASSIGN, Character.toString(ch));
                break;
            case ';':
                token = new Token(Token.SEMICOLON, Character.toString(ch));
                break;
            case '(':
                token = new Token(Token.LPAREN, Character.toString(ch));
                break;
            case ')':
                token = new Token(Token.RPAREN, Character.toString(ch));
                break;
            case ',':
                token = new Token(Token.COMMA, Character.toString(ch));
                break;
            case '+':
                token = new Token(Token.PLUS, Character.toString(ch));
                break;
            case '{':
                token = new Token(Token.LBRACE, Character.toString(ch));
               break;
            case '}':
                token = new Token(Token.RBRACE, Character.toString(ch));
                break;
            case '\0':
                token = new Token(Token.EOF, Character.toString(ch));
                break;
            default:
                if (Character.isLetter(ch)) {
                }
        }

        readChar();
        return token;
    }
}
