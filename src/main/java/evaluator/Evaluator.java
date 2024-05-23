package evaluator;

import java.util.ArrayList;

import ast.BlockStatement;
import ast.Bool;
import ast.ExpressionStatement;
import ast.Identifier;
import ast.IfExpression;
import ast.InfixExpression;
import ast.IntegerLiteral;
import ast.LetStatement;
import ast.Node;
import ast.PrefixExpression;
import ast.Program;
import ast.ReturnStatement;
import ast.Statement;
import monkeyobject.Environment;
import monkeyobject.MonkeyBool;
import monkeyobject.MonkeyError;
import monkeyobject.MonkeyInt;
import monkeyobject.MonkeyNull;
import monkeyobject.MonkeyObject;
import monkeyobject.MonkeyReturnValue;

public final class Evaluator {
    private final static MonkeyBool TRUE = new MonkeyBool(true);
    private final static MonkeyBool FALSE = new MonkeyBool(false);
    private final static MonkeyNull NULL = new MonkeyNull();
    private final static String UNKNOWN_OPERATOR_PREFIX_ERR_FMT = 
        "unknown operator: %s%s";
    private final static String UNKNOWN_OPERATOR_MINUS_ERR_FMT =
        "unknown operator: -%s";
    private final static String UNKNOWN_OPERATOR_INFIX_ERR_FMT =
        "unknown operator: %s %s %s";
    private final static String TYPE_MISMATCH_ERR_FMT =
        "type mismatch: %s %s %s";
    private final static String IDENTIFIER_NOT_FOUND_ERR_FMT =
        "identifier not found: %s";

    private Evaluator() {}

    private static MonkeyObject evalProgram(ArrayList<Statement> statements,
        Environment env) {
        MonkeyObject result = null;

        for (var stmt : statements) {
            result = Eval(stmt, env);

            if (result instanceof MonkeyReturnValue) {
                return ((MonkeyReturnValue)result).value;
            } else if (result instanceof MonkeyError) {
                return result;
            }
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

    private static MonkeyObject evalMinusOperatorExpression(MonkeyObject right){
        if (right.Type() != MonkeyInt.INTEGER_OBJ) {
            return createNewError(UNKNOWN_OPERATOR_MINUS_ERR_FMT, right.Type());
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
                return createNewError(UNKNOWN_OPERATOR_PREFIX_ERR_FMT, operator,
                    right.Type());
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
                return createNewError(UNKNOWN_OPERATOR_INFIX_ERR_FMT,
                    left.Type(), operator, right.Type());
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
        } else if (left.Type() != right.Type()) {
            return createNewError(TYPE_MISMATCH_ERR_FMT, left.Type(),
                operator, right.Type());
        }
        return createNewError(UNKNOWN_OPERATOR_INFIX_ERR_FMT, left.Type(),
            operator, right.Type());
    }

    private static boolean isTruthy(MonkeyObject obj) {
        if (obj == NULL || obj == FALSE) {
            return false;
        }
        return true;
    }

    private static MonkeyObject evalIfExpression(IfExpression expr,
        Environment env) {
        MonkeyObject condition = Eval(expr.condition, env);
        if (isError(condition))
            return condition;

        if (isTruthy(condition)){
            return Eval(expr.consequence, env);
        } else if (expr.alternative != null) {
            return Eval(expr.alternative, env);
        } else {
            return NULL;
        }
    }

    private static MonkeyObject evalBlockStatement(BlockStatement block,
        Environment env) {
        MonkeyObject result = null;

        for (var stmt: block.statements) {
            result = Eval(stmt, env);

            if (result != null) {
                String rt = result.Type();
                if (rt.equals(MonkeyReturnValue.RETURN_VALUE_OBJ) ||
                    rt.equals(MonkeyError.ERROR_OBJ)) {
                    return result;
                }
            }
        }

        return result;
    }

    private static MonkeyError createNewError(String format, Object... a) {
        return new MonkeyError(String.format(format, a));
    }

    private static boolean isError(MonkeyObject obj) {
        if (obj != null)
            return obj.Type().equals(MonkeyError.ERROR_OBJ);
        return false;
    }

    private static MonkeyObject evalIdentifier(Identifier node, Environment env) {
        MonkeyObject val = env.Get(node.value);
        if (val == null)
            return createNewError(IDENTIFIER_NOT_FOUND_ERR_FMT,
                node.value.toString());

        return val;
    }

    public static MonkeyObject Eval(Node node, Environment env) {
        if (node instanceof Program) {
            return evalProgram(((Program)node).statements, env);
        } else if (node instanceof ExpressionStatement) {
            return Eval(((ExpressionStatement)node).expression, env);
        } else if (node instanceof BlockStatement) {
            return evalBlockStatement(((BlockStatement)node), env);
        } else if (node instanceof Identifier) {
            return evalIdentifier((Identifier)node, env);
        } else if (node instanceof IfExpression) {
            return evalIfExpression((IfExpression)node, env);
        } else if (node instanceof InfixExpression) {
            MonkeyObject left = Eval(((InfixExpression)node).left, env);
            if (isError(left)) {
                return left;
            }

            MonkeyObject right = Eval(((InfixExpression)node).right, env);
            if (isError(right)) {
                return right;
            }

            return evalInfixExpression(((InfixExpression)node).operator, left,
                                        right);
        } else if (node instanceof LetStatement) {
            MonkeyObject val = Eval(((LetStatement)node).value, env);
            if (isError(val))
                return val;

            env.Set(((LetStatement)node).name.value, val);
        } else if (node instanceof PrefixExpression) {
            MonkeyObject right = Eval(((PrefixExpression)node).right, env);
            if (isError(right)) {
                return right;
            }

            return evalPrefixExpression(((PrefixExpression)node).operator,
                                        right);
        } else if (node instanceof ReturnStatement) {
            MonkeyObject val = Eval(((ReturnStatement)node).returnValue, env);
            if (isError(val)) {
                return val;
            }

            return new MonkeyReturnValue(val);
        } else if (node instanceof Bool) {
            return nativeBooleanToBoolObject(((Bool)node).value);
        } else if (node instanceof IntegerLiteral) {
            return new MonkeyInt(((IntegerLiteral)(node)).value);
        }

        return null;
    }
}
