package parser;

import ast.Expression;
import ast.ExpressionStatement;
import ast.Identifier;
import ast.LetStatement;
import ast.Program;
import ast.ReturnStatement;
import ast.Statement;
import lexer.Lexer;
import token.Token;

import java.util.ArrayList;
import java.util.HashMap;

class Parser {
    Lexer l;
    Token curToken;
    Token peekToken;
    ArrayList<String> errors;
    HashMap<String, PrefixParseFn> prefixParseFns;
    HashMap<String, InfixParseFn> infixParseFns;

    private static enum ExpressionType {
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
        if (prefix == null)
            return null;

        return prefix.call();
    } 

    private Expression parseIdentifier() {
        return new Identifier(curToken, curToken.literal);
    }
}
