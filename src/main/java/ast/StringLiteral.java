package ast;

import token.Token;

public class StringLiteral implements Expression{
    public Token token;
    public String value;

    public StringLiteral(Token token, String value) {
        this.token = token;
        this.value = value;
    }

    public void expressionNode() {}

    public String TokenLiteral() { return token.literal; }

    public String toString() { return token.literal; }
}
