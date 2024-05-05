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

    public String TokenLiteral() {
        return token.literal;
    }

    public String toString() {
        String output = TokenLiteral() + " ";
        if (returnValue != null)
        {
            output += returnValue.toString();
        }
        return output + ";";
    }
}