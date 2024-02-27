package token;

public class Token {
    String type;
    String literal;

    /* Token Types */
    final static String ILLEGAL = "ILLEGAL";
    final static String EOF = "EOF";
    final static String IDENT = "IDENT";
    final static String INT = "INT";
    final static String ASSIGN = "=";
    final static String PLUS = "+";
    final static String COMMA = ",";
    final static String SEMICOLON = ";";
    final static String LPAREN = "(";
    final static String RPAREN = ")";
    final static String LBRACE = "{";
    final static String RBRACE = "}";
    final static String FUNCTION = "FUNCTION";
    final static String LET = "LET";
}