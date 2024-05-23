package evaluator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import ast.Program;
import lexer.Lexer;
import monkeyobject.Environment;
import monkeyobject.MonkeyBool;
import monkeyobject.MonkeyError;
import monkeyobject.MonkeyFunction;
import monkeyobject.MonkeyInt;
import monkeyobject.MonkeyNull;
import monkeyobject.MonkeyObject;
import parser.Parser;

class EvaluatorTest {
    /* Helper Functions */
    MonkeyObject testEval(String input) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program prog = p.parseProgram();
        Environment env = new Environment();

        return Evaluator.Eval(prog, env);
    }

    void testIntegerObject(MonkeyObject obj, long expected) {
        assertInstanceOf(MonkeyInt.class, obj);

        MonkeyInt intObj = (MonkeyInt)obj;
        assertEquals(expected, intObj.value);
    }

    void testBoolObject(MonkeyObject obj, boolean expected) {
        assertInstanceOf(MonkeyBool.class, obj);

        MonkeyBool intObj = (MonkeyBool)obj;
        assertEquals(expected, intObj.value);
    }

    void testEvalIntegerExpression(String input, long expected) {
        MonkeyObject evaluated = testEval(input);
        testIntegerObject(evaluated, expected);
    }

    void testEvalBoolExpression(String input, boolean expected) {
        MonkeyObject evaluated = testEval(input);
        testBoolObject(evaluated, expected);
    }

    void testEvalConditionalExpression(String input, long expected) {
        MonkeyObject evaluated = testEval(input);
        assertInstanceOf(MonkeyInt.class, evaluated);
        assertEquals(expected, ((MonkeyInt)evaluated).value);
    }

    void testEvalConditionalExpressionIsNull(String input) {
        MonkeyObject evaluated = testEval(input);
        assertInstanceOf(MonkeyNull.class, evaluated);
    }

    // Does this really need to be its own method?
    // testEvalBoolExpression is exactly the same
    void testBangOperator(String input, boolean expected) {
        MonkeyObject evaluated = testEval(input);
        testBoolObject(evaluated, expected);
    }

    void testEvalReturnStatements(String input, long expected) {
        MonkeyObject evaluated = testEval(input);
        testIntegerObject(evaluated, expected);
    }

    void testEvalErrorHandling(String input, String expected) {
        MonkeyObject evaluated = testEval(input);

        assertInstanceOf(MonkeyError.class, evaluated);
        MonkeyError errObj = (MonkeyError)evaluated;
        assertEquals(expected, errObj.message);
    }

    void testEvalLetStatements(String input, long expected) {
        testIntegerObject(testEval(input), expected);
    }

    /* Tests */
    @Test
    void fiveShouldParseAsIntegerObjectWithValueOf5() {
        String input = "5";
        long expected = 5;

        testEvalIntegerExpression(input, expected);
    }

    @Test
    void minusFiveShouldParseAsIntObjectWithValueOfNeg5() {
        String input = "-5";
        long expected = -5;

        testEvalIntegerExpression(input, expected);
    }

    @Test
    void fivePlusFivePlusFivePlusFiveMinusTenShouldEqual10() {
        String input = "5 + 5 + 5 + 5 - 10";
        long expected = 10;

        testEvalIntegerExpression(input, expected);
    }

    @Test
    void twoTimesTwoTimesTwoTimesTwoTimesTwoShouldEqual32() {
        String input = "2 * 2 * 2 * 2 * 2";
        long expected = 32;

        testEvalIntegerExpression(input, expected);
    }

    @Test
    void negFiftyPlusOneHundredPlusNegFiftyShouldEqual0() {
        String input = "-50 + 100 + -50";
        long expected = 0;

        testEvalIntegerExpression(input, expected);
    }

    @Test
    void fiveTimesTwoPlusTenShouldEqual20() {
        String input = "5 * 2 + 10";
        long expected = 20;

        testEvalIntegerExpression(input, expected);
    }
    
    @Test
    void fivePlusTwoTimesTenShouldEqual25() {
        String input = "5 + 2 * 10";
        long expected = 25;

        testEvalIntegerExpression(input, expected);
    }

    @Test
    void twentyPlusTwoTimesNegTenShouldEqual0() {
        String input = "20 + 2 * -10";
        long expected = 0;

        testEvalIntegerExpression(input, expected);
    }

    @Test
    void fiftyDividedByTwoTimesTwoPlusTenShouldEqual60() {
        String input = "50 / 2 * 2 + 10";
        long expected = 60;

        testEvalIntegerExpression(input, expected);
    }

    @Test
    void twoTimesFivePlusTenWithParenthesisShouldEqual30() {
        String input = "2 * (5 + 10)";
        long expected = 30;

        testEvalIntegerExpression(input, expected);
    }

    @Test
    void threeTimesThreeTimesThreePlusTenShouldEqual37() {
        String input = "3 * 3 * 3 + 10";
        long expected = 37;

        testEvalIntegerExpression(input, expected);
    }

    @Test
    void threeTimesThreeTimesThreePlusTenWithParenthesisShouldEqual37() {
        String input = "3 * (3 * 3) + 10";
        long expected = 37;

        testEvalIntegerExpression(input, expected);
    }

    @Test
    void fivePlusTenTimesTwoPlusFifteenDividedByThreeTimesTwoPlusNegTenWithParenthesisShouldEqual50() {
        String input = "(5 + 10 * 2 + 15 / 3) * 2 + -10";
        long expected = 50;

        testEvalIntegerExpression(input, expected);
    }

    @Test
    void trueShouldParseAsBoolObjectWithValueOfTrue() {
        String input = "true";
        boolean expected = true;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void falseShouldParseAsBoolObjectWithValueOfFalse() {
        String input = "false";
        boolean expected = false;

        testEvalBoolExpression(input, expected);
    }

    @Test 
    void bangTrueShouldParseAsBoolObjectWithValueOfFalse() {
        String input = "!true";
        boolean expected = false;

        testBangOperator(input, expected);
    }

    @Test
    void bangFalseShouldParseAsBoolObjectWithValueOfTrue() {
        String input = "!false";
        boolean expected = true;

        testBangOperator(input, expected);
    }

    @Test
    void bangFiveShouldParseAsBoolObjectWithValueOfFalse() {
        String input = "!5";
        boolean expected = false;

        testBangOperator(input, expected);
    }

    @Test
    void bangBangTrueShouldParseAsBoolObjectWithValueOfTrue() {
        String input = "!!true";
        boolean expected = true;

        testBangOperator(input, expected);
    }

    @Test
    void bangBangFalseShouldParseAsBoolObjectWithValueOfFalse() {
        String input = "!!false";
        boolean expected = false;

        testBangOperator(input, expected);
    }

    @Test
    void bangBangFiveShouldParseAsBoolObjectWithValueOfTrue() {
        String input = "!!5";
        boolean expected = true;

        testBangOperator(input, expected);
    }

    @Test
    void oneLessThanTwoShouldEqualTrue() {
        String input = "1 < 2";
        boolean expected = true;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void oneGreaterThanTwoShoulEqualFalse() {
        String input = "1 > 2";
        boolean expected = false;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void oneLessThanOneShouldEqualFalse() {
        String input = "1 < 1";
        boolean expected = false;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void oneGreaterThanOneShouldEqualFalse() {
        String input = "1 > 1";
        boolean expected = false;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void oneEqualsEqualsOneShouldEqualTrue() {
        String input = "1 == 1";
        boolean expected = true;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void oneBangEqualsOneShouldEqualFalse() {
        String input = "1 != 1";
        boolean expected = false;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void oneEqualsEqualsTwoShouldEqualFalse() {
        String input = "1 == 2";
        boolean expected = false;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void oneBangEqualsTwoShouldEqualTrue() {
        String input = "1 != 2";
        boolean expected = true;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void trueEqualsEqualsTrueShouldEqualTrue() {
        String input = "true == true";
        boolean expected = true;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void falseEqualsEqualsFalseShouldEqualTrue() {
        String input = "false == false";
        boolean expected = true;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void trueEqualsEqualsFalseShouldEqualFalse() {
        String input = "true == false";
        boolean expected = false;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void trueBangEqualsFalseShouldEqualTrue() {
        String input = "true != false";
        boolean expected = true;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void falseBangEqualsTrueShouldEqualTrue() {
        String input = "false != true";
        boolean expected = true;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void oneLessThanTwoInParenthesisEqualsEqualsTrueShouldEqualTrue() {
        String input = "(1 < 2) == true";
        boolean expected = true;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void oneLessThanTwoInParenthesisEqualsEqualsFalseShouldEqualFalse() {
        String input = "(1 < 2) == false";
        boolean expected = false;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void oneGreaterThanTwoInParenthesisEqualsEqualsTrueShouldEqualFalse() {
        String input = "(1 > 2) == true";
        boolean expected = false;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void oneGreaterThanTwoInParenthesisEqualsEqualsFalseShouldEqualTrue() {
        String input = "(1 > 2) == false";
        boolean expected = true;

        testEvalBoolExpression(input, expected);
    }

    @Test
    void ifTrueThenTenShouldEvaluateTo10() {
        String input = "if (true) { 10 }";
        long expectedOutput = 10;

        testEvalConditionalExpression(input, expectedOutput);
    }

    @Test
    void ifFalseThenTenShouldEvaluateToNull() {
        String input = "if (false) { 10 }";

        testEvalConditionalExpressionIsNull(input);
    }

    @Test
    void ifOneThen10ShouldEvaluateTo10() {
        String input = "if (1) { 10 }";
        long expectedOutput = 10;

        testEvalConditionalExpression(input, expectedOutput);
    }

    @Test
    void ifOneLessThanTwoThenTenShouldEvaluateTo10() {
        String input = "if (1 < 2) { 10 }";
        long expectedOutput = 10;

        testEvalConditionalExpression(input, expectedOutput);
    }

    @Test
    void ifOneGreaterThanTwoThenTenShouldEvaluateToNull() {
        String input = "if (1 > 2) { 10 }";

        testEvalConditionalExpressionIsNull(input);
    }

    @Test
    void ifOneGreaterThanTwoThenTenElseTwentyShouldEvaluateTo20() {
        String input = "if (1 > 2) { 10 } else { 20 }";
        long expectedOutput = 20;

        testEvalConditionalExpression(input, expectedOutput);
    }

    @Test
    void ifOneLessThanTwoThenTenElseTwentyShouldEvaluateTo20() {
        String input = "if (1 < 2) { 10 } else { 20 }";
        long expectedOutput = 10;

        testEvalConditionalExpression(input, expectedOutput);
    }

    @Test
    void returnTenShouldReturnTheValue10() {
        String input = "return 10;";
        long expectedOutput = 10;

        testEvalReturnStatements(input, expectedOutput);
    }

    @Test
    void returnTenFollowedByNineShouldReturnTheValue10() {
        String input = "return 10; 9;";
        long expectedOutput = 10;

        testEvalReturnStatements(input, expectedOutput);
    }

    @Test
    void IfTenGreaterThanOneReturn10NestedWithinIfTenGreaterThanOneReturn1ShouldReturn10() {
        String input = """
            if (10 > 1) {
                if (10 > 1) {
                    return 10;
                }

                return 1;
            }""";
        long expectedOutput = 10;

        testEvalReturnStatements(input, expectedOutput);
    }

    @Test
    void fivePlusTrueShouldProduceATypeMismatchError() {
        String input = "5 + true;";
        String expectedOutput = "type mismatch: INTEGER + BOOLEAN";

        testEvalErrorHandling(input, expectedOutput);
    }

    @Test
    void fivePlusTrueSemicolonFiveShouldProduceATypeMismatchError() {
        String input = "5 + true; 5;";
        String expectedOutput = "type mismatch: INTEGER + BOOLEAN";

        testEvalErrorHandling(input, expectedOutput);
    }

    @Test
    void minusTrueShouldProduceAnUnknownOperatorError() {
        String input = "-true";
        String expectedOutput = "unknown operator: -BOOLEAN";

        testEvalErrorHandling(input, expectedOutput);
    }

    @Test
    void truePlusFalseShouldProduceAnUnknownOperatorError() {
        String input = "true + false";
        String expectedOutput = "unknown operator: BOOLEAN + BOOLEAN";

        testEvalErrorHandling(input, expectedOutput);
    }

    @Test
    void fiveSemiTruePlusFalseSemiFiveShouldProduceAnUnknownOperatorError() {
        String input = "5; true + false; 5";
        String expectedOutput = "unknown operator: BOOLEAN + BOOLEAN";

        testEvalErrorHandling(input, expectedOutput);
    }

    @Test
    void ifTenGreaterThanOneThenTruePlusFalseShouldProduceAnUnknownOperatorError() {
        String input = "if (10 > 1) { true + false; }";
        String expectedOutput = "unknown operator: BOOLEAN + BOOLEAN";

        testEvalErrorHandling(input, expectedOutput);
    }

    @Test
    void ifTenGreaterThanOneThenTruePlusFalseNestedWithinIfTenGreaterThanOneThen1ShouldProduceAnUnknownOperatorError() {
        String input = """
            if (10 > 1) {
                if (10 > 1) {
                    return true + false;
                }
                return 1;
            }""";;
        String expectedOutput = "unknown operator: BOOLEAN + BOOLEAN";

        testEvalErrorHandling(input, expectedOutput);
    }

    @Test
    void foobarShouldProduceAnIdentifierNotFoundError() {
        String input = "foobar";
        String expectedOutput = "identifier not found: foobar";

        testEvalErrorHandling(input, expectedOutput);
    }

    @Test
    void letAEqualFiveSemiAShouldReturn5() {
        String input = "let a = 5; a;";
        long expected = 5;

        testEvalLetStatements(input, expected);
    }

    @Test
    void letAEqualFiveTimesFiveSemiAShouldReturn25() {
        String input = "let a = 5 * 5; a;";
        long expected = 25;

        testEvalLetStatements(input, expected);
    }

    @Test
    void letAEqualFiveSemiLetBEqualASemiBShouldReturn5() {
        String input = "let a = 5; let b = a; b;";
        long expected = 5;

        testEvalLetStatements(input, expected);
    }

    @Test
    void letAEqualFiveSemiLetBEqualASemiLetCEqualAPlusBPlus5SemiCShouldReturn15() {
        String input = "let a = 5; let b = a; let c = a + b + 5; c;";
        long expected = 15;

        testEvalLetStatements(input, expected);
    }
    
    @Test
    void evaluatorShouldBeAbleToBuildAFunctionObjectCorrectly() {
        String input = "fn(x) { x + 2; }";
        MonkeyObject evaluated = testEval(input);

        assertInstanceOf(MonkeyFunction.class, evaluated);
        MonkeyFunction fn = (MonkeyFunction)evaluated;

        assertEquals(1, fn.parameters.size());
        assertEquals("x", fn.parameters.get(0).toString());

        // Might have to put this in brackets
        String expectedBody = "(x + 2)";
        assertEquals(expectedBody, fn.body.toString());
    }
}