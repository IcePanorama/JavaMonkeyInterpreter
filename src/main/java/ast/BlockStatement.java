package ast;

import java.util.ArrayList;

import token.Token;

public class BlockStatement implements Statement{
    Token token;
    public ArrayList<Statement> statements;

    public BlockStatement(Token token) {
        this.token = token;
    }

    public void statementNode() {}

    public String TokenLiteral() {
        return token.literal;
    }

    public String toString() {
        String output = new String();

        for (var stmt : statements) {
            output += stmt.toString();
        }

        return output;
    }
}
