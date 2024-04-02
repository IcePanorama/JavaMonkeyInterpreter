package ast;

import token.Token;

public class IntegerLiteral implements Expression{
    Token token;
    long value;

    public void expressionNode() {}

    public String TokenLiteral() { return token.literal; }

    public String toString() { return token.literal; }
}
