package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import ast.ExpressionStatement;
import ast.Identifier;
import ast.LetStatement;
import ast.Statement;
import ast.ReturnStatement;
import lexer.Lexer;

public class ParserTest {
    void testLetStatement(Statement s, String name){
        assertEquals(s.TokenLiteral(), "let");

        assertInstanceOf(LetStatement.class, s);

        LetStatement letStmt = (LetStatement)s;
        assertEquals(letStmt.name.value, name);
        assertEquals(letStmt.name.TokenLiteral(), name);
    }

    void checkParseErrors(Parser p){
        if (p.errors.size() == 0){
            return;
        }

        System.err.printf("Parser has %d errors.\n", p.errors.size());
        for (var err : p.errors){
            System.err.printf("Parser error: %s\n", err);
        }

        fail();
    }

    @Test
    void testLetStatements() {
        String input = """
            let x = 5;
            let y = 10;
            let foobar = 838383;
                """;
        String expectedOutput[] = {
            "x",
            "y",
            "foobar"
        };

        var l = new Lexer(input);
        var p = new Parser(l);
        var program = p.parseProgram();
        checkParseErrors(p);
        if (program == null){
            fail("parseProgram() returned null.");
        }
        if (program.statements.size() != 3){
            fail(String.format(
                "program.Statements does not contain 3 statements; got %d.",
                program.statements.size())
            );
        }

        for (int i = 0; i < expectedOutput.length; i++){
            Statement stmt = program.statements.get(i);
            testLetStatement(stmt, expectedOutput[i]);
        }
    }

    @Test
    void testReturnStatements(){
        String input = """
            return 5;
            return 19;
            return 993322;
        """;

        var l = new Lexer(input);
        var p = new Parser(l);
        var program = p.parseProgram();
        checkParseErrors(p);

        if (program.statements.size() != 3){
            fail(String.format(
                "program.Statements does not contain 3 statements; got %d;",
                program.statements.size())
            );
        }

        for (var stmt : program.statements){
            assertInstanceOf(ReturnStatement.class, stmt);
            ReturnStatement returnStmt = (ReturnStatement)stmt;
            assertEquals(returnStmt.TokenLiteral(), "return");
        }
    }

    @Test
    void toStringReturnsInputAsString() {
        String input = "let myVar = anotherVar;";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        assertEquals(input, prog.toString());
    }

    @Test
    void exampleIndentifierExpressionTest() {
        String input = "foobar;";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        if (prog.statements.size() != 1) {
            fail(String.format(
                    "Program doesn't have enough statements. Got %d.", 
                    prog.statements.size()
                ));
        }

        var stmt = prog.statements.get(0);
        assertInstanceOf(ExpressionStatement.class, stmt);

        var ident = ((ExpressionStatement)(stmt)).expression;
        assertInstanceOf(Identifier.class, ident);

        var val = (Identifier)(ident);
        assertEquals("foobar", val.toString());
        assertEquals("foobar", val.TokenLiteral());
    }
}
