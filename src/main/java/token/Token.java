package token;

public class Token {
    String type;
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
}