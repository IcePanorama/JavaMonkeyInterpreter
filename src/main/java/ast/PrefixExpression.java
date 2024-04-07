package ast;

import token.Token;

public class PrefixExpression implements Expression{
    Token token;
    String operator;
    Expression right;

    public void expressionNode() {}

    public String TokenLiteral() { return token.literal; }

    public String toString() {
        return "(" + operator + right.toString() + ")";
    }
}
