package ast;

import token.Token;

public class IfExpression implements Expression{
    Token token;
    public Expression condition;
    public BlockStatement consequence;
    public BlockStatement alternative;

    public IfExpression(Token token) {
        this.token = token;
    }

    public void expressionNode() {}

    public String getTokenLiteral() {
        return token.literal;
    }

    public String toString() {
        String output = "if";

        output += condition.toString();
        output += " ";
        output += consequence.toString();

        if (alternative != null) {
            output += "else ";
            output += alternative.toString();
        }

        return output;
    }
}
