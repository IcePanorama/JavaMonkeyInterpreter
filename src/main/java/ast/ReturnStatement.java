package ast;

import token.Token;

public class ReturnStatement implements Statement{
    Token token;
    Expression returnValue;

    public ReturnStatement (Token token) {
        this.token = token;
    }

    public void statementNode() {
        return;
    }

    public String TokenLiteral() {
        return token.literal;
    }
}
