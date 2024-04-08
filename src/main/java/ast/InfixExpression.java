package ast;

import token.Token;

public class InfixExpression implements Expression{
    Token token;
    public Expression left;
    public String operator;
    public Expression right;

    public InfixExpression() {
    }

    public void expressionNode() {}

    public String TokenLiteral() { return token.literal; }

    public String toString() {
        return "(" + left.toString() + " " + operator + " " + right.toString()
                + ")";
    }
}
