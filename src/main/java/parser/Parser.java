package parser;

import lexer.Lexer;
import token.Token;
import ast.Program;

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
