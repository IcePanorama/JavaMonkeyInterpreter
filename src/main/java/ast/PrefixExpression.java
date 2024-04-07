package ast;

import token.Token;

public class PrefixExpression implements Expression{
    Token token;
    public String operator;
    public Expression right;

    public PrefixExpression(Token token, String operator) {
        this.token = token;
        this.operator = operator;
    }

    public void expressionNode() {}

    public String TokenLiteral() { return token.literal; }

    public String toString() {
        return "(" + operator + right.toString() + ")";
    }
}
