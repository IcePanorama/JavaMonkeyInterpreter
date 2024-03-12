package ast;

import token.Token;

public class Identifier implements Expression {
    Token token;
    public String value;

    public void expressionNode() {
        return;
    }

    public String TokenLiteral() {
        return token.literal;
    }
}
