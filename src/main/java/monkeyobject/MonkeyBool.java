package monkeyobject;

public class MonkeyBool implements MonkeyObject {
    public boolean value;
    final static String BOOLEAN_OBJ = "BOOLEAN";

    public MonkeyBool (boolean value) {
        this.value = value;
    }

    public String Type() { return BOOLEAN_OBJ; }

    public String Inspect() { return Boolean.toString(value); }
}
