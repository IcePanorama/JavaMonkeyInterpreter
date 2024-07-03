package evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import ast.ArrayLiteral;
import ast.BlockStatement;
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
import ast.Node;
import ast.PrefixExpression;
import ast.Program;
import ast.ReturnStatement;
import ast.Statement;
import ast.StringLiteral;
import monkeyobject.BuiltinFunction;
import monkeyobject.Environment;
import monkeyobject.HashKey;
import monkeyobject.HashPair;
import monkeyobject.Hashable;
import monkeyobject.MonkeyArray;
import monkeyobject.MonkeyBool;
import monkeyobject.MonkeyError;
import monkeyobject.MonkeyFunction;
import monkeyobject.MonkeyHash;
import monkeyobject.MonkeyInt;
import monkeyobject.MonkeyNull;
import monkeyobject.MonkeyObject;
import monkeyobject.MonkeyReturnValue;
import monkeyobject.MonkeyString;

public final class Evaluator {
    private final static MonkeyBool TRUE = new MonkeyBool(true);
    private final static MonkeyBool FALSE = new MonkeyBool(false);
    private final static MonkeyNull NULL = new MonkeyNull();

    /* Error Message Format Strings */
    private final static String IDENTIFIER_NOT_FOUND_ERR_FMT =
        "identifier not found: %s";
    private final static String NOT_A_FUNCTION_ERR_FMT = "not a function: %s";
    private final static String TYPE_MISMATCH_ERR_FMT =
        "type mismatch: %s %s %s";
    private final static String UNKNOWN_OPERATOR_PREFIX_ERR_FMT = 
        "unknown operator: %s%s";
    private final static String UNKNOWN_OPERATOR_MINUS_ERR_FMT =
        "unknown operator: -%s";
    private final static String UNKNOWN_OPERATOR_INFIX_ERR_FMT =
        "unknown operator: %s %s %s";
    private final static String WRONG_NUM_ARGUMENTS_ERR_FMT =
        "wrong number of arguments: got=%d, want=%d";
    private final static String ARGUMENT_TO_FUNC_NOT_SUPPORTED_ERR_FMT =
        "argument to '%s' not supported, got %s";
    private final static String INDEX_OPERATOR_NOT_SUPPORTED_ERR_FMT =
        "index operator not supported: %s";
    private final static String UNUSABLE_AS_HASH_OBJ_ERR_FMT = "unusable as hash key: %s";

    /* Builtin Functions */
    private static Function<MonkeyObject[], MonkeyObject> BUILTIN_LEN = 
        (args) -> {
            if (args.length != 1) {
                return createNewError(WRONG_NUM_ARGUMENTS_ERR_FMT, args.length, 1);
            } else if (args[0] instanceof MonkeyArray) {
                return new MonkeyInt(((MonkeyArray)args[0]).elements.length);
            } else if (args[0] instanceof MonkeyString) {
                return new MonkeyInt((((MonkeyString)args[0]).value).length());
            }
            return createNewError(ARGUMENT_TO_FUNC_NOT_SUPPORTED_ERR_FMT,
                                "len", ((MonkeyObject)args[0]).Type());
        };
    private static Function<MonkeyObject[], MonkeyObject> BUILTIN_FIRST = 
        (args) -> {
            if (args.length != 1) {
                return createNewError(WRONG_NUM_ARGUMENTS_ERR_FMT, args.length, 1);
            } else if (args[0].Type() != MonkeyArray.ARRAY_OBJ) {
                return createNewError(ARGUMENT_TO_FUNC_NOT_SUPPORTED_ERR_FMT,
                                    "first", args[0].Type());
            }
            MonkeyArray arr = ((MonkeyArray)args[0]);
            if (arr.elements.length > 0) {
                return arr.elements[0];
            }
            return NULL;
        };
    private static Function<MonkeyObject[], MonkeyObject> BUILTIN_LAST = 
        (args) -> {
            if (args.length != 1) {
                return createNewError(WRONG_NUM_ARGUMENTS_ERR_FMT, args.length, 1);
            } else if (args[0].Type() != MonkeyArray.ARRAY_OBJ) {
                return createNewError(ARGUMENT_TO_FUNC_NOT_SUPPORTED_ERR_FMT,
                                    "last", args[0].Type());
            }
            MonkeyArray arr = ((MonkeyArray)args[0]);
            if (arr.elements.length > 0) {
                return arr.elements[arr.elements.length - 1];
            }
            return NULL;
        };
    private static Function<MonkeyObject[], MonkeyObject> BUILTIN_REST = 
        (args) -> {
            if (args.length != 1) {
                return createNewError(WRONG_NUM_ARGUMENTS_ERR_FMT, args.length, 1);
            } else if (args[0].Type() != MonkeyArray.ARRAY_OBJ) {
                return createNewError(ARGUMENT_TO_FUNC_NOT_SUPPORTED_ERR_FMT,
                                    "rest", args[0].Type());
            }
            MonkeyArray arr = ((MonkeyArray)args[0]);
            int len = arr.elements.length;

            if (arr.elements.length > 0) {
                MonkeyObject[] newElements = new MonkeyObject[len - 1];
                for (int i = 0; i < newElements.length; i++) {
                    newElements[i] = arr.elements[i + 1];
                }
                return new MonkeyArray(newElements);
            }
            return NULL;
        };
    private static Function<MonkeyObject[], MonkeyObject> BUILTIN_PUSH = 
        (args) -> {
            if (args.length != 2) {
                return createNewError(WRONG_NUM_ARGUMENTS_ERR_FMT, args.length, 2);
            } else if (args[0].Type() != MonkeyArray.ARRAY_OBJ) {
                return createNewError(ARGUMENT_TO_FUNC_NOT_SUPPORTED_ERR_FMT,
                                    "push", args[0].Type());
            }
            MonkeyArray arr = ((MonkeyArray)args[0]);
            int len = arr.elements.length;

            MonkeyObject[] newElements = new MonkeyObject[len + 1];
            for (int i = 0; i < arr.elements.length; i++) {
                newElements[i] = arr.elements[i];
            }
            newElements[len] = args[1];
            return new MonkeyArray(newElements);
        };
    private static Function<MonkeyObject[], MonkeyObject> BUILTIN_PUTS = 
        (args) -> {
            for (var arg: args) {
                System.out.println(arg.Inspect());
            }
            return NULL;
        };
    private static HashMap<String,BuiltinFunction> BUILTIN_FUNCTIONS = new HashMap<>() {{
        put("len", new BuiltinFunction(BUILTIN_LEN));
        put("first", new BuiltinFunction(BUILTIN_FIRST));
        put("last", new BuiltinFunction(BUILTIN_LAST));
        put("rest", new BuiltinFunction(BUILTIN_REST));
        put("push", new BuiltinFunction(BUILTIN_PUSH));
        put("puts", new BuiltinFunction(BUILTIN_PUTS));
    }};

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

    private static MonkeyObject evalStringInfixExpression(String operator, MonkeyObject left, MonkeyObject right) {
        if (!operator.equals("+")) {
            return createNewError(UNKNOWN_OPERATOR_INFIX_ERR_FMT, left.Type(), operator, right.Type());
        }
        String leftVal = ((MonkeyString)left).value;
        String rightVal = ((MonkeyString)right).value;
        return new MonkeyString(leftVal + rightVal);
    }

    private static MonkeyObject evalInfixExpression(String operator,
        MonkeyObject left, MonkeyObject right){
        if (
            left.Type() == MonkeyInt.INTEGER_OBJ
            && right.Type() == MonkeyInt.INTEGER_OBJ
        ) {
            return evalIntegerInfixExpression(operator, left, right);
        } else if (operator.equals("==")) {
            return nativeBooleanToBoolObject(left.equals(right));
        } else if (operator.equals("!=")) {
            return nativeBooleanToBoolObject(!left.equals(right));
        } else if (left.Type() != right.Type()) {
            return createNewError(TYPE_MISMATCH_ERR_FMT, left.Type(),
                operator, right.Type());
        } else if (
            left.Type() == MonkeyString.STRING_OBJ
            && right.Type() == MonkeyString.STRING_OBJ
        ) {
            return evalStringInfixExpression(operator, left, right);
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
        if (val != null)
            return val;
        
        BuiltinFunction builtin = BUILTIN_FUNCTIONS.get(node.value);
        if (builtin != null)
            return builtin;

        return createNewError(IDENTIFIER_NOT_FOUND_ERR_FMT,
                              node.value.toString());
    }

    private static MonkeyObject[] evalExpressions(Expression[] exprs,
        Environment env) {
        MonkeyObject[] results = new MonkeyObject[exprs.length];

        for (int i = 0; i < exprs.length; i++) {
            var e = exprs[i];
            MonkeyObject evaluated = Eval((Node)e, env);
            if (isError(evaluated)) {
                return new MonkeyObject[] {evaluated};
            }
            results[i] = evaluated;
        }

        return results;
    }

    private static Environment extendFunctionEnv(MonkeyFunction fn,
        MonkeyObject[] args) {
        Environment env = new Environment(fn.env);

        for (int i = 0; i < fn.parameters.size(); i++) {
            env.Set(fn.parameters.get(i).value, args[i]);
        }

        return env;
    }

    private static MonkeyObject unwrapReturnValue(MonkeyObject obj) {
        MonkeyReturnValue returnValue; 
        try {
            returnValue = (MonkeyReturnValue)obj;
        } catch (ClassCastException e) {
            // Should we print the exception w/ System.err.print() here?
            //System.err.println(e);
            return obj; 
        }

        return returnValue;
    }

    private static MonkeyObject applyFunction(MonkeyObject fn,
        MonkeyObject[] args) {
        if (fn instanceof MonkeyFunction) {
            MonkeyFunction function = (MonkeyFunction)fn;
            Environment extendedEnv = extendFunctionEnv(function, args);
            MonkeyObject evaluated = Eval(function.body, extendedEnv);
            return unwrapReturnValue(evaluated);
        } else if (fn instanceof BuiltinFunction) {
            return (MonkeyObject)((BuiltinFunction)fn).function.apply(args);
        }

        return createNewError(NOT_A_FUNCTION_ERR_FMT, fn.Type());
    }
    
    private static MonkeyObject evalArrayIndexExpression(MonkeyObject left,
                                                        MonkeyObject index) {
        MonkeyArray arrObj = (MonkeyArray)left;
        long idx = ((MonkeyInt)index).value;
        long max = arrObj.elements.length - 1;

        if (idx < 0 || idx > max) {
            return NULL;
        }
        return arrObj.elements[(int) idx];
    }

    private static MonkeyObject evalHashIndexExpression(MonkeyObject hash,
                                                        MonkeyObject index) {
        MonkeyHash hashObject = (MonkeyHash)hash;

        if (!(index instanceof Hashable)) {
            return createNewError(UNUSABLE_AS_HASH_OBJ_ERR_FMT, index.Type());
        }
        Hashable key = (Hashable)index;

        System.out.println(hashObject.pairs);
        System.out.println(key);

        HashPair pair = hashObject.pairs.get(key.getHashKey());
        if (pair == null) {
            return NULL;
        }

        return pair.value;
    }
    
    private static MonkeyObject evalIndexExpression(MonkeyObject left,
                                                    MonkeyObject index) {
        if (left.Type() == MonkeyArray.ARRAY_OBJ && index.Type() == MonkeyInt.INTEGER_OBJ) {
            return evalArrayIndexExpression(left, index);
        } else if (left.Type() == MonkeyHash.HASH_OBJ) {
            return evalHashIndexExpression(left, index);
        }
        return createNewError(INDEX_OPERATOR_NOT_SUPPORTED_ERR_FMT, left.Type());
    }

    private static MonkeyObject evalHashLiteral(HashLiteral node, Environment env) {
        HashMap<HashKey, HashPair> pairs = new HashMap<>();

        for (var keyNode: node.pairs.keySet().toArray()) {
            MonkeyObject key = Eval((Expression)keyNode, env);
            if (isError(key)) {
                return key;
            }

            if (!(key instanceof Hashable)) {
                return createNewError(UNUSABLE_AS_HASH_OBJ_ERR_FMT, key.Type());
            }
            Hashable hashKey = (Hashable)key;

            MonkeyObject value = Eval(node.pairs.get(keyNode), env);
            if (isError(value)) {
                return value;
            }

            HashKey hashed = hashKey.getHashKey();
            pairs.put(hashed, new HashPair(key, value));
        }

        return new MonkeyHash(pairs);
    }

    public static MonkeyObject Eval(Node node, Environment env) {
        /* Program */
        if (node instanceof Program) {
            return evalProgram(((Program)node).statements, env);
        }
        /* Statements */
        else if (node instanceof BlockStatement) {
            return evalBlockStatement(((BlockStatement)node), env);
        } else if (node instanceof ExpressionStatement) {
            return Eval(((ExpressionStatement)node).expression, env);
        } else if (node instanceof LetStatement) {
            MonkeyObject val = Eval(((LetStatement)node).value, env);
            if (isError(val))
                return val;

            env.Set(((LetStatement)node).name.value, val);
        } else if (node instanceof ReturnStatement) {
            MonkeyObject val = Eval(((ReturnStatement)node).returnValue, env);
            if (isError(val)) {
                return val;
            }

            return new MonkeyReturnValue(val);
        }
        /* Expressions */
        else if (node instanceof CallExpression) {
            MonkeyObject function = Eval(((CallExpression)node).function, env);
            if (isError(function)) {
                return function;
            }
            
            MonkeyObject[] args = evalExpressions(((CallExpression)node).arguments, env);
            if (args.length == 1 && isError(args[0])) {
                return args[0];
            }

            return applyFunction(function, args);
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
        } else if (node instanceof IndexExpression) {
            MonkeyObject left = Eval(((IndexExpression)node).left, env);
            if (isError(left)) {
                return left;
            }

            MonkeyObject index = Eval(((IndexExpression)node).index, env);
            if (isError(index)) {
                return index;
            }

            return evalIndexExpression(left, index);
        } else if (node instanceof PrefixExpression) {
            MonkeyObject right = Eval(((PrefixExpression)node).right, env);
            if (isError(right)) {
                return right;
            }

            return evalPrefixExpression(((PrefixExpression)node).operator,
                                        right);
        }
        /* Literals/Others */
        else if (node instanceof ArrayLiteral) {
            MonkeyObject[] elements = evalExpressions(((ArrayLiteral)node).elements, env);
            if (elements.length == 1 && isError(elements[0])) {
                return elements[0];
            }
            return new MonkeyArray(elements);
        } else if (node instanceof Bool) {
            return nativeBooleanToBoolObject(((Bool)node).value);
        } else if (node instanceof FunctionLiteral) {
            ArrayList<Identifier> params = ((FunctionLiteral)node).parameters;
            BlockStatement body = ((FunctionLiteral)node).body;
            return new MonkeyFunction(params, body, env);
        } else if (node instanceof Identifier) {
            return evalIdentifier((Identifier)node, env);
        } else if (node instanceof IntegerLiteral) {
            return new MonkeyInt(((IntegerLiteral)(node)).value);
        } else if (node instanceof HashLiteral) {
            return evalHashLiteral((HashLiteral)node, env);
        } else if (node instanceof StringLiteral) {
            return new MonkeyString(((StringLiteral)node).value);
        }

        return null;
    }
}
