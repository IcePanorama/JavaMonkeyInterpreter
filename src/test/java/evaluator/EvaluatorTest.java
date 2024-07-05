package evaluator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import ast.Program;
import lexer.Lexer;
import monkeyobject.Environment;
import monkeyobject.HashKey;
import monkeyobject.MonkeyArray;
import monkeyobject.MonkeyBool;
import monkeyobject.MonkeyError;
import monkeyobject.MonkeyFunction;
import monkeyobject.MonkeyHash;
import monkeyobject.MonkeyInt;
import monkeyobject.MonkeyNull;
import monkeyobject.MonkeyObject;
import monkeyobject.MonkeyString;
import parser.Parser;

class EvaluatorTest {
    /* Helper Functions */
    MonkeyObject testEval(String input) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program prog = p.parseProgram();
        Environment env = new Environment();

        return Evaluator.eval(prog, env);
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

    void testEvalNullConditionalExpression(String input) {
        MonkeyObject evaluated = testEval(input);
        assertInstanceOf(MonkeyNull.class, evaluated);
    }

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

    void testStringObject(MonkeyObject obj, String expected) {
        assertInstanceOf(MonkeyString.class, obj);

        MonkeyString str = (MonkeyString)obj;
        assertEquals(expected, str.value);
    }

    MonkeyObject testEvalFunctions(String input, String[] expectedParams,
        String expectedBody) {
        MonkeyObject evaluated = testEval(input);

        assertInstanceOf(MonkeyFunction.class, evaluated);
        MonkeyFunction fn = (MonkeyFunction)evaluated;

        assertEquals(expectedParams.length, fn.parameters.size());
        int i;
        for (i = 0; i < expectedParams.length; i++) {
//TODO: should be able to natively support comparing identifiers
            assertEquals(expectedParams[i], fn.parameters.get(i).toString());
        }
        assertEquals(i, fn.parameters.size());
        assertEquals(expectedBody, fn.body.toString());

        return evaluated;
    }

    void testEvalArrayLiterals(MonkeyObject obj, long... expected) {
        assertInstanceOf(MonkeyArray.class, obj);
        MonkeyArray arr = (MonkeyArray)obj;
        
        assertEquals(expected.length, arr.elements.length);

        for (int i = 0; i < expected.length; i++) {
            testIntegerObject(arr.elements[i], expected[i]);
        }
    }

    void testEvalArrayIndexOperators(MonkeyObject obj, long expected) {
        assertInstanceOf(MonkeyInt.class, obj);
        testIntegerObject((MonkeyInt)obj, expected);
    }

    void testEvalNullObject(MonkeyObject obj) {
        assertInstanceOf(MonkeyNull.class, obj);
    }

    void testEvalArrayIndexOperators(MonkeyObject obj) {
        testEvalNullObject(obj);
    }
    
    void testEvalArrayObject(MonkeyObject obj, MonkeyArray expected) {
        assertInstanceOf(MonkeyArray.class, obj);
        assertEquals(expected, (MonkeyArray)obj);
    }

    void testEvalHashObjects(MonkeyObject obj, String expectedKey,
                             long expectedValue) {
        assertInstanceOf(MonkeyHash.class, obj);

        MonkeyHash result = (MonkeyHash)obj;
        assertEquals(1, result.pairs.size());

        HashKey key = (HashKey)(result.pairs.keySet().toArray()[0]);
        assertEquals((new MonkeyString(expectedKey)).getHashKey(), key);
        testIntegerObject(result.pairs.get(key).value, expectedValue);
    }

    void testEvalHashObjects(MonkeyObject obj, long expectedKey,
                             long expectedValue) {
        assertInstanceOf(MonkeyHash.class, obj);

        MonkeyHash result = (MonkeyHash)obj;
        assertEquals(1, result.pairs.size());

        HashKey key = (HashKey)(result.pairs.keySet().toArray()[0]);
        assertEquals((new MonkeyInt(expectedKey)).getHashKey(), key);
        testIntegerObject(result.pairs.get(key).value, expectedValue);
    }

    void testEvalHashObjects(MonkeyObject obj, boolean expectedKey,
                             long expectedValue) {
        assertInstanceOf(MonkeyHash.class, obj);

        MonkeyHash result = (MonkeyHash)obj;
        assertEquals(1, result.pairs.size());

        HashKey key = (HashKey)(result.pairs.keySet().toArray()[0]);
        assertEquals((new MonkeyBool(expectedKey)).getHashKey(), key);
        testIntegerObject(result.pairs.get(key).value, expectedValue);
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

        testEvalNullConditionalExpression(input);
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

        testEvalNullConditionalExpression(input);
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
    void helloInQuotesMinusWorldInQuotesShouldProduceAnUnknownOperatorError() {
        String input = "\"Hello\" - \"World\"";
        testEvalErrorHandling(input, "unknown operator: STRING - STRING");
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
        testEvalFunctions(input, new String[] {"x"}, "(x + 2)");
    }

    @Test
    void functionObjectShouldBeAbleToImplicitlyReturnValue() {
        String input = "let identity = fn(x) { x; }; identity(5);";
        testIntegerObject(testEval(input), 5);
    }

    @Test
    void functionObjectShouldBeAbleToExplicitlyReturnValue() {
        String input = "let identity = fn(x) { return x; }; identity(5);";
        testIntegerObject(testEval(input), 5);
    }

    @Test
    void functionWithSingleParameterShouldBeAbleToUsePassedValueInExpression() {
        String input = "let double = fn(x) { x * 2; }; double(5);";
        testIntegerObject(testEval(input), 10);
    }

    @Test
    void functionWithTwoParametersShouldBeAbleToUseBothInExpression() {
        String input = "let add = fn(x, y) { x + y; }; add(5, 5);";
        testIntegerObject(testEval(input), 10);
    }

    @Test
    void functionObjectsShouldEvaluateArgumentsPriorToPassingThem() {
        String input = "let add = fn(x, y) { x + y; }; add(5 + 5, add(5, 5));";
        testIntegerObject(testEval(input), 20);
    }

    @Test
    void functionObjectsShouldSupportAnonymousFunctions() {
        String input = "fn(x) { x; }(5)";
        testIntegerObject(testEval(input), 5);
    }

    @Test
    void functionObjectsShouldSupportClosures() {
        String input = "let newAdder = fn(x) {fn(y) { x + y };}; let addTwo = newAdder(2); addTwo(2);";
        testIntegerObject(testEval(input), 4);
    }

    @Test
    void helloWorldInQuotesShouldCreateAStringObjWithAValueOfHelloWorld() {
        String input = "\"Hello World\"";
        testStringObject(testEval(input), "Hello World");
    }

    @Test
    void stringObjectsShouldSupportConcatenation() {
        String input = "\"Hello\" + \" \" + \"World!\"";
        testStringObject(testEval(input), "Hello World!");
    }

    @Test
    void stringObjectsShouldSupportEqualsOperator() {
        String input = "\"Hello\" == \"Hello\";";
        testEvalBoolExpression(input, true);
    }

    @Test
    void stringObjectsShouldSupportNotEqualsOperator() {
        String input = "\"Hello\" != \"World\"";
        testEvalBoolExpression(input, true);
    }

    @Test
    void builtinLenShouldReturnZeroWhenGivenAnEmptyString() {
        String input = "len(\"\")";
        MonkeyObject evaluated = testEval(input);
        testIntegerObject(evaluated, 0);
    }

    @Test
    void builtinLenShouldCalculateTheCorrectLengthOfAGivenString() {
        String input = "len(\"four\")";
        MonkeyObject evaluated = testEval(input);
        testIntegerObject(evaluated, 4);
    }

    @Test
    void builtinLenShouldProduceAnArgumentNotSupportedErrorWhenGivenAnInt() {
        String input = "len(1)";
        testEvalErrorHandling(input, "argument to \'len\' not supported, got INTEGER");
    }

    @Test
    void builtinLenShouldProduceAWrongNumberOfArgumentsErrorWhenGivenMoreThanOneString() {
        String input = "len(\"one\", \"two\")";
        testEvalErrorHandling(input, "wrong number of arguments: got=2, want=1");
    }

    @Test
    void builtinLenShouldCalculateTheCorrectLengthOfAGivenArray() {
        String input = "len([1, 2, 3, 4])";
        MonkeyObject evaluated = testEval(input);
        testIntegerObject(evaluated, 4);
    }
    
    @Test
    void builtinLenShouldProduceAWrongNumberOfArgumentsErrorWhenGivenMoreThanOneArray() {
        String input = "len([1, 2], [3, 4])";
        testEvalErrorHandling(input, "wrong number of arguments: got=2, want=1");
    }

    @Test
    void builtinFirstShouldReturnNullWhenGivenAnEmptyArray() {
        String input = "first([]);";
        testEvalNullObject(testEval(input));
    }

    @Test
    void builtinFirstShouldReturnTheFirstElementOfAGivenArray() {
        String input = "first([1, 2, 3, 4])";
        testIntegerObject(testEval(input), 1);
    }

    @Test
    void builtinFirstShouldProduceAnArgumentNotSupportedErrorWhenGivenAnInt() {
        String input = "first(1)";
        testEvalErrorHandling(input, "argument to \'first\' not supported, got INTEGER");
    }

    @Test
    void builtinFirstShouldProduceAWrongNumberOfArgumentsErrorWhenGivenMoreThanOneArray() {
        String input = "first([1, 2], [3, 4])";
        testEvalErrorHandling(input, "wrong number of arguments: got=2, want=1");
    }

    @Test
    void builtinLastShouldReturnNullWhenGivenAnEmptyArray() {
        String input = "last([]);";
        testEvalNullObject(testEval(input));
    }

    @Test
    void builtinLastShouldReturnTheLastElementOfAGivenArray() {
        String input = "last([1, 2, 3, 4])";
        testIntegerObject(testEval(input), 4);
    }

    @Test
    void builtinLastShouldProduceAnArgumentNotSupportedErrorWhenGivenAnInt() {
        String input = "last(1)";
        testEvalErrorHandling(input, "argument to \'last\' not supported, got INTEGER");
    }

    @Test
    void builtinLastShouldProduceAWrongNumberOfArgumentsErrorWhenGivenMoreThanOneArray() {
        String input = "last([1, 2], [3, 4])";
        testEvalErrorHandling(input, "wrong number of arguments: got=2, want=1");
    }

    @Test
    void builtinRestShouldReturnNullWhenGivenAnEmptyArray() {
        String input = "rest([]);";
        testEvalNullObject(testEval(input));
    }

    @Test
    void builtinRestShouldReturnAnEmptyArrayWhenGivenAnArrayWithOneElement() {
        String input = "rest([1]);";
        testEvalArrayObject(
            testEval(input), 
            new MonkeyArray(new MonkeyObject[] {})
        );
    }

    @Test
    void builtinRestShouldReturnAllButTheFirstElementOfAGivenArray() {
        String input = "rest([1, 2, 3, 4])";
        testEvalArrayObject(
            testEval(input),
            new MonkeyArray(
                new MonkeyObject[] {
                    new MonkeyInt(2),
                    new MonkeyInt(3),
                    new MonkeyInt(4)
                }
            )
        );
    }

    @Test
    void builtinRestShouldProduceAnArgumentNotSupportedErrorWhenGivenAnInt() {
        String input = "rest(1)";
        testEvalErrorHandling(input, "argument to \'rest\' not supported, got INTEGER");
    }

    @Test
    void builtinRestShouldProduceAWrongNumberOfArgumentsErrorWhenGivenMoreThanOneArray() {
        String input = "rest([1, 2], [3, 4])";
        testEvalErrorHandling(input, "wrong number of arguments: got=2, want=1");
    }

    @Test
    void builtinPushShouldReturnAnArrayWithOneElementWhenGivenAnEmptyArray() {
        String input = "push([], 1)";
        testEvalArrayObject(
            testEval(input),
            new MonkeyArray(new MonkeyObject[] { new MonkeyInt(1)})
        );
    }

    @Test
    void builtinPushShouldAppendANewValueToAGivenArray() {
        String input = "push([1, 2], 3)";
        testEvalArrayObject(
            testEval(input),
            new MonkeyArray(
                new MonkeyObject[] {
                    new MonkeyInt(1),
                    new MonkeyInt(2),
                    new MonkeyInt(3)
                }
            )
        );
    }

    @Test
    void builtinPushShouldProduceAnArgumentNotSupportedErrorWhenGivenAnInt() {
        String input = "push(1, 2)";
        testEvalErrorHandling(input, "argument to \'push\' not supported, got INTEGER");
    }

    @Test
    void builtinPushShouldProduceAWrongNumberOfArgumentsErrorWhenGivenLessThanTwoArguments() {
        String input = "push([]);";
        testEvalErrorHandling(input, "wrong number of arguments: got=1, want=2");
    }

    @Test
    void builtinPushShouldProduceAWrongNumberOfArgumentsErrorWhenGivenMoreThanTwoArguments() {
        String input = "push([], 1, 2);";
        testEvalErrorHandling(input, "wrong number of arguments: got=3, want=2");
    }

    @Test
    void arrayLiteralElementsShouldBeEvaluated() {
        String input = "[1, 2 * 2, 3 + 3]";
        MonkeyObject evaluated = testEval(input);
        testEvalArrayLiterals(evaluated, 1, 4, 6);
    }

    @Test
    void indexOperatorWithAValueOfZeroShouldReturnTheFirstItemFromAList() {
        String input = "[1, 2, 3][0];";
        long expectedOutput = 1;

        testEvalArrayIndexOperators(testEval(input), expectedOutput);
    }

    @Test
    void indexOperatorWithAValueOfOneShouldReturnTheSecondItemFromAList() {
        String input = "[1, 2, 3][1];";
        long expectedOutput = 2;

        testEvalArrayIndexOperators(testEval(input), expectedOutput);
    }

    @Test
    void indexOperatorWithAValueOfTwoShouldReturnTheThirdItemFromAList() {
        String input = "[1, 2, 3][2];";
        long expectedOutput = 3;

        testEvalArrayIndexOperators(testEval(input), expectedOutput);
    }

    @Test
    void indexOperatorShouldSupportVariablesAsValues() {
        String input = "let i = 0; [1][i];";
        long expectedOutput = 1;

        testEvalArrayIndexOperators(testEval(input), expectedOutput);
    }

    @Test
    void indexOperatorShouldSupportExpressionsAsValues() {
        String input = "[1, 2, 3][1 + 1];";
        long expectedOutput = 3;

        testEvalArrayIndexOperators(testEval(input), expectedOutput);
    }

    @Test
    void indexOperatorShouldSupportAccessingElementsOfPreviouslyDeclaredArray() {
        String input = "let myArray = [1, 2, 3]; myArray[2];";
        long expectedOutput = 3;

        testEvalArrayIndexOperators(testEval(input), expectedOutput);
    }

    // better name?
    @Test
    void precedenceOfArrayIndexOperatorsShouldWorkAsExpected() {
        String input = "let myArray = [1, 2, 3]; myArray[0] + myArray[1] + myArray[2];";
        long expectedOutput = 6;

        testEvalArrayIndexOperators(testEval(input), expectedOutput);
    }

    @Test
    void indexOperatorOutOfRangeShouldReturnNull() {
        String input = "[1, 2, 3][3]";

        testEvalArrayIndexOperators(testEval(input));
    }

    @Test
    void indexOperatorNegativeValueShouldReturnNull() {
        String input = "[1, 2, 3][-1]";

        testEvalArrayIndexOperators(testEval(input));
    }

    @Test
    void hashesCanBeConstructedWithAStringKey() {
        String input = "{\"one\": 10 - 9}";
        testEvalHashObjects(testEval(input), "one", 1);
    }

    @Test
    void hashesCanBeConstructedWithVariableKeys() {
        String input = "let two = \"two\"; {two : 1 + 1}";
        testEvalHashObjects(testEval(input), "two", 2);
    }

    @Test
    void hashesCanBeConstructedWithConcatenatedStringKeys() {
        String input = "{\"thr\" + \"ee\": 6 / 2}";
        testEvalHashObjects(testEval(input), "three", 3);
    }

    @Test
    void hashesCanBeConstructedWithIntegerKeys() {
        String input = "{4 : 4}";
        testEvalHashObjects(testEval(input), 4, 4);
    }

    @Test
    void hashesCanBeConstructedWithTrueBoolKeys() {
        String input = "{true : 5}";
        testEvalHashObjects(testEval(input), true, 5);
    }

    @Test
    void hashesCanBeConstructedWithFalseBoolKeys() {
        String input = "{false : 6}";
        testEvalHashObjects(testEval(input), false, 6);
    }

    @Test
    void hashesCanBeIndexedWithStrings() {
        String input = "{\"foo\": 5}[\"foo\"]";

        MonkeyObject evaluated = testEval(input);
        testIntegerObject(evaluated, 5l);
    }

    @Test
    void hashesShouldReturnNullWhenIndexedWithUnknownKey() {
        String input = "{\"foo\": 5}[\"bar\"]";

        MonkeyObject evaluated = testEval(input);
        testEvalNullObject(evaluated);
    }

    @Test
    void hashesCanBeIndexedWithVariables() {
        String input = "let key = \"foo\"; {\"foo\": 5}[key]";

        MonkeyObject evaluated = testEval(input);
        testIntegerObject(evaluated, 5l);
    }

    @Test
    void indexingAnEmptyHashShouldReturnNull() {
        String input = "{}[\"foo\"]";

        MonkeyObject evaluated = testEval(input);
        testEvalNullObject(evaluated);
    }

    @Test
    void hashesCanBeIndexedWithInts() {
        String input = "{5: 5}[5]";

        MonkeyObject evaluated = testEval(input);
        testIntegerObject(evaluated, 5l);
    }

    @Test
    void hashesCanBeIndexedWithTrueBools() {
        String input = "{true: 5}[true]";

        MonkeyObject evaluated = testEval(input);
        testIntegerObject(evaluated, 5l);
    }

    @Test
    void hashesCanBeIndexedWithFalseBools() {
        String input = "{false: 5}[false]";

        MonkeyObject evaluated = testEval(input);
        testIntegerObject(evaluated, 5l);
    }

    @Test
    void hashesShouldProduceAnErrorWhenIndexedWithAFunction() {
        String input = "{\"name\": \"Monkey\"}[fn(x) {x}];";

        testEvalErrorHandling(input, "unusable as hash key: FUNCTION");
    }
}