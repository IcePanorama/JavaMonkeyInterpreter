package evaluator;

import java.util.ArrayList;

import ast.Bool;
import ast.ExpressionStatement;
import ast.IntegerLiteral;
import ast.Node;
import ast.Program;
import ast.Statement;
import monkeyobject.MonkeyBool;
import monkeyobject.MonkeyInt;
import monkeyobject.MonkeyObject;

public final class Evaluator {
    private Evaluator() {}

    private static MonkeyObject evalStatements(ArrayList<Statement> statements) {
        MonkeyObject result = null;

        for (var stmt : statements) {
            result = Eval(stmt);
        }

        return result;
    }

    public static MonkeyObject Eval(Node node) {
        if (node instanceof Program) {
            return evalStatements(((Program)node).statements);
        } else if (node instanceof ExpressionStatement) {
            return Eval(((ExpressionStatement)node).expression);
        } else if (node instanceof Bool) {
            return new MonkeyBool(((Bool)node).value);
        } else if (node instanceof IntegerLiteral) {
            return new MonkeyInt(((IntegerLiteral)(node)).value);
        }

        return null;
    }
}
