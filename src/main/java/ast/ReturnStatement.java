package ast;

import token.Token;

public class ReturnStatement implements Statement{
    Token token;
    public Expression returnValue;

    public ReturnStatement (Token token) {
        this.token = token;
    }

    public void statementNode() {
        return;
    }

    public String getTokenLiteral() {
        return token.literal;
    }

    @Override
    public String toString() {
        String output = getTokenLiteral() + " ";
        if (returnValue != null)
        {
            output += returnValue.toString();
        }
        return output + ";";
    }
}