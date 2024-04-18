package ast;

import java.util.ArrayList;

import token.Token;

public class BlockStatment implements Statement{
    Token token;
    ArrayList<Statement> statements;

    BlockStatment() {}

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
