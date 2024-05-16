package monkeyobject;

public class MonkeyNull implements MonkeyObject {
    final static String NULL_OBJ = "NULL";

    public String Type() { return NULL_OBJ; }

    public String Inspect() { return "null"; }
}
