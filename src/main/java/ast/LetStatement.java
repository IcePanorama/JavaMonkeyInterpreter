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

    public String toString() {
        String output = TokenLiteral() + " " + name.toString() + " = ";
        if (value != null)
        {
            output += value.toString();
        }
        return output + ";";
    }
}
