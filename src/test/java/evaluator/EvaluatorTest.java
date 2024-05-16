package evaluator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import lexer.Lexer;
import monkeyobject.MonkeyBool;
import monkeyobject.MonkeyInt;
import monkeyobject.MonkeyObject;
import parser.Parser;

class EvaluatorTest {
    /* Helper Functions */
    MonkeyObject testEval(String input) {
        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();

        return Evaluator.Eval(prog);
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

    // Does this really need to be its own method?
    // testEvalBoolExpression is exactly the same
    void testBangOperator(String input, boolean expected) {
        MonkeyObject evaluated = testEval(input);
        testBoolObject(evaluated, expected);
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

}