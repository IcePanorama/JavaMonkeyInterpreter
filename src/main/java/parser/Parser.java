package parser;

import ast.ArrayLiteral;
import ast.BlockStatement;
import ast.Bool;
import ast.CallExpression;
import ast.Expression;
import ast.ExpressionStatement;
import ast.FunctionLiteral;
import ast.HashLiteral;
import ast.Identifier;
import ast.IfExpression;
import ast.IndexExpression;
import ast.InfixExpression;
import ast.IntegerLiteral;
import ast.LetStatement;
import ast.PrefixExpression;
import ast.Program;
import ast.ReturnStatement;
import ast.Statement;
import ast.StringLiteral;
import lexer.Lexer;
import token.Token;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    private Lexer l;
    private Token curToken;
    private Token peekToken;
    public ArrayList<String> errors;
    private HashMap<String, PrefixParseFn> prefixParseFns;
    private HashMap<String, InfixParseFn> infixParseFns;
    private HashMap<String, ExpressionType> precedences;

    private enum ExpressionType {
        LOWEST,
        EQUALS,
        LESSGREATER,
        SUM,
        PRODUCT,
        PREFIX,
        CALL,
        INDEX
    };

    public Parser(Lexer l) {
        this.l = l;
        errors = new ArrayList<String>();

        initializePrecedences();

        prefixParseFns = new HashMap<>();
        registerPrefixFns();

        infixParseFns = new HashMap<>();
        registerInfixFns();

        nextToken();
        nextToken();
    }

    public Program parseProgram() {
        Program program = new Program();
        ArrayList<Statement> statements = new ArrayList<>();

        while (!curTokenIs(new Token(Token.EOF))){
            Statement stmt = parseStatement();
            if (stmt != null){
                statements.add(stmt);
            }
            nextToken();
        }

        program.statements = statements.toArray(new Statement[0]);

        return program;
    }

    private Statement parseStatement(){
        switch (curToken.type) {
            case Token.LET:
                return parseLetStatement();
            case Token.RETURN:
                return parseReturnStatement();
            default:
                return parseExpressionStatement();
        }
    }

    private LetStatement parseLetStatement(){
        LetStatement stmt = new LetStatement(curToken);

        if (!expectPeek(new Token(Token.IDENT))){
            return null;
        }

        stmt.name = new Identifier(curToken, curToken.literal);

        if (!expectPeek(new Token(Token.ASSIGN))){
            return null;
        }

        nextToken();

        stmt.value = parseExpression(ExpressionType.LOWEST);
        
        if (peekTokenIs(new Token(Token.SEMICOLON))){
            nextToken();
        }
        
        return stmt;
    }

    private ReturnStatement parseReturnStatement() {
        ReturnStatement stmt = new ReturnStatement(curToken);

        nextToken();

        stmt.returnValue = parseExpression(ExpressionType.LOWEST);

        if (peekTokenIs(new Token(Token.SEMICOLON))){
            nextToken();
        }

        return stmt;
    }

    private ExpressionStatement parseExpressionStatement() {
        ExpressionStatement stmt = new ExpressionStatement(curToken);
        stmt.expression = parseExpression(ExpressionType.LOWEST);

        if (peekTokenIs(new Token(Token.SEMICOLON))) {
            nextToken();
        }

        return stmt;
    }

    private Expression parseStringLiteral() {
        return new StringLiteral(curToken, curToken.literal);
    }

    private Expression parseExpression(ExpressionType precedence) {
        PrefixParseFn prefix = prefixParseFns.get(curToken.type);
        if (prefix == null) {
            noPrefixParseFnError(curToken.type);
            return null;
        }
        Expression leftExpr = prefix.call();

        while (!peekTokenIs(new Token(Token.SEMICOLON)) &&
               precedence.compareTo(peekPrecedence()) < 0) {
            InfixParseFn infix = infixParseFns.get(peekToken.type);
            if (infix == null) {
                return leftExpr;
            }

            nextToken();

            leftExpr = infix.call(leftExpr);
        }

        return leftExpr;
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

    private Expression parsePrefixExpression() {
        PrefixExpression expr = new PrefixExpression(curToken, 
                                                     curToken.literal);
        nextToken();
        expr.right = parseExpression(ExpressionType.PREFIX);
        return expr;
    }

    private Expression parseInfixExpression(Expression left) {
        InfixExpression expr = new InfixExpression(curToken, curToken.literal,
                                                   left);

        ExpressionType precedence = curPrecedence();
        nextToken();
        expr.right = parseExpression(precedence);

        return expr;
    }

    private Expression parseBool() {
        return new Bool(curToken, curTokenIs(new Token(Token.TRUE)));
    }

    private Expression parseGroupedExpression() {
        nextToken();

        Expression expr = parseExpression(ExpressionType.LOWEST);

        if (!expectPeek(new Token(Token.RPAREN))) {
            return null;
        }

        return expr;
    }

    private Expression parseIfExpression() {
        IfExpression expr = new IfExpression(curToken);

        if (!expectPeek(new Token(Token.LPAREN))) {
            return null;
        }

        nextToken();
        expr.condition = parseExpression(ExpressionType.LOWEST);

        if (!expectPeek(new Token(Token.RPAREN))) {
            return null;
        }

        if (!expectPeek(new Token(Token.LBRACE))) {
            return null;
        }

        expr.consequence = parseBlockStatement();

        if (peekTokenIs(new Token(Token.ELSE))) {
            nextToken();

            if (!expectPeek(new Token(Token.LBRACE))) {
                return null;
            }

            expr.alternative = parseBlockStatement();
        }

        return expr;
    }

    private BlockStatement parseBlockStatement() {
        BlockStatement block = new BlockStatement(curToken);
        ArrayList<Statement> statements = new ArrayList<>();

        nextToken();

        while (!curTokenIs(new Token(Token.RBRACE)) && !curTokenIs(new Token(Token.EOF))) {
            Statement stmt = parseStatement();
            if (stmt != null) {
                statements.add(stmt);
            }
            nextToken();
        }

        block.statements = statements.toArray(new Statement[0]);

        return block;
    }

    private Expression parseFunctionLiteral() {
        FunctionLiteral lit = new FunctionLiteral(curToken);

        if (!expectPeek(new Token(Token.LPAREN))) {
            return null;
        }

        lit.parameters = parseFunctionParameters();

        if (!expectPeek(new Token(Token.LBRACE))) {
            return null;
        }

        lit.body = parseBlockStatement();

        return lit;
    }

    private ArrayList<Identifier> parseFunctionParameters() {
        ArrayList<Identifier> identifiers = new ArrayList<>();

        if (peekTokenIs(new Token(Token.RPAREN))) {
            nextToken();
            return identifiers;
        }

        nextToken();

        Identifier ident = new Identifier(curToken, curToken.literal);
        identifiers.add(ident);

        while (peekTokenIs(new Token(Token.COMMA))) {
            nextToken();
            nextToken();
            ident = new Identifier(curToken, curToken.literal);
            identifiers.add(ident);
        }

        if (!expectPeek(new Token(Token.RPAREN))) {
            return null;
        }

        return identifiers;
    }

    private Expression parseCallExpression(Expression function) {
        CallExpression expr = new CallExpression(curToken, function);
        expr.arguments = parseExpressionList(new Token(Token.RPAREN, ")"));
        return expr;
    }

    private Expression parseArrayLiteral() {
        ArrayLiteral arr = new ArrayLiteral(curToken);
        arr.elements = parseExpressionList(new Token(Token.RBRACKET, "]"));
        return arr;
    }

    private Expression[] parseExpressionList(Token end) {
        ArrayList<Expression> list = new ArrayList<>();

        if (peekTokenIs(end)) {
            nextToken();
            return new Expression[] {};
        }

        nextToken();
        list.add(parseExpression(ExpressionType.LOWEST));

        while (peekTokenIs(new Token(Token.COMMA, ","))) {
            nextToken();
            nextToken();
            list.add(parseExpression(ExpressionType.LOWEST));
        }

        if (!expectPeek(end)) {
            return new Expression[] {};
        }

        return list.toArray(new Expression[0]);
    }

    private Expression parseIndexExpression(Expression left) {
        IndexExpression expr = new IndexExpression(curToken, left);

        nextToken();
        expr.index = parseExpression(ExpressionType.LOWEST);

        if (!expectPeek(new Token(Token.RBRACKET))) {
            return null;
        }

        return expr;
    }

    private Expression parseHashLiteral() {
        HashLiteral hash = new HashLiteral(curToken);
        hash.pairs = new HashMap<>();

        while (!peekTokenIs(new Token(Token.RBRACE))) {
            nextToken();
            Expression key = parseExpression(ExpressionType.LOWEST);

            if (!expectPeek(new Token(Token.COLON))) {
                return null;
            }

            nextToken();
            Expression value = parseExpression(ExpressionType.LOWEST);
            hash.pairs.put(key, value);

            if (!peekTokenIs(new Token(Token.RBRACE))
                && !expectPeek(new Token(Token.COMMA))) {
                return null;
            }
        }

        if (!expectPeek(new Token(Token.RBRACE))) {
            return null;
        }

        return hash;
    }

    /* Parser Setup Functions */
    private void initializePrecedences() {
        /*
         * In order from highest to lowest precedence.
         */
        precedences = new HashMap<>();
        precedences.put(Token.LBRACKET, ExpressionType.INDEX);
        precedences.put(Token.EQ, ExpressionType.EQUALS);
        precedences.put(Token.NOTEQ, ExpressionType.EQUALS);
        precedences.put(Token.LT, ExpressionType.LESSGREATER);
        precedences.put(Token.GT, ExpressionType.LESSGREATER);
        precedences.put(Token.PLUS, ExpressionType.SUM);
        precedences.put(Token.MINUS, ExpressionType.SUM);
        precedences.put(Token.SLASH, ExpressionType.PRODUCT);
        precedences.put(Token.ASTERISK, ExpressionType.PRODUCT);
        precedences.put(Token.LPAREN, ExpressionType.CALL);
    }

    private void registerPrefixFns() {
        registerPrefix(Token.IDENT, this::parseIdentifier);
        registerPrefix(Token.INT, this::parseIntegerLiteral);
        registerPrefix(Token.BANG, this::parsePrefixExpression);
        registerPrefix(Token.MINUS, this::parsePrefixExpression);
        registerPrefix(Token.TRUE, this::parseBool);
        registerPrefix(Token.FALSE, this::parseBool);
        registerPrefix(Token.LPAREN, this::parseGroupedExpression);
        registerPrefix(Token.IF, this::parseIfExpression);
        registerPrefix(Token.FUNCTION, this::parseFunctionLiteral);
        registerPrefix(Token.STRING, this::parseStringLiteral);
        registerPrefix(Token.LBRACKET, this::parseArrayLiteral);
        registerPrefix(Token.LBRACE, this::parseHashLiteral);
    }

    private void registerPrefix(String tokenType, PrefixParseFn fn) {
        this.prefixParseFns.put(tokenType, fn);
    }

    private void registerInfixFns() {
        registerInfix(Token.PLUS, this::parseInfixExpression);
        registerInfix(Token.MINUS, this::parseInfixExpression);
        registerInfix(Token.SLASH, this::parseInfixExpression);
        registerInfix(Token.ASTERISK, this::parseInfixExpression);
        registerInfix(Token.EQ, this::parseInfixExpression);
        registerInfix(Token.NOTEQ, this::parseInfixExpression);
        registerInfix(Token.LT, this::parseInfixExpression);
        registerInfix(Token.GT, this::parseInfixExpression);
        registerInfix(Token.LPAREN, this::parseCallExpression);
        registerInfix(Token.LBRACKET, this::parseIndexExpression);
    }

    private void registerInfix(String tokenType, InfixParseFn fn) {
        this.infixParseFns.put(tokenType, fn);
    }

    /* Helper Functions */
    private void nextToken() {
        curToken = peekToken;
        peekToken = l.nextToken();
    }
    
    private boolean curTokenIs(Token t){
        return curToken.type == t.type;
    }

    private boolean peekTokenIs(Token t){
        return peekToken.type == t.type;
    }

    private boolean expectPeek(Token t){
        if (peekTokenIs(t)){
            nextToken();
            return true;
        }
        peekError(t);
        return false;
    }

    private void peekError(Token t){
        String msg = String.format("Expected next token to be %s, got %s.", t, 
                                    peekToken.type);
        errors.add(msg);
    } 

    private ExpressionType peekPrecedence() {
        ExpressionType p = precedences.get(peekToken.type);
        return p != null ? p : ExpressionType.LOWEST;
    }

    private ExpressionType curPrecedence() {
        ExpressionType p = precedences.get(curToken.type);
        return p != null ? p : ExpressionType.LOWEST;
    }
}