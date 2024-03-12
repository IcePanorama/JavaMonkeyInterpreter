package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import lexer.Lexer;
import ast.Statement;
import ast.LetStatement;

public class ParserTest {
    void testLetStatement(Statement s, String name){
        // Should this func even return anything if 
        // we're gonna use fail anyways?
        assertEquals(s.TokenLiteral(), "let");

        assertInstanceOf(LetStatement.class, s);

        LetStatement letStmt = (LetStatement)s;
        assertEquals(letStmt.name.value, name);
        assertEquals(letStmt.name.TokenLiteral(), name);
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
        if (program == null){
            fail("parseProgram() returned null.");
        }
        if (program.statements.size() != 3){
            fail("program.Statements does not contain 3 statements.");
        }

        for (int i = 0; i < expectedOutput.length; i++){
            Statement stmt = program.statements.get(i);
            testLetStatement(stmt, expectedOutput[i]);
        }
    }
}
