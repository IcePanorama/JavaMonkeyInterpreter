package ast;

//import java.util.ArrayList;

import token.Token;

public class BlockStatement implements Statement{
    Token token;
    //public ArrayList<Statement> statements;
    public Statement[] statements;

    public BlockStatement(Token token) {
        this.token = token;
    }

    public void statementNode() {}

    public String getTokenLiteral() {
        return token.literal;
    }

    @Override
    public String toString() {
        String output = new String();

        for (Statement stmt : statements) {
            output += stmt.toString();
        }

        return output;
    }
}
