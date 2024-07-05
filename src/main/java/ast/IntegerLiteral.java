package ast;

import token.Token;

public class IntegerLiteral implements Expression{
    Token token;
    public long value;

    public IntegerLiteral(Token token) {
        this.token = token;
    }

    public void expressionNode() {}

    public String getTokenLiteral() { return token.literal; }

    public String toString() { return token.literal; }
}
