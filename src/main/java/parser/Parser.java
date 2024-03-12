package parser;

import ast.Identifier;
import ast.LetStatement;
import ast.Program;
import ast.Statement;
import lexer.Lexer;
import token.Token;

import java.util.ArrayList;

class Parser {
    Lexer l;
    Token curToken;
    Token peekToken;

    Parser(Lexer l) {
        this.l = l;
        nextToken();
        nextToken();
    }

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
        return false;
    }

    LetStatement parseLetStatement(){
        LetStatement stmt = new LetStatement(curToken);
        if (!expectPeek(new Token(Token.IDENT, ""))){
            return null;
        }

        stmt.name = new Identifier(curToken, curToken.literal);

        if (!expectPeek(new Token(Token.ASSIGN, ""))){
            return null;
        }

        //TODO: We're skipping the expressions
        //      until we encounter a semicolon
        while (curTokenIs(new Token(Token.SEMICOLON, ""))){
            nextToken();
        }
        
        return stmt;
    }

    Statement parseStatement(){
        switch (curToken.type) {
            case Token.LET:
                return parseLetStatement();
            default:
                return null;
        }
    }

    Program parseProgram() {
        Program program = new Program();
        program.statements = new ArrayList<Statement>();

        while (curToken.type != Token.EOF){
            Statement stmt = parseStatement();
            if (stmt != null){
                program.statements.add(stmt);
            }
            nextToken();
        }

        return program;
    }
}
