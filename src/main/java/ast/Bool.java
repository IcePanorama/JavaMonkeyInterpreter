package ast;

import token.Token;

public class Bool implements Expression{
    Token token;
    public boolean value;

    public Bool (Token token, boolean value) {
        this.token = token;
        this.value = value;
    }

    public void expressionNode() {}

    public String TokenLiteral() { return token.literal; }

    public String toString() { return token.literal; }
}
