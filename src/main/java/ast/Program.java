package ast;

class Program {
    Statement statements[];

    String TokenLiteral() {
        if (statements.length > 0){
            return statements[0].TokenLiteral();
        }
        return "";
    }
}
