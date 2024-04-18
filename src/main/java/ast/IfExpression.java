package ast;

import token.Token;

public class IfExpression implements Expression{
    Token token;
    Expression condition;
    public BlockStatement consequence;
    public BlockStatement alternative;

    public IfExpression() {}

    public void expressionNode() {}

    public String TokenLiteral() {
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
