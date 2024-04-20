package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import ast.Bool;
import ast.CallExpression;
import ast.Expression;
import ast.ExpressionStatement;
import ast.FunctionLiteral;
import ast.Identifier;
import ast.IfExpression;
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

//TODO: this function needs a better name
    FunctionLiteral programIsFunctionLiteral(Program p) {
        Statement stmt = p.statements.get(0);
        assertInstanceOf(ExpressionStatement.class, stmt);

        Expression expr = ((ExpressionStatement)stmt).expression;
        assertInstanceOf(FunctionLiteral.class, expr);

        return (FunctionLiteral)expr;
    }

    CallExpression programIsCallExpression(Program p) {
        Statement stmt = p.statements.get(0);
        assertInstanceOf(ExpressionStatement.class, stmt);

        Expression expr = ((ExpressionStatement)stmt).expression;
        assertInstanceOf(CallExpression.class, expr);

        return (CallExpression)expr;
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

    void testBoolLiteral(Expression expr, boolean value) {
        assertInstanceOf(Bool.class, expr);

        Bool bool = (Bool)(expr);
        assertEquals("" + value, bool.TokenLiteral());
    }

    void testFunctionLiteral(Identifier expr, Identifier expected) {
        assertEquals(expected.TokenLiteral(),expr.TokenLiteral());
        assertEquals(expected.value, expr.value);
    }

    void testLiteralExpression(Expression expr, long expected) {
        testIntegerLiteral(expr, expected);
    }

    void testLiteralExpression(Expression expr, String expected) {
        testIdentifier(expr, expected);
    }

    void testLiteralExpression(Identifier expr, Identifier expected) {
        testFunctionLiteral(expr, expected);
    }
    
    //FIXME: this one function should take an Expression,
    //not an ExpressionStatement
    void testLiteralExpression(Expression expr, boolean expected) {
        testBoolLiteral(expr, expected);
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

    void testInfixExpression(Expression expr, boolean left, String operator,
                             boolean right) {
        assertInstanceOf(InfixExpression.class, expr);

        InfixExpression opExp = (InfixExpression)expr;
        testLiteralExpression(opExp.left, left);
        assertEquals(opExp.operator, operator);
        testLiteralExpression(opExp.right, right);
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
            "!="
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
            "3 < 5 == true",
            "1 + (2 + 3) + 4",
            "(5 + 5) * 2",
            "2 / (5 + 5)",
            "-(5 + 5)",
            "!(true == true)"
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
            "[((3 < 5) == true)]",
            "[((1 + (2 + 3)) + 4)]",
            "[((5 + 5) * 2)]",
            "[(2 / (5 + 5))]",
            "[(-(5 + 5))]",
            "[(!(true == true))]"
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
        var literal = ((ExpressionStatement)stmt).expression;
        assertInstanceOf(Bool.class, literal);
        boolean value = ((Bool)literal).value;
        assertEquals(value, true);
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
        var literal = ((ExpressionStatement)stmt).expression;
        assertInstanceOf(Bool.class, literal);
        boolean value = ((Bool)literal).value;
        assertEquals(value, false);
    }

    @Test
    void exampleInfixBoolExpressionTest() {
        String[] inputs = {
            "true == true",
            "true != false",
            "false == false"
        };
        boolean[] leftValues = {
            true,
            true,
            false
        };
        String[] operators = {
            "==",
            "!=",
            "=="
        };
        boolean[] rightValues = {
            true,
            false,
            false
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
    void examplePrefixBoolExpressionTest() {
        String[] inputs = {
            "!true",
            "!false"
        };
        String[] operators = {
            "!",
            "!"
        };
        boolean[] values = {
            true,
            false
        };

        for (int i = 0; i < inputs.length; i++) {
            var l = new Lexer(inputs[i]);
            var p = new Parser(l);
            var prog = p.parseProgram();
            checkParseErrors(p);

            checkProgHasExpectedNumStatements(prog, 1);

            var stmt = prog.statements.get(0);
            assertInstanceOf(ExpressionStatement.class, stmt);

            var expression = ((ExpressionStatement) (stmt)).expression;
            assertInstanceOf(PrefixExpression.class, expression);

            var prefixExpr = (PrefixExpression)expression;
            assertEquals(operators[i], prefixExpr.operator);
            testBoolLiteral(prefixExpr.right, values[i]);
        }
    }

    @Test
    void exampleIfExpressionTest() {
        String input = "if (x < y) { x }";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();

        checkProgHasExpectedNumStatements(prog, 1);

        Statement stmt = prog.statements.get(0);
        assertInstanceOf(ExpressionStatement.class, stmt);

        Expression expr = ((ExpressionStatement)stmt).expression;
        assertInstanceOf(IfExpression.class, expr);

        IfExpression ifExpression = (IfExpression)expr;
        testInfixExpression(ifExpression.condition, "x", "<", "y");
        assertEquals(1, ifExpression.consequence.statements.size());

        Statement consequenceStmt = ifExpression.consequence.statements.get(0);
        assertInstanceOf(ExpressionStatement.class,
            consequenceStmt);

        Expression consequenceExpr = ((ExpressionStatement)consequenceStmt).expression;
        testIdentifier(consequenceExpr, "x");
        assertNull(ifExpression.alternative);
    }
    
    @Test
    void exampleIfElseExpressionTest() {
        String input = "if (x < y) { x } else { y }";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();

        checkProgHasExpectedNumStatements(prog, 1);

        Statement stmt = prog.statements.get(0);
        assertInstanceOf(ExpressionStatement.class, stmt);

        Expression expr = ((ExpressionStatement)stmt).expression;
        assertInstanceOf(IfExpression.class, expr);

        IfExpression ifExpression = (IfExpression)expr;
        testInfixExpression(ifExpression.condition, "x", "<", "y");
        assertEquals(1, ifExpression.consequence.statements.size());

        Statement consequenceStmt = ifExpression.consequence.statements.get(0);
        assertInstanceOf(ExpressionStatement.class, consequenceStmt);

        Expression consequenceExpr = ((ExpressionStatement)consequenceStmt).expression;
        testIdentifier(consequenceExpr, "x");

        assertNotNull(ifExpression.alternative);
        assertEquals(1, ifExpression.alternative.statements.size());

        Statement alternativeStmt = ifExpression.alternative.statements.get(0);
        assertInstanceOf(ExpressionStatement.class, alternativeStmt);

        Expression alternativeExpr = ((ExpressionStatement)alternativeStmt).expression;
        testIdentifier(alternativeExpr, "y");
    }

    @Test
    void exampleFunctionLiteralExpressionTest() {
        String input = "fn(x, y) { x + y; }";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);
        checkProgHasExpectedNumStatements(prog, 1);

        Statement stmt = prog.statements.get(0);
        assertInstanceOf(ExpressionStatement.class, stmt);

        Expression expr = ((ExpressionStatement)stmt).expression;
        assertInstanceOf(FunctionLiteral.class, expr);

        FunctionLiteral func = (FunctionLiteral)expr;
        assertEquals(2, func.parameters.size());

        testLiteralExpression(func.parameters.get(0), "x");
        testLiteralExpression(func.parameters.get(1), "y");

        assertEquals(1, func.body.statements.size());

        Statement bodyStmt = func.body.statements.get(0);
        assertInstanceOf(ExpressionStatement.class, bodyStmt);

        Expression bodyExpr = ((ExpressionStatement)bodyStmt).expression;
        testInfixExpression(bodyExpr, "x", "+", "y");
    }

//TODO: this next 3 could probably be refactored even further
    @Test
    void functionLiteralWithAnEmptyParameterListShouldHaveNoParameters() {
        String input = "fn () {};";
        Identifier[] expectedParameters = new Identifier[0];

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        FunctionLiteral func = programIsFunctionLiteral(prog);
        assertEquals(expectedParameters.length, func.parameters.size());

        for (int i = 0; i < expectedParameters.length; i++) {
            testLiteralExpression(func.parameters.get(i), expectedParameters[i]);
        }
    }

    @Test
    void functionLiteralWithOneParameterShouldHaveOneParameter() {
        String input = "fn(x) {};";
        Identifier[] expectedParameters = {
            new Identifier(new Token(Token.IDENT, "x"), "x")
        };

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        FunctionLiteral func = programIsFunctionLiteral(prog);
        assertEquals(expectedParameters.length, func.parameters.size());

        for (int i = 0; i < expectedParameters.length; i++) {
            testLiteralExpression(func.parameters.get(i), expectedParameters[i]);
        }
    }

    @Test
    void functionLiteralWithMultipleParametersShouldHaveMultipleParameters() {
        String input = "fn (x, y, z) {};";
        Identifier[] expectedParams = {
            new Identifier(new Token(Token.IDENT, "x"), "x"),
            new Identifier(new Token(Token.IDENT, "y"), "y"),
            new Identifier(new Token(Token.IDENT, "z"), "z")
        };

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        FunctionLiteral func = programIsFunctionLiteral(prog);
        assertEquals(expectedParams.length, func.parameters.size());

        for (int i = 0; i < expectedParams.length; i++) {
            testLiteralExpression(func.parameters.get(i), expectedParams[i]);
        }
    }

    @Test
    void exampleCallExpressionTest() {
        String input = "add(1, 2 * 3, 4 + 5);";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 1);

        CallExpression callExpr = programIsCallExpression(prog);
        testIdentifier(callExpr.function, "add");
        assertEquals(3, callExpr.arguements.size());
        testLiteralExpression(callExpr.arguements.get(0), 1);
        testInfixExpression(callExpr.arguements.get(1), 2, "*", 3);
        testInfixExpression(callExpr.arguements.get(2), 4, "+", 5);
    }

//TODO: create tests for CallExpression parameters
//      sim. to the ones made for functions
    @Test
    void callExpressionWithNoParametersShouldHaveNoParameters() {
        String input = "foobar();";

        // Should the next 6 lines be their own func?
        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 1);

        CallExpression callExpr = programIsCallExpression(prog);
        assertEquals(0, callExpr.arguements.size());
    }

    @Test
    void callExpressionWithOneArguementShouldHaveOneArguement() {
        String input = "foobar(x);";
        //this probably shouldn't be a Identifier
        Identifier expectedArguement = new Identifier(
            new Token(Token.IDENT, "x"),
            "x"
        );

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 1);

        CallExpression callExpr = programIsCallExpression(prog);
        assertEquals(1, callExpr.arguements.size());
        //idk if this'll work
        testLiteralExpression(
            (Identifier)callExpr.arguements.get(0),
            expectedArguement
        );
    }

    @Test
    void callExpressionWithMultipleArguementsShouldHaveMultipleArguements() {
        String input = "foobar(x, y, z);";
        Identifier[] expectedArguements = {
            new Identifier(new Token(Token.IDENT, "x"), "x"),
            new Identifier(new Token(Token.IDENT, "y"), "y"),
            new Identifier(new Token(Token.IDENT, "z"), "z")
        };

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 1);

        CallExpression callExpr = programIsCallExpression(prog);
        assertEquals(3, callExpr.arguements.size());

        for (int i = 0; i < callExpr.arguements.size(); i++) {
            testLiteralExpression(
                    (Identifier) callExpr.arguements.get(i),
                    expectedArguements[i]
            );
        }
    }
}