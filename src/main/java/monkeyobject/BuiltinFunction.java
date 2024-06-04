package monkeyobject;

import java.util.function.Function;

public class BuiltinFunction implements MonkeyObject{
    public static final String BUILTIN_OBJ = "BUILTIN";

    public Function<MonkeyObject[], MonkeyObject> function;

    public BuiltinFunction(Function<MonkeyObject[], MonkeyObject> fn) {
        this.function= fn;
    }

    public String Type() { return BUILTIN_OBJ; }

    public String Inspect() { return "builtin function"; }
}
