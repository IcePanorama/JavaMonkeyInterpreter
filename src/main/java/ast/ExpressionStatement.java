package ast;

import token.Token;

public class ExpressionStatement implements Statement{
   Token token; 
   Expression expression;

   public void statementNode() {}

   public String TokenLiteral() {
    return token.literal;
   }
}
