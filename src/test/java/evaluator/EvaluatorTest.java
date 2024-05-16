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
    MonkeyObject testEval(String input) {
        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();

        return Evaluator.Eval(prog);
    }

    void testIntegerObject(MonkeyObject obj, long expected) {
        assertInstanceOf(MonkeyInt.class, obj);

        MonkeyInt intObj = (MonkeyInt)obj;
        assertEquals(intObj.value, expected);
    }

    void testBoolObject(MonkeyObject obj, boolean expected) {
        assertInstanceOf(MonkeyBool.class, obj);

        MonkeyBool intObj = (MonkeyBool)obj;
        assertEquals(intObj.value, expected);
    }

    void testEvalIntegerExpression(String input, long expected) {
        MonkeyObject evaluated = testEval(input);
        testIntegerObject(evaluated, expected);
    }

    void testEvalBoolExpression(String input, boolean expected) {
        MonkeyObject evaluated = testEval(input);
        testBoolObject(evaluated, expected);
    }

    @Test
    void fiveShouldParseAsIntegerObjectWithValueOf5() {
        String input = "5";
        long expected = 5;

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
}
