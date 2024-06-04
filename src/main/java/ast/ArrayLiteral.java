package ast;

import token.Token;

public class ArrayLiteral implements Expression{
    public Token token;
    public Expression[] elements;

    public ArrayLiteral(Token token) {
        this.token = token;
    }

    public void expressionNode() {}

    public String TokenLiteral() { return token.literal; }

    public String toString() {
        String[] output = new String[elements.length];
        for (int i = 0; i < elements.length; i++) {
            output[i] = elements[i].toString();
        }

        return "[" + String.join(", ", output) + "]";
    }
}
