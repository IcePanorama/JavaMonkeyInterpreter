package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import ast.Bool;
import ast.Expression;
import ast.ExpressionStatement;
import ast.Identifier;
import ast.InfixExpression;
import ast.IntegerLiteral;
import ast.LetStatement;
import ast.PrefixExpression;
import ast.Program;
import ast.Statement;
import ast.ReturnStatement;
import lexer.Lexer;
import token.Token;

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

    void checkProgHasExpectedNumStatements(Program p, int expectedNoStmts) {
        if (p.statements.size() != expectedNoStmts) {
            fail(String.format(
                    "Program doesn't have enough statements. Got %d.", 
                    p.statements.size()
                ));
        }
    }

    void testIntegerLiteral(Expression intLiteral, long value) {
        assertInstanceOf(IntegerLiteral.class, intLiteral);
        
        var integer = (IntegerLiteral)intLiteral;
        assertEquals(value, integer.value);
        assertEquals(String.format("%d", value), integer.TokenLiteral());
    }

    void testIdentifier(Expression expr, String value) {
        assertInstanceOf(Identifier.class, expr);
        
        Identifier ident = (Identifier)expr;
        assertEquals(value, ident.value);
        assertEquals(value, ident.TokenLiteral());
    }

    void testLiteralExpression(Expression expr, long expected) {
        testIntegerLiteral(expr, expected);
    }

    void testLiteralExpression(Expression expr, String expected) {
        testIdentifier(expr, expected);
    }

    //FIXME: this one function should take an Expression,
    //not an ExpressionStatement
    void testLiteralExpression(ExpressionStatement expr, boolean expected) {
        testBoolExpression(expr, "" + expected, false);
    }

    void testInfixExpression(Expression expr, long left, String operator,
                             long right) {
        assertInstanceOf(InfixExpression.class, expr);

        InfixExpression opExp = (InfixExpression)expr;
        testLiteralExpression(opExp.left, left);
        assertEquals(opExp.operator, operator);
        testLiteralExpression(opExp.right, right);
    }

    void testInfixExpression(Expression expr, String left, String operator,
                             String right) {
        assertInstanceOf(InfixExpression.class, expr);

        InfixExpression opExp = (InfixExpression)expr;
        testLiteralExpression(opExp.left, left);
        assertEquals(opExp.operator, operator);
        testLiteralExpression(opExp.right, right);
    }

    void testBoolExpression(ExpressionStatement expr, String expectedTokenType,
                            boolean expectedValue) {
        assertInstanceOf(Bool.class, (expr.expression));

        Bool bool = (Bool)(expr.expression);
        System.out.println(bool.TokenLiteral() + " " + bool.value);
        assertEquals(expectedTokenType, bool.TokenLiteral());
        assertEquals(expectedValue, bool.value);
    }

