package ast;

import token.Token;

public class IndexExpression implements Expression{
    Token token;
    public Expression left;
    public Expression index;

    public IndexExpression(Token token, Expression left) {
        this.token = token;
        this.left = left;
    }

    public void expressionNode() { }

    public String getTokenLiteral() { return token.literal; }

    public String toString() {
        return String.format(
            "(%s[%s])",
            left.toString(),
            index.toString()
        );
    }
}
