package monkeyobject;

public class MonkeyInt implements MonkeyObject {
    public long value;
    final static String INTEGER_OBJ = "INTEGER";

    public String Type() { return INTEGER_OBJ; }

    public String Inspect() { 
        return Long.toString(value);
    }
}
