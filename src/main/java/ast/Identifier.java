package ast;

import token.Token;

public class Identifier implements Expression {
    Token token;
    public String value;

    public Identifier(Token token, String value){
        this.token = token;
        this.value = value;
    }

    public void expressionNode() {
        return;
    }

    public String getTokenLiteral() {
        return token.literal;
    }

    public String toString() {
        return value;
    }
}
