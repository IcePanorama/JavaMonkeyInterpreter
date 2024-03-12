package ast;

public class Program {
    public Statement statements[];

    String TokenLiteral() {
        if (statements.length > 0){
            return statements[0].TokenLiteral();
        }
        return "";
    }
}