//TODO: Refactor test. Write more proper tests w/ better coverage
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

        assertEquals("[" + input + "]", prog.toString());
    }

    @Test
    void exampleIndentifierExpressionTest() {
        String input = "foobar;";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 1);

        var stmt = prog.statements.get(0);
        assertInstanceOf(ExpressionStatement.class, stmt);

        var ident = ((ExpressionStatement)(stmt)).expression;
        assertInstanceOf(Identifier.class, ident);

        var val = (Identifier)(ident);
        assertEquals("foobar", val.toString());
        assertEquals("foobar", val.TokenLiteral());
    }

    @Test
    void exampleIntegerLiteralExpressionTest() {
        String input = "5;";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 1);
        
        var stmt = prog.statements.get(0);
        assertInstanceOf(ExpressionStatement.class, stmt);

        var literal = ((ExpressionStatement)(stmt)).expression;
        assertInstanceOf(IntegerLiteral.class, literal);

        var value = ((IntegerLiteral)(literal)).value;
        assertEquals(5, value);
        assertEquals("5", ((IntegerLiteral)(literal)).TokenLiteral());
    }

    @Test
    void examplePrefixExpressionTest() {
        String input = "!5; -15;";
        String[] operators = { "!", "-" };
        long[] integerValues = { 5L, 15L };

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 2);

        for (int i = 0; i < prog.statements.size(); i++) {
            var stmt = prog.statements.get(i);
            // is it worth moving this one line to a new func?
            assertInstanceOf(ExpressionStatement.class, stmt);

            var expression = ((ExpressionStatement)(stmt)).expression;
            assertInstanceOf(PrefixExpression.class, expression);

            var prefixExpr = (PrefixExpression)expression;
            assertEquals(operators[i], prefixExpr.operator);
            System.out.println(prefixExpr.right + " " + integerValues[i]);
            testIntegerLiteral(prefixExpr.right, integerValues[i]);
        }
    }

    @Test
    void exampleInfixExpressionTest() {
        String[] inputs = {
            "5 + 5;",
            "5 - 5;",
            "5 * 5;",
            "5 / 5;",
            "5 > 5;",
            "5 < 5;",
            "5 == 5;",
            "5 != 5;"
        };
        long[] leftValues = {
            5,
            5,
            5,
            5,
            5,
            5,
            5,
            5
        };
        String[] operators = {
            "+",
            "-",
            "*",
            "/",
            ">",
            "<",
            "==",
            "!=",
        };
        long[] rightValues = {
            5,
            5,
            5,
            5,
            5,
            5,
            5,
            5
        };

        for (int i = 0; i < inputs.length; i++) {
            String input = inputs[i];
            var l = new Lexer(input);
            var p = new Parser(l);
            var prog = p.parseProgram();
            checkParseErrors(p);

            checkProgHasExpectedNumStatements(prog, 1);

            Statement stmt = prog.statements.get(0);
            assertInstanceOf(ExpressionStatement.class, stmt);
            ExpressionStatement exprStmt = (ExpressionStatement)stmt;
            testInfixExpression(exprStmt.expression, leftValues[i],
                                operators[i], rightValues[i]);
        }
    }

    @Test
    void exampleTestOperatorPrecedenceParsing() {
        String[] inputs = {
            "-a * b",
            "!-a",
            "a + b + c",
            "a + b - c",
            "a * b * c",
            "a * b / c",
            "a + b * c + d / e - f",
            "3 + 4; -5 * 5",
            "5 > 4 == 3 < 4",
            "5 < 4 != 3 > 4",
            "3 + 4 * 5 == 3 * 1 + 4 * 5",
            "true",
            "false",
            "3 > 5 == false",
            "3 < 5 == true"
        };
        String[] expectedOutputs = {
            "[((-a) * b)]",
            "[(!(-a))]",
            "[((a + b) + c)]",
            "[((a + b) - c)]",
            "[((a * b) * c)]",
            "[((a * b) / c)]",
            "[(((a + (b * c)) + (d / e)) - f)]",
            "[(3 + 4), ((-5) * 5)]",
            "[((5 > 4) == (3 < 4))]",
            "[((5 < 4) != (3 > 4))]",
            "[((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))]",
            "[true]",
            "[false]",
            "[((3 > 5) == false)]",
            "[((3 < 5) == true)]"
        };

        for (int i = 0; i < inputs.length; i++) {
            var l = new Lexer(inputs[i]);
            var p = new Parser(l);
            var prog = p.parseProgram();
            checkParseErrors(p);

            assertEquals(expectedOutputs[i], prog.toString());
        }
    }

    @Test
    void boolTrueShouldParseAsTrueExpression() {
        String input = "true;";
        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);
        checkProgHasExpectedNumStatements(prog, 1);

        Statement stmt = prog.statements.get(0);
        assertInstanceOf(ExpressionStatement.class, stmt);
        ExpressionStatement expr = (ExpressionStatement)stmt;
        testBoolExpression(expr, "true", true);
    }

    @Test
    void boolFalseShouldParseAsFalseExpression() {
        String input = "false;";
        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);
        checkProgHasExpectedNumStatements(prog, 1);

        Statement stmt = prog.statements.get(0);
        assertInstanceOf(ExpressionStatement.class, stmt);
        ExpressionStatement expr = (ExpressionStatement)stmt;
        testBoolExpression(expr, "false", false);
    }
}
