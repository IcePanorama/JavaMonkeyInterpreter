package ast;

import java.util.ArrayList;

import token.Token;

public class CallExpression implements Expression{
    Token token;
    public Expression function;
    public ArrayList<Expression> arguments;

    public CallExpression(Token token, Expression function) {
        this.token = token;
        this.function = function;
    }

    public void expressionNode() {}

    public String TokenLiteral() {
        return token.literal;
    }

    public String toString() {
        String output = function.toString();

        String[] args = new String[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            args[i] = arguments.get(i).toString();
        }

        output += "(";
        output += String.join(", ", args);
        output += ")";

        return output;
    }
}
