package evaluator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import lexer.Lexer;
import monkeyobject.MonkeyInt;
import monkeyobject.MonkeyObject;
import parser.Parser;

class EvaluatorTest {
    MonkeyObject testEval(String input) {
        var l = new Lexer(input);
        var p = new Parser(l);
        var prog = p.parseProgram();

        return Eval(prog);
    }

    void testIntegerObject(MonkeyObject obj, long expected) {
        assertInstanceOf(MonkeyInt.class, obj);

        MonkeyInt intObj = (MonkeyInt)obj;
        assertEquals(intObj.value, expected);
    }

    void testEvalIntegerExpression(String input, long expected) {
        MonkeyObject evaluated = testEval(input);
        testIntegerObject(evaluated, expected);
    }

    @Test
    void fiveShouldParseAsIntegerObjectWithValueOf5() {
        String input = "5";
        long expected = 5;

        testEvalIntegerExpression(input, expected);
    }

    @Test
    void tenShouldParseAsIntegerObjectWithValueOf10() {
        String input = "10";
        long expected = 10;

        testEvalIntegerExpression(input, expected);
    }
}
