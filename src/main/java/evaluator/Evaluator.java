package evaluator;

import java.util.ArrayList;

import ast.BlockStatement;
import ast.Bool;
import ast.ExpressionStatement;
import ast.IfExpression;
import ast.InfixExpression;
import ast.IntegerLiteral;
import ast.Node;
import ast.PrefixExpression;
import ast.Program;
import ast.Statement;
import monkeyobject.MonkeyBool;
import monkeyobject.MonkeyInt;
import monkeyobject.MonkeyNull;
import monkeyobject.MonkeyObject;

public final class Evaluator {
    private final static MonkeyBool TRUE = new MonkeyBool(true);
    private final static MonkeyBool FALSE = new MonkeyBool(false);
    private final static MonkeyNull NULL = new MonkeyNull();

    private Evaluator() {}

    private static MonkeyObject evalStatements(ArrayList<Statement> statements) {
        MonkeyObject result = null;

        for (var stmt : statements) {
            result = Eval(stmt);
        }

        return result;
    }

    private static MonkeyBool nativeBooleanToBoolObject(boolean input) {
        if (input)
            return TRUE;
        return FALSE;
    }

    private static MonkeyObject evalBangOperatorExpression(MonkeyObject right) {
        if (right == TRUE) {
            return FALSE;
        } else if (right == FALSE) {
            return TRUE;
        } else if (right == NULL) {
                return TRUE;
        }

        return FALSE;
    }

    private static MonkeyObject evalMinusOperatorExpression(MonkeyObject right) {
        if (right.Type() != MonkeyInt.INTEGER_OBJ) {
            return NULL;
        }
        
        return new MonkeyInt(-((MonkeyInt)right).value);
    }

    private static MonkeyObject evalPrefixExpression(String operator,
        MonkeyObject right) {
        switch (operator) {
            case "!": 
                return evalBangOperatorExpression(right);
            case "-":
                return evalMinusOperatorExpression(right);
            default:
                return NULL;
        }
    }

    private static MonkeyObject evalIntegerInfixExpression(String operator,
        MonkeyObject left, MonkeyObject right){
        long leftVal = ((MonkeyInt)left).value;
        long rightVal = ((MonkeyInt)right).value;

        switch (operator) {
            case "+":
                return new MonkeyInt(leftVal + rightVal);
            case "-":
                return new MonkeyInt(leftVal - rightVal);
            case "*":
                return new MonkeyInt(leftVal * rightVal);
            case "/":
                return new MonkeyInt(leftVal / rightVal);
            case "<":
                return nativeBooleanToBoolObject(leftVal < rightVal);
            case ">":
                return nativeBooleanToBoolObject(leftVal > rightVal);
            case "==":
                return nativeBooleanToBoolObject(leftVal == rightVal);
            case "!=":
                return nativeBooleanToBoolObject(leftVal != rightVal);
            default:
                return NULL;
        }
    }

    private static MonkeyObject evalInfixExpression(String operator,
        MonkeyObject left, MonkeyObject right){
        if (
            left.Type() == MonkeyInt.INTEGER_OBJ
            && right.Type() == MonkeyInt.INTEGER_OBJ
        ) {
            return evalIntegerInfixExpression(operator, left, right);
        } else if (operator.equals("==")) {
            return nativeBooleanToBoolObject(left == right);
        } else if (operator.equals("!=")) {
            return nativeBooleanToBoolObject(left != right);
        }
        return NULL;
    }

    private static boolean isTruthy(MonkeyObject obj) {
        if (obj == NULL || obj == FALSE) {
            return false;
        }
        return true;
    }

    private static MonkeyObject evalIfExpression(IfExpression expr) {
        MonkeyObject condition = Eval(expr.condition);

        if (isTruthy(condition)){
            return Eval(expr.consequence);
        } else if (expr.alternative != null) {
            return Eval(expr.alternative);
        } else {
            return NULL;
        }
    }

    public static MonkeyObject Eval(Node node) {
        if (node instanceof Program) {
            return evalStatements(((Program)node).statements);
        } else if (node instanceof ExpressionStatement) {
            return Eval(((ExpressionStatement)node).expression);
        } else if (node instanceof BlockStatement) {
            return evalStatements(((BlockStatement)node).statements);
        } else if (node instanceof IfExpression) {
            return evalIfExpression((IfExpression)node);
        } else if (node instanceof InfixExpression) {
            MonkeyObject left = Eval(((InfixExpression)node).left);
            MonkeyObject right = Eval(((InfixExpression)node).right);

            return evalInfixExpression(((InfixExpression)node).operator,
                                        left,
                                        right);
        } else if (node instanceof PrefixExpression) {
            MonkeyObject right = Eval(((PrefixExpression)node).right);

            return evalPrefixExpression(((PrefixExpression)node).operator,
                                        right);
        } else if (node instanceof Bool) {
            return nativeBooleanToBoolObject(((Bool)node).value);
        } else if (node instanceof IntegerLiteral) {
            return new MonkeyInt(((IntegerLiteral)(node)).value);
        }

        return null;
    }
}
