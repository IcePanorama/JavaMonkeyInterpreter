package monkeyobject;

public class MonkeyInt implements MonkeyObject {
    public long value;
    final static String INTEGER_OBJ = "INTEGER";

    public MonkeyInt(long value) {
        this.value = value;
    }

    public String Type() { return INTEGER_OBJ; }

    public String Inspect() { 
        return Long.toString(value);
    }
}
