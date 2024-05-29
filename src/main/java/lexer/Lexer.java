package lexer;

import token.Token;

public class Lexer {
    String input;
    int position;       // points to current char
    int readPosition;   // after current char
    char ch;

    public Lexer (String input){
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

    char peekChar() {
        if (readPosition >= input.length()) {
            return '\0';
        }
        return input.charAt(readPosition);
    }

    public Token nextToken () {
        Token token = null;

        skipWhitespace();

        switch (ch) {
            case '\0':
                token = new Token(Token.EOF, Character.toString(ch));
                break;
            case '=':
                if (peekChar() == '=') {
                    char firstCh = ch;
                    readChar();
                    token = new Token(Token.EQ, firstCh + "" + ch);
                    break;
                }
                token = new Token(Token.ASSIGN, Character.toString(ch));
                break;
            case '+':
                token = new Token(Token.PLUS, Character.toString(ch));
                break;
            case '-':
                token = new Token(Token.MINUS, Character.toString(ch));
                break;
            case '!':
                if (peekChar() == '=') {
                    char firstCh = ch;
                    readChar();
                    token = new Token(Token.NOTEQ, firstCh + "" + ch);
                    break;
                }
                token = new Token(Token.BANG, Character.toString(ch));
                break;
            case '*':
                token = new Token(Token.ASTERISK, Character.toString(ch));
                break;
            case '/':
                token = new Token(Token.SLASH, Character.toString(ch));
                break;
            case ',':
                token = new Token(Token.COMMA, Character.toString(ch));
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
            case '{':
                token = new Token(Token.LBRACE, Character.toString(ch));
               break;
            case '}':
                token = new Token(Token.RBRACE, Character.toString(ch));
                break;
            case '<':
                token = new Token(Token.LT, Character.toString(ch));
                break;
            case '>':
                token = new Token(Token.GT, Character.toString(ch));
                break;
            case '\"':
                token = new Token(Token.STRING);
                token.literal = readString();
                break;
            default:
                if (isLetter(ch)) {
                    String literal = readIdentifier();
                    // must return here as we don't want to readChar below
                    return new Token(Token.lookupIdent(literal), literal);
                } else if (Character.isDigit(ch)){
                    return new Token(Token.INT, readNumber());
                }else {
                    token = new Token(Token.ILLEGAL, "ILLEGAL");
                }
        }

        readChar();
        return token;
    }

    String readIdentifier() {
        int start_pos = position;
        while (isLetter(ch)){
            readChar();
        }

        return input.substring(start_pos, position);
    }

    String readNumber() {
        int start_pos = position;
        while (Character.isDigit(ch)){
            readChar();
        }
        return input.substring(start_pos, position);
    }

    boolean isLetter(char ch){
        return 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || ch == '_';
    }

    void skipWhitespace() {
        while (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
            readChar();
        }
    }

    String readString() {
        int pos = position + 1;
        while (true) {
            readChar();
            if (ch == '\"' || ch == 0) {
                break;
            }
        }
        return input.substring(pos, position);
    }
}
