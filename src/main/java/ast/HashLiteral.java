package ast;

import java.util.HashMap;

import token.Token;

public class HashLiteral implements Expression{
    Token token;
    public HashMap<Expression, Expression> pairs;

    public HashLiteral(Token token) {
        this.token = token;
    }

    public void expressionNode() {}

    public String TokenLiteral() { return token.literal; }

    public String toString() {
        String[] items = new String[pairs.size()];
        int i = 0;
        for (var key: pairs.keySet()) {
            items[i] = key.toString() + ":" + pairs.get(key).toString();
            i++;
        }

        return "{" + String.join(",", items) + "}";
    }
}
