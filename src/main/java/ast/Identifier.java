package ast;

import token.Token;

class Identifier implements Expression {
    Token token;
    String value;

    public void expressionNode() {
        return;
    }

    public String TokenLiteral() {
        return token.literal;
    }
}
