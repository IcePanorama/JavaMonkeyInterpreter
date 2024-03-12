package ast;

import token.Token;

public class LetStatement implements Statement {
    Token token;
    public Identifier name;
    Expression value;

    public LetStatement(Token token){
        this.token = token;
    }

    public void statementNode(){
        return;
    }

    public String TokenLiteral(){
        return token.literal;
    }
}
