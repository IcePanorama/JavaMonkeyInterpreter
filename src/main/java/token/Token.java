package token;

import java.util.Map;

public class Token {
    public String type;
    String literal;

    /* Token Types */
    public final static String ILLEGAL = "ILLEGAL";
    public final static String EOF = "EOF";
    public final static String IDENT = "IDENT";
    public final static String INT = "INT";
    public final static String ASSIGN = "=";
    public final static String PLUS = "+";
    public final static String COMMA = ",";
    public final static String SEMICOLON = ";";
    public final static String LPAREN = "(";
    public final static String RPAREN = ")";
    public final static String LBRACE = "{";
    public final static String RBRACE = "}";
    public final static String FUNCTION = "FUNCTION";
    public final static String LET = "LET";
    public final static Map<String, String> keywords = Map.of(
        "fn", FUNCTION, 
        "let", LET
    );

    public Token (String type, String literal){
        this.type = type;
        this.literal = literal;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Token)){
            return false;
        }

        return (type.equals(((Token)(o)).type) && 
                literal.equals(((Token)(o)).literal));
    }

    public static String lookupIdent(String ident) {
        if (keywords.containsKey(ident)){
            return keywords.get(ident);
        }
        return IDENT;
    }
}