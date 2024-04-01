package ast;

import token.Token;

public class ExpressionStatement implements Statement{
   Token token; 
   public Expression expression;

   public ExpressionStatement(Token token) {
      this.token = token;
   }

   public void statementNode() {}

   public String TokenLiteral() {
    return token.literal;
   }

   public String toString() {
      return expression == null ? "" : expression.toString();
   }
}
