package parser;

import lexer.Lexer;
import ast.Program;
import token.Token;

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

    Program parseProgram() {
        return null;
    }
}
