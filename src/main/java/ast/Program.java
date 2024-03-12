package ast;

import java.util.ArrayList;

public class Program {
    public ArrayList<Statement> statements;

    String TokenLiteral() {
        if (statements.size() > 0){
            return statements.get(0).TokenLiteral();
        }
        return "";
    }
}
