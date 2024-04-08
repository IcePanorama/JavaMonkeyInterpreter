package parser;

import ast.Expression;
import ast.ExpressionStatement;
import ast.Identifier;
import ast.IntegerLiteral;
import ast.LetStatement;
import ast.PrefixExpression;
import ast.Program;
import ast.ReturnStatement;
import ast.Statement;
import lexer.Lexer;
import token.Token;

import java.util.ArrayList;
import java.util.HashMap;

class Parser {
    private Lexer l;
    private Token curToken;
    private Token peekToken;
    ArrayList<String> errors;
    private HashMap<String, PrefixParseFn> prefixParseFns;
    private HashMap<String, InfixParseFn> infixParseFns;
    private enum ExpressionType {
        //_t,
        LOWEST,
        EQUALS,
        LESSGREATER,
        SUM,
        PRODUCT,
        PREFIX,
        CALL
    };

    Parser(Lexer l) {
        this.l = l;
        errors = new ArrayList<String>();

        prefixParseFns = new HashMap<>();
        registerPrefix(Token.IDENT, this::parseIdentifier);
        registerPrefix(Token.INT, this::parseIntegerLiteral);
        registerPrefix(Token.BANG, this::parsePrefixExpression);
        registerPrefix(Token.MINUS, this::parsePrefixExpression);

        nextToken();
        nextToken();
    }

    /* Helper Functions */
    void nextToken() {
        curToken = peekToken;
        peekToken = l.nextToken();
    }
    
    boolean curTokenIs(Token t){
        return curToken.type == t.type;
    }

    boolean peekTokenIs(Token t){
        return peekToken.type == t.type;
    }

    boolean expectPeek(Token t){
        if (peekTokenIs(t)){
            nextToken();
            return true;
        }
        peekError(t);
        return false;
    }

    void peekError(Token t){
        String msg = String.format("Expected next token to be %s, got %s.", t, 
                                    peekToken.type);
        errors.add(msg);
    } 

    /* Actual Parser Stuff */
    Program parseProgram() {
        Program program = new Program();
        program.statements = new ArrayList<Statement>();

        while (!curTokenIs(new Token(Token.EOF))){
            Statement stmt = parseStatement();
            if (stmt != null){
                program.statements.add(stmt);
            }
            nextToken();
        }

        return program;
    }

    Statement parseStatement(){
        switch (curToken.type) {
            case Token.LET:
                return parseLetStatement();
            case Token.RETURN:
                return parseReturnStatement();
            default:
                return parseExpressionStatement();
        }
    }

    LetStatement parseLetStatement(){
        LetStatement stmt = new LetStatement(curToken);

        if (!expectPeek(new Token(Token.IDENT))){
            return null;
        }

        stmt.name = new Identifier(curToken, curToken.literal);

        if (!expectPeek(new Token(Token.ASSIGN))){
            return null;
        }

        //TODO: We're skipping the expressions
        //      until we encounter a semicolon
        while (!curTokenIs(new Token(Token.SEMICOLON))){
            nextToken();
        }
        
        return stmt;
    }

    ReturnStatement parseReturnStatement() {
        ReturnStatement stmt = new ReturnStatement(curToken);
        nextToken();

        //TODO: We're skipping the expression until
        //we ecounter a semicolon;
        while (!(curTokenIs(new Token(Token.SEMICOLON)))){
            nextToken();
        }

        return stmt;
    }

    void registerPrefix(String tokenType, PrefixParseFn fn) {
        this.prefixParseFns.put(tokenType, fn);
    }

    void registerInfix(String tokenType, InfixParseFn fn) {
        this.infixParseFns.put(tokenType, fn);
    }

    ExpressionStatement parseExpressionStatement() {
        ExpressionStatement stmt = new ExpressionStatement(curToken);
        stmt.expression = parseExpression(ExpressionType.LOWEST);

        if (peekTokenIs(new Token(Token.SEMICOLON))) {
            nextToken();
        }

        return stmt;
    }

    private Expression parseExpression(ExpressionType precedence) {
        var prefix = prefixParseFns.get(curToken.type);

        if (prefix == null) {
            noPrefixParseFnError(curToken.type);
            return null;
        }

        return prefix.call();
    }

    private void noPrefixParseFnError(String tokenType) {
        errors.add(String.format(
                    "No prefix parse function for %s found.", 
                    tokenType)
                );
    }

    private Expression parseIdentifier() {
        return new Identifier(curToken, curToken.literal);
    }

    private Expression parseIntegerLiteral() {
        var lit = new IntegerLiteral(curToken);

        long value;
        try {
            value = Long.parseLong(curToken.literal);
        }
        catch (NumberFormatException e) {
            errors.add(String.format(
                        "Could not parse %s as a long.",
                        curToken.literal
                    ));
            return null;
        }

        lit.value = value;
        return lit;
    }

    Expression parsePrefixExpression() {
        PrefixExpression expr = new PrefixExpression(curToken, 
                                                     curToken.literal);
        nextToken();
        expr.right = parseExpression(ExpressionType.PREFIX);
        return expr;
    }
}
