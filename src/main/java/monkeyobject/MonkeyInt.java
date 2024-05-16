package monkeyobject;

class MonkeyInt implements MonkeyObject {
    long value;
    final static String INTEGER_OBJ = "INTEGER";

    public String Type() { return INTEGER_OBJ; }

    public String Inspect() { 
        return Long.toString(value);
    }
}
