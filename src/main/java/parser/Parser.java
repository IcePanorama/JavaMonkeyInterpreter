package parser;

import ast.Identifier;
import ast.LetStatement;
import ast.Program;
import ast.ReturnStatement;
import ast.Statement;
import lexer.Lexer;
import token.Token;

import java.util.ArrayList;

class Parser {
    Lexer l;
    Token curToken;
    Token peekToken;
    ArrayList<String> errors;

    Parser(Lexer l) {
        this.l = l;
        errors = new ArrayList<String>();
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
                return null;
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
}
