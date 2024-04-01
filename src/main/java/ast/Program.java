package ast;

import java.util.ArrayList;

public class Program implements Node{
    public ArrayList<Statement> statements;

    public String TokenLiteral() {
        if (statements.size() > 0){
            return statements.get(0).TokenLiteral();
        }
        return "";
    }

    public String toString() {
        return statements.toString();
    }
}