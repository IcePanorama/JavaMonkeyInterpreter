package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import ast.ArrayLiteral;
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

//TODO: refactor me!
public class ParserTest {
    void testLetStatement(Statement s, String name){
        assertEquals(s.getTokenLiteral(), "let");

        assertInstanceOf(LetStatement.class, s);

        LetStatement letStmt = (LetStatement)s;
        assertEquals(letStmt.name.value, name);
        assertEquals(letStmt.name.getTokenLiteral(), name);
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
        if (p.statements.length != expectedNoStmts) {
            fail(String.format(
                    "Program doesn't have enough statements. Got %d.", 
                    p.statements.length
                ));
        }
    }

    PrefixExpression testPrefixExpressionHelperFunction(String input) {
        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 1);

        var stmt = prog.statements[0];
        assertInstanceOf(ExpressionStatement.class, stmt);

        var expression = ((ExpressionStatement) (stmt)).expression;
        assertInstanceOf(PrefixExpression.class, expression);

        return (PrefixExpression)expression;
    }

    void testPrefixExpressions(String input, String expectedOperator, boolean expectedValue) {
        var prefixExpr = testPrefixExpressionHelperFunction(input);
        assertEquals(expectedOperator, prefixExpr.operator);
        testBoolLiteral(prefixExpr.right, expectedValue);
    }

    void testPrefixExpressions(String input, String expectedOperator, long expectedValue) {
        var prefixExpr = testPrefixExpressionHelperFunction(input);
        assertEquals(expectedOperator, prefixExpr.operator);
        testIntegerLiteral(prefixExpr.right, expectedValue);
    }

    FunctionLiteral programIsFunctionLiteral(Program p) {
        Statement stmt = p.statements[0];
        assertInstanceOf(ExpressionStatement.class, stmt);

        Expression expr = ((ExpressionStatement)stmt).expression;
        assertInstanceOf(FunctionLiteral.class, expr);

        return (FunctionLiteral)expr;
    }

    CallExpression programIsCallExpression(Program p) {
        Statement stmt = p.statements[0];
        assertInstanceOf(ExpressionStatement.class, stmt);

        Expression expr = ((ExpressionStatement)stmt).expression;
        assertInstanceOf(CallExpression.class, expr);

        return (CallExpression)expr;
    }

    void testIntegerLiteral(Expression intLiteral, long value) {
        assertInstanceOf(IntegerLiteral.class, intLiteral);
        
        var integer = (IntegerLiteral)intLiteral;
        assertEquals(value, integer.value);
        assertEquals(String.format("%d", value), integer.getTokenLiteral());
    }

    void testIdentifier(Expression expr, String value) {
        assertInstanceOf(Identifier.class, expr);
        
        Identifier ident = (Identifier)expr;
        assertEquals(value, ident.value);
        assertEquals(value, ident.getTokenLiteral());
    }

    void testBoolLiteral(Expression expr, boolean value) {
        assertInstanceOf(Bool.class, expr);

        Bool bool = (Bool)(expr);
        assertEquals("" + value, bool.getTokenLiteral());
    }

    void testFunctionLiteral(Identifier expr, Identifier expected) {
        assertEquals(expected.getTokenLiteral(),expr.getTokenLiteral());
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
    
    void testLiteralExpression(Expression expr, boolean expected) {
        testBoolLiteral(expr, expected);
    }

    InfixExpression testInfixExpressionHelperFunction(String input) {
        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 1);

        Statement stmt = prog.statements[0];
        assertInstanceOf(ExpressionStatement.class, stmt);
        
        Expression expr = ((ExpressionStatement)stmt).expression;
        assertInstanceOf(InfixExpression.class, expr);
        
        return (InfixExpression)expr;
    }

    void testInfixExpression(String input, boolean expectedLeft,
                             String expectedOperator, boolean expectedRight) {
        InfixExpression expr = testInfixExpressionHelperFunction(input);

        testLiteralExpression(expr.left, expectedLeft);
        assertEquals(expr.operator, expectedOperator);
        testLiteralExpression(expr.right, expectedRight);
    }

    void testInfixExpression(String input, long expectedLeft,
                             String expectedOperator, long expectedRight) {
        InfixExpression expr = testInfixExpressionHelperFunction(input);
        testLiteralExpression(expr.left, expectedLeft);
        assertEquals(expr.operator, expectedOperator);
        testLiteralExpression(expr.right, expectedRight);
    }

    void testInfixExpression(Expression inputExpr, String expectedLeft,
                             String expectedOperator, String expectedRight) {
        assertInstanceOf(InfixExpression.class, inputExpr);
        InfixExpression expr = (InfixExpression)inputExpr;
        testLiteralExpression(expr.left, expectedLeft);
        assertEquals(expr.operator, expectedOperator);
        testLiteralExpression(expr.right, expectedRight);
    }

    void testInfixExpression(Expression inputExpr, long expectedLeft,
                             String expectedOperator, long expectedRight) {
        assertInstanceOf(InfixExpression.class, inputExpr);
        InfixExpression expr = (InfixExpression)inputExpr;
        testLiteralExpression(expr.left, expectedLeft);
        assertEquals(expr.operator, expectedOperator);
        testLiteralExpression(expr.right, expectedRight);
    }

    Program parserTestHelperFunction(String input) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program prog = p.parseProgram();
        checkParseErrors(p);

        return prog;
    }

    void testOperatorPrecedence(String input, String expectedOutput) {
        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        assertEquals(expectedOutput, prog.statements[0].toString());
    }

    @Test
    void letXEqual5ParsesCorrectlyAsALetStatement() {
        String input = "let x = 5;";
        String expectedIdentifier = "x";
        long expectedOutput = 5;

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 1);

        Statement stmt = prog.statements[0];
        testLetStatement(stmt, expectedIdentifier);

        Expression val = ((LetStatement)stmt).value;
        testLiteralExpression(val, expectedOutput);
    }

    @Test
    void letYEqualTrueParsesCorrectlyAsALetStatement() {
        String input = "let y = true;";
        String expectedIdentifier = "y";
        boolean expectedOutput = true;

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 1);

        Statement stmt = prog.statements[0];
        testLetStatement(stmt, expectedIdentifier);

        Expression val = ((LetStatement)stmt).value;
        testLiteralExpression(val, expectedOutput);
    }

    @Test
    void letFoobarEqualYParsesCorrectlyAsALetStatement() {
        String input = "let foobar = y;";
        String expectedIdentifier = "foobar";
        String expectedOutput = "y";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 1);

        Statement stmt = prog.statements[0];
        testLetStatement(stmt, expectedIdentifier);

        Expression val = ((LetStatement)stmt).value;
        testLiteralExpression(val, expectedOutput);
    }

    @Test
    void return5ParsesCorrectlyAsAReturnStatement() {
        String input = "return 5;";
        
        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 1);

        Statement stmt = prog.statements[0];
        assertInstanceOf(ReturnStatement.class, stmt);

        ReturnStatement returnStmt = (ReturnStatement)stmt;
        assertEquals(returnStmt.getTokenLiteral(), "return");

        Expression returnValue = returnStmt.returnValue;
        testLiteralExpression(returnValue, 5);
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
    void foobarShouldBeParsedAsAnIdentifier() {
        String input = "foobar;";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 1);

        var stmt = prog.statements[0];
        assertInstanceOf(ExpressionStatement.class, stmt);

        var ident = ((ExpressionStatement)(stmt)).expression;
        assertInstanceOf(Identifier.class, ident);

        var val = (Identifier)(ident);
        assertEquals("foobar", val.toString());
        assertEquals("foobar", val.getTokenLiteral());
    }

    @Test
    void fiveShouldBeParsedAsAnIntegerLiteral() {
        String input = "5;";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 1);
        
        var stmt = prog.statements[0];
        assertInstanceOf(ExpressionStatement.class, stmt);

        var literal = ((ExpressionStatement)(stmt)).expression;
        assertInstanceOf(IntegerLiteral.class, literal);

        var value = ((IntegerLiteral)(literal)).value;
        assertEquals(5, value);
        assertEquals("5", ((IntegerLiteral)(literal)).getTokenLiteral());
    }

    @Test
    void bangFiveShouldBeParsedCorrectlyAsAPrefixExpression() {
        String input = "!5;";
        String expectedOperator = "!";
        long expectedValue = 5;

        testPrefixExpressions(input, expectedOperator, expectedValue);
    }

    @Test
    void minusFifteenShouldBeParsedCorrectlyAsAPrefixExpression() {
        String input = "-15;";
        String expectedOperator = "-";
        long expectedValue = 15;

        testPrefixExpressions(input, expectedOperator, expectedValue);
    }

    @Test
    void fivePlusFiveShouldBeParsedCorrectlyAsAnInfixExpression() {
        String input = "5 + 5;";
        long expectedLeftValue = 5;
        String expectedOperator = "+";
        long expectedRightValue = 5;

        testInfixExpression(input, expectedLeftValue, expectedOperator,
                            expectedRightValue);
    }

    @Test
    void fiveMinusFiveShouldBeParsedCorrectlyAsAnInfixExpression() {
        String input = "5 - 5;";
        long expectedLeftValue = 5;
        String expectedOperator = "-";
        long expectedRightValue = 5;

        testInfixExpression(input, expectedLeftValue, expectedOperator,
                            expectedRightValue);
    }

    @Test
    void fiveTimesFiveShouldBeParsedCorrectlyAsAnInfixExpression() {
        String input = "5 * 5;";
        long expectedLeftValue = 5;
        String expectedOperator = "*";
        long expectedRightValue = 5;

        testInfixExpression(input, expectedLeftValue, expectedOperator,
                            expectedRightValue);
    }

    @Test
    void fiveDivFiveShouldBeParsedCorrectlyAsAnInfixExpression() {
        String input = "5 / 5;";
        long expectedLeftValue = 5;
        String expectedOperator = "/";
        long expectedRightValue = 5;

        testInfixExpression(input, expectedLeftValue, expectedOperator,
                            expectedRightValue);
    }

    @Test
    void fiveGTFiveShouldBeParsedCorrectlyAsAnInfixExpression() {
        String input = "5 > 5;";
        long expectedLeftValue = 5;
        String expectedOperator = ">";
        long expectedRightValue = 5;

        testInfixExpression(input, expectedLeftValue, expectedOperator,
                            expectedRightValue);
    }

    @Test
    void fiveLTFiveShouldBeParsedCorrectlyAsAnInfixExpression() {
        String input = "5 < 5;";
        long expectedLeftValue = 5;
        String expectedOperator = "<";
        long expectedRightValue = 5;

        testInfixExpression(input, expectedLeftValue, expectedOperator,
                            expectedRightValue);
    }

    @Test
    void fiveEqualsEqualsFiveShouldBeParsedCorrectlyAsAnInfixExpression() {
        String input = "5 == 5;";
        long expectedLeftValue = 5;
        String expectedOperator = "==";
        long expectedRightValue = 5;

        testInfixExpression(input, expectedLeftValue, expectedOperator,
                            expectedRightValue);
    }

    @Test
    void fiveBangEqualsFiveShouldBeParsedCorrectlyAsAnInfixExpression() {
        String input = "5 != 5;";
        long expectedLeftValue = 5;
        String expectedOperator = "!=";
        long expectedRightValue = 5;

        testInfixExpression(input, expectedLeftValue, expectedOperator,
                            expectedRightValue);
    }

    @Test
    void minusAPlusBShouldBeParsedWithCorrectPrecedence() {
        String input = "-a * b";
        String expectedOutput = "((-a) * b)";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void bangMinusAShouldBeParsedWithCorrectPrecedence() {
        String input = "!-a";
        String expectedOutput = "(!(-a))";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void aPlusBPlusCShouldBeParsedWithCorrectPrecedence() {
        String input = "a + b + c";
        String expectedOutput = "((a + b) + c)";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void aPlusBMinusCShouldBeParsedWithCorrectPrecedence() {
        String input = "a + b - c";
        String expectedOutput = "((a + b) - c)";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void aTimesBTimesCShouldBeParsedWithCorrectPrecedence() {
        String input = "a * b * c";
        String expectedOutput = "((a * b) * c)";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void aTimesBDivCShouldBeParsedWithCorrectPrecedence() {
        String input = "a * b / c";
        String expectedOutput = "((a * b) / c)";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void aPlusBTimesCPlusDDivEMinusFShouldBeParsedWithCorrectPrecedence() {
        String input = "a + b * c + d / e - f";
        String expectedOutput = "(((a + (b * c)) + (d / e)) - f)";

        testOperatorPrecedence(input, expectedOutput);
    }

    /* Why is my toString method not being called???? */
    @Test
    void parserShouldBeAbleToProperlyDeterminePrecedenceForTwoSeparateStatements() {
        String input = "3 + 4; -5 * 5";
        String expectedOutput = "[(3 + 4), ((-5) * 5)]";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        assertInstanceOf(Program.class, prog);
        assertInstanceOf(Statement.class, prog.statements[0]);

        assertEquals(expectedOutput, prog.toString());
    }

    @Test
    void fiveGTFourEqualsEqualsThreeLTFourShouldBeParsedWithCorrectPrecedence() {
        String input = "5 > 4 == 3 < 4";
        String expectedOutput = "((5 > 4) == (3 < 4))";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void fiveLTFourBangEqualsThreeGTFourShouldBeParsedWithCorrectPrecedence() {
        String input = "5 < 4 != 3 > 4";
        String expectedOutput = "((5 < 4) != (3 > 4))";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void threePlusFourTimesFiveEqualsEqualsThreeTimesOnePlusFourTimesFiveShouldBeParsedWithCorrectPrecedence() {
        String input = "3 + 4 * 5 == 3 * 1 + 4 * 5";
        String expectedOutput = "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void trueShouldBeParsedWithCorrectPrecedence() {
        String input = "true";
        String expectedOutput = "true";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void falseShouldBeParsedWithCorrectPrecedence() {
        String input = "false";
        String expectedOutput = "false";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void threeGTFiveEqualsEqualsFalseShouldBeParsedWithCorrectPrecedence() {
        String input = "3 > 5 == false";
        String expectedOutput = "((3 > 5) == false)";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void threeLTFiveEqualsEqualsTrueShouldBeParsedWithCorrectPrecedence() {
        String input = "3 < 5 == true";
        String expectedOutput = "((3 < 5) == true)";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void infixAdditionExpressionWithParenthesisShouldBeParsedWithCorrectPrecedence() {
        String input = "1 + (2 + 3) + 4";
        String expectedOutput = "((1 + (2 + 3)) + 4)";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void infixMultiplicationExpressionWithParenthesisShouldBeParsedWithCorrectPrecedence() {
        String input = "(5 + 5) * 2";
        String expectedOutput = "((5 + 5) * 2)";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void infixDivisionExpressionWithParenthesisShouldBeParsedWithCorrectPrecedence() {
        String input = "2 / (5 + 5)";
        String expectedOutput = "(2 / (5 + 5))";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void prefixNegationExpressionWithParenthesisShouldBeParsedWithCorrectPrecedence() {
        String input = "-(5 + 5)";
        String expectedOutput = "(-(5 + 5))";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void prefixBangExpressionWithParenthesisShouldBeParsedWithCorrectPrecedence() {
        String input = "!(true == true)";
        String expectedOutput = "(!(true == true))";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void callExpressionsWithinExpressionsShouldBeParsedWithCorrectPrecedence() {
        String input = "a + add(b * c) + d";
        String expectedOutput = "((a + add((b * c))) + d)";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void callExpressionArguementsShouldBeParsedWithCorrectPrecedence() {
        String input = "add(a, b, 1, 2 * 3, 4 + 5, add(6, 7 * 8))";
        String expectedOutput = "add(a, b, 1, (2 * 3), (4 + 5), add(6, (7 * 8)))";

        testOperatorPrecedence(input, expectedOutput);
    }

    @Test
    void boolTrueShouldParseAsTrueExpression() {
        String input = "true;";
        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);
        checkProgHasExpectedNumStatements(prog, 1);

        Statement stmt = prog.statements[0];
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

        Statement stmt = prog.statements[0];
        assertInstanceOf(ExpressionStatement.class, stmt);
        var literal = ((ExpressionStatement)stmt).expression;
        assertInstanceOf(Bool.class, literal);
        boolean value = ((Bool)literal).value;
        assertEquals(value, false);
    }

    @Test
    void trueEqualsEqualsTrueShouldBeParsedCorrectlyAsAnInfixExpression() {
        String input = "true == true";
        boolean expectedLeft = true;
        String expectedOperator = "==";
        boolean expectedRight = true;

        testInfixExpression(input, expectedLeft, expectedOperator,
                            expectedRight);
    }

    @Test
    void trueBangEqualsFalseShouldBeParsedCorrectlyAsAnInfixExpression() {
        String input = "true != false";
        boolean expectedLeft = true;
        String expectedOperator = "!=";
        boolean expectedRight = false;

        testInfixExpression(input, expectedLeft, expectedOperator,
                            expectedRight);
    }

    @Test
    void falseEqualsEqualsFalseShouldBeParsedCorrectlyAsAnInfixExpression() {
        String input = "false == false";
        boolean expectedLeft = false;
        String expectedOperator = "==";
        boolean expectedRight = false;

        testInfixExpression(input, expectedLeft, expectedOperator,
                            expectedRight);
    }

    @Test
    void bangFalseShouldBeParsedAsAPrefixBoolExpression() {
        String input = "!false";
        String expectedOperator = "!";
        boolean expectedValue = false;

        testPrefixExpressions(input, expectedOperator, expectedValue);
    }

    @Test
    void bangTrueShouldBeParsedAsAPrefixBoolExpression() {
        String input = "!true";
        String expectedOperator = "!";
        boolean expectedValue = true;

        testPrefixExpressions(input, expectedOperator, expectedValue);
    }

    @Test
    void ifXLessThanYThenXShouldBeParsedCorrectlyAsAnIfStatement() {
        String input = "if (x < y) { x }";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();

        checkProgHasExpectedNumStatements(prog, 1);

        Statement stmt = prog.statements[0];
        assertInstanceOf(ExpressionStatement.class, stmt);

        Expression expr = ((ExpressionStatement)stmt).expression;
        assertInstanceOf(IfExpression.class, expr);

        IfExpression ifExpression = (IfExpression)expr;
        testInfixExpression(ifExpression.condition, "x", "<", "y");
        assertEquals(1, ifExpression.consequence.statements.length);

        Statement consequenceStmt = ifExpression.consequence.statements[0];
        assertInstanceOf(ExpressionStatement.class,
            consequenceStmt);

        Expression consequenceExpr = ((ExpressionStatement)consequenceStmt).expression;
        testIdentifier(consequenceExpr, "x");
        assertNull(ifExpression.alternative);
    }
    
    @Test
    void ifXLessThanYThenXElseYShouldBeParsedCorrectlyAsAnIfElseStatement() {
        String input = "if (x < y) { x } else { y }";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();

        checkProgHasExpectedNumStatements(prog, 1);

        Statement stmt = prog.statements[0];
        assertInstanceOf(ExpressionStatement.class, stmt);

        Expression expr = ((ExpressionStatement)stmt).expression;
        assertInstanceOf(IfExpression.class, expr);

        IfExpression ifExpression = (IfExpression)expr;
        testInfixExpression(ifExpression.condition, "x", "<", "y");
        assertEquals(1, ifExpression.consequence.statements.length);

        Statement consequenceStmt = ifExpression.consequence.statements[0];
        assertInstanceOf(ExpressionStatement.class, consequenceStmt);

        Expression consequenceExpr = ((ExpressionStatement)consequenceStmt).expression;
        testIdentifier(consequenceExpr, "x");

        assertNotNull(ifExpression.alternative);
        assertEquals(1, ifExpression.alternative.statements.length);

        Statement alternativeStmt = ifExpression.alternative.statements[0];
        assertInstanceOf(ExpressionStatement.class, alternativeStmt);

        Expression alternativeExpr = ((ExpressionStatement)alternativeStmt).expression;
        testIdentifier(alternativeExpr, "y");
    }

    @Test
    void functionLiteralWithTwoArgumentsShouldBeParsedCorrectly() {
        String input = "fn(x, y) { x + y; }";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);
        checkProgHasExpectedNumStatements(prog, 1);

        Statement stmt = prog.statements[0];
        assertInstanceOf(ExpressionStatement.class, stmt);

        Expression expr = ((ExpressionStatement)stmt).expression;
        assertInstanceOf(FunctionLiteral.class, expr);

        FunctionLiteral func = (FunctionLiteral)expr;
        assertEquals(2, func.parameters.size());

        testLiteralExpression(func.parameters.get(0), "x");
        testLiteralExpression(func.parameters.get(1), "y");

        assertEquals(1, func.body.statements.length);

        Statement bodyStmt = func.body.statements[0];
        assertInstanceOf(ExpressionStatement.class, bodyStmt);

        Expression bodyExpr = ((ExpressionStatement)bodyStmt).expression;
        testInfixExpression(bodyExpr, "x", "+", "y");
    }

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
    void callingAddFuncWithArgsOneTwoTimesThreeAndFourPlusFiveShouldBeParsedCorrectlyAsACallExpression() {
        String input = "add(1, 2 * 3, 4 + 5);";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 1);

        CallExpression callExpr = programIsCallExpression(prog);
        testIdentifier(callExpr.function, "add");
        assertEquals(3, callExpr.arguments.length);
        testLiteralExpression(callExpr.arguments[0], 1);
        testInfixExpression(callExpr.arguments[1], 2L, "*", 3L);
        testInfixExpression(callExpr.arguments[2], 4L, "+", 5L);
    }

    @Test
    void callExpressionWithNoParametersShouldHaveNoParameters() {
        String input = "foobar();";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        checkProgHasExpectedNumStatements(prog, 1);

        CallExpression callExpr = programIsCallExpression(prog);
        assertEquals(0, callExpr.arguments.length);
    }

    @Test
    void callExpressionWithOneArguementShouldHaveOneArguement() {
        String input = "foobar(x);";
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
        assertEquals(1, callExpr.arguments.length);
        testLiteralExpression(
            (Identifier)callExpr.arguments[0],
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
        assertEquals(3, callExpr.arguments.length);

        for (int i = 0; i < callExpr.arguments.length; i++) {
            testLiteralExpression(
                    (Identifier) callExpr.arguments[i],
                    expectedArguements[i]
            );
        }
    }

    @Test
    void helloWorldInQuotesShouldParseAsAStringWithAValueOfHelloWorld() {
        String input = "\"hello world\"";
        String expectedOutput = "hello world";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        Statement stmt = prog.statements[0];
        assertInstanceOf(ExpressionStatement.class, stmt);

        Expression epxr = ((ExpressionStatement)stmt).expression;
        assertInstanceOf(StringLiteral.class, epxr);

        StringLiteral literal = (StringLiteral)epxr;
        assertEquals(expectedOutput, literal.value);
    }

    @Test
    void arrayLiteralsShouldBeParsedCorrectly() {
        String input = "[1, 2 * 2, 3 + 3]";

        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();
        checkParseErrors(p);

        Statement stmt = prog.statements[0];
        assertInstanceOf(ExpressionStatement.class, stmt);

        Expression expr = ((ExpressionStatement)stmt).expression;
        assertInstanceOf(ArrayLiteral.class, expr);
        ArrayLiteral arr = ((ArrayLiteral)expr);
        assertEquals(3, arr.elements.length);
        
        testIntegerLiteral(arr.elements[0], 1);
        testInfixExpression(arr.elements[1], 2, "*", 2);
        testInfixExpression(arr.elements[2], 3, "+", 3);
    }

    @Test
    void infixIndexExpressionsShouldBeParsedCorrectly() {
        String input = "myArray[1 + 1]";

        Program prog = parserTestHelperFunction(input);
        checkProgHasExpectedNumStatements(prog, 1);

        assertInstanceOf(ExpressionStatement.class,
                         prog.statements[0]);
        ExpressionStatement stmt = ((ExpressionStatement)prog.statements[0]);

        assertInstanceOf(IndexExpression.class, stmt.expression);
        IndexExpression expr = ((IndexExpression)(stmt.expression));

        testIdentifier(expr.left, "myArray");
        testInfixExpression(expr.index, 1, "+",
                            1);
    }

    @Test
    void infixIndexOperateShouldHaveTheHighestPrecedence() {
        String input =
            "add(a * [1, 2, 3, 4][b * c] * d, a * b[2], b[1], 2 * [1, 2][1])";
        
        testOperatorPrecedence(
            input,
            "add(((a * ([1, 2, 3, 4][(b * c)])) * d), (a * (b[2])), (b[1]), (2 * ([1, 2][1])))"
        );
    }

    @Test
    void hashLiteralsWithStringKeysShouldBeParsedCorrectly() {
        String input = "{ \"one\": 1, \"two\": 2, \"three\": 3}";
        HashMap<String, Long> expectedOutput = new HashMap<>();
        expectedOutput.put("one", 1l);
        expectedOutput.put("two", 2l);
        expectedOutput.put("three", 3l);

        Program p = parserTestHelperFunction(input);
        assertInstanceOf(ExpressionStatement.class, p.statements[0]);

        ExpressionStatement stmt = (ExpressionStatement)(p.statements[0]);
        assertInstanceOf(HashLiteral.class, stmt.expression);

        HashLiteral hash = (HashLiteral)(stmt.expression);
        assertEquals(hash.pairs.size(), 3);

        for (var key : hash.pairs.keySet()) {
            assertInstanceOf(StringLiteral.class, key);
            assert(expectedOutput.containsKey(key.toString()));
            assertInstanceOf(IntegerLiteral.class, hash.pairs.get(key));
            testIntegerLiteral(
                hash.pairs.get(key),
                expectedOutput.get(key.toString())
            );
        }
    }

    @Test
    void hashLiteralsWithIntegerKeysShouldBeParsedCorrectly() {
        String input = "{ 1: \"one\", 2: \"two\", 3: \"three\" }";
        HashMap<Long, String> expectedOutput = new HashMap<>();
        expectedOutput.put(1l, "one");
        expectedOutput.put(2l, "two");
        expectedOutput.put(3l, "three");

        Program p = parserTestHelperFunction(input);
        assertInstanceOf(ExpressionStatement.class, p.statements[0]);

        ExpressionStatement stmt = (ExpressionStatement)(p.statements[0]);
        assertInstanceOf(HashLiteral.class, stmt.expression);

        HashLiteral hash = (HashLiteral)(stmt.expression);
        assertEquals(hash.pairs.size(), 3);

        for (var key : hash.pairs.keySet()) {
            assertInstanceOf(IntegerLiteral.class, key);
            assert(expectedOutput.containsKey(((IntegerLiteral)key).value));

            Expression value = hash.pairs.get(key);
            assertInstanceOf(StringLiteral.class, value);
            StringLiteral str = ((StringLiteral)value);
            assertEquals(expectedOutput.get(((IntegerLiteral)key).value), str.value);
        }
    }

    @Test
    void hashLiteralsWithBoolKeysShouldBeParsedCorrectly() {
        String input = "{ true: \"one\", false: \"two\" }";
        HashMap<Boolean, String> expectedOutput = new HashMap<>();
        expectedOutput.put(true, "one");
        expectedOutput.put(false, "two");

        Program p = parserTestHelperFunction(input);
        assertInstanceOf(ExpressionStatement.class, p.statements[0]);

        ExpressionStatement stmt = (ExpressionStatement)(p.statements[0]);
        assertInstanceOf(HashLiteral.class, stmt.expression);

        HashLiteral hash = (HashLiteral)(stmt.expression);
        assertEquals(hash.pairs.size(), 2);

        for (var key : hash.pairs.keySet()) {
            assertInstanceOf(Bool.class, key);
            assert(expectedOutput.containsKey(((Bool)key).value));

            Expression value = hash.pairs.get(key);
            assertInstanceOf(StringLiteral.class, value);
            StringLiteral str = ((StringLiteral)value);
            assertEquals(expectedOutput.get(((Bool)key).value), str.value);
        }
    }

    @Test
    void anEmptyHashLiteralShouldBeParsedCorrectly() {
        String input = "{}";

        Program p = parserTestHelperFunction(input);
        assertInstanceOf(ExpressionStatement.class, p.statements[0]);

        ExpressionStatement stmt = (ExpressionStatement)(p.statements[0]);
        assertInstanceOf(HashLiteral.class, ((ExpressionStatement)stmt).expression);
        assertEquals(0, ((HashLiteral)(stmt.expression)).pairs.size());
    }

    @Test
    void hashLiteralsCanBeBuiltWithAnyExpression() {
        String input = "{ \"one\": 0 + 1, \"two\": 10 - 8, \"three\": 15 / 5 }";
        String[] expectedKeys = { "one", "two", "three" };
        long[] expectedLefts = { 0, 10, 15 };
        String[] expectedOperators = { "+", "-", "/" };
        long[] expectedRights = { 1, 8, 5 };

        Program p = parserTestHelperFunction(input);
        assertInstanceOf(ExpressionStatement.class, p.statements[0]);

        ExpressionStatement stmt = (ExpressionStatement)(p.statements[0]);
        assertInstanceOf(HashLiteral.class, stmt.expression);

        HashLiteral hash = (HashLiteral)(stmt.expression);
        assertEquals(hash.pairs.size(), 3);

        for (Expression key : hash.pairs.keySet()) {
            assertInstanceOf(StringLiteral.class, key);
            /*
                Order is not guaranteed in HashMaps
                See: https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html 
             */
            assert(Arrays.asList(expectedKeys).contains(((StringLiteral)key).value));
            // might be a better way of doing this
            int i = Arrays.asList(expectedKeys).indexOf(((StringLiteral)key).value);

            Expression value = hash.pairs.get(key);
            assertInstanceOf(InfixExpression.class, value);

            InfixExpression expr = (InfixExpression)value;
            assertInstanceOf(IntegerLiteral.class, expr.left);
            assertEquals(expectedLefts[i], ((IntegerLiteral)(expr.left)).value);

            assertEquals(expectedOperators[i], expr.operator);

            assertInstanceOf(IntegerLiteral.class, expr.right);
            assertEquals(expectedRights[i], ((IntegerLiteral)(expr.right)).value);
        }
    }

}