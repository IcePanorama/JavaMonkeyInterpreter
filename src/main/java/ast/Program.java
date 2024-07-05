package ast;

public class Program implements Node{
    public Statement[] statements;

    public String getTokenLiteral() {
        if (statements.length > 0){
            return statements[0].getTokenLiteral();
        }
        return "";
    }

    @Override
    public String toString() {
        System.out.println("In my toString() method!");
        String[] strStmts = new String[statements.length];

        int i = 0;
        for (Statement stmt : statements) {
            strStmts[i] = stmt.toString();
            i++;
        }
        System.out.println(strStmts);

        return "[" + String.join(", ", strStmts) + "]";
    }
}