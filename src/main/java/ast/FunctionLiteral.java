package ast;

import java.util.ArrayList;

import token.Token;

public class FunctionLiteral implements Expression{
    Token token;
    ArrayList<Identifier> parameters;
    BlockStatement body;

    public FunctionLiteral() {}

    public void expressionNode() {}

    public String TokenLiteral() {
        return token.literal;
    }

    public String toString() {
        String output = TokenLiteral();

        String[] params = new String[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            params[i] = parameters.get(i).toString();
        }

        output += "(";
        output += String.join(", ", params);
        output += ") ";
        output += body.toString();

        return output;
    }
}
