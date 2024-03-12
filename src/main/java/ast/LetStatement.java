package ast;

import token.Token;

class LetStatement implements Statement {
    Token token;
    Identifier name;
    Expression value;

    public void statementNode(){
        return;
    }

    public String TokenLiteral(){
        return token.literal;
    }
}
