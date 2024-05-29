package token;

import java.util.Map;

public class Token {
    public String type;
    public String literal;

    /* Token Types */
    public final static String ILLEGAL = "ILLEGAL";
    public final static String EOF = "EOF";
    public final static String IDENT = "IDENT";
    public final static String INT = "INT";
    public final static String ASSIGN = "=";
    public final static String PLUS = "+";
    public final static String MINUS = "-";
    public final static String BANG = "!";
    public final static String ASTERISK = "*";
    public final static String SLASH = "/";
    public final static String COMMA = ",";
    public final static String SEMICOLON = ";";
    public final static String LPAREN = "(";
    public final static String RPAREN = ")";
    public final static String LBRACE = "{";
    public final static String RBRACE = "}";
    public final static String LT = "<";
    public final static String GT = ">";
    public final static String EQ = "==";
    public final static String NOTEQ = "!=";
    public final static String FUNCTION = "FUNCTION";
    public final static String LET = "LET";
    public final static String TRUE = "TRUE";
    public final static String FALSE = "FALSE";
    public final static String IF = "IF";
    public final static String ELSE = "ELSE";
    public final static String RETURN = "RETURN";
    public final static String STRING = "STRING";
    public final static Map<String, String> keywords = Map.of(
        "fn", FUNCTION, 
        "let", LET,
        "true", TRUE,
        "false", FALSE,
        "if", IF,
        "else", ELSE,
        "return", RETURN
    );

    public Token (String type){
        this.type = type;
    }

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

    @Override
    public String toString() {
        if (literal == null){
            return type;
        }
        return "{Type:" + type + " Literal:" + literal+ "}";
    }
}