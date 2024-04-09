package ast;

import token.Token;

public class Bool implements Expression{
    Token token;
    boolean value;

    public Bool () {}

    public void expressionNode() {}

    public String TokenLiteral() { return token.literal; }

    public String toString() { return token.literal; }
}
