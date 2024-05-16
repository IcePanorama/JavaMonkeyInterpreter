package monkeyobject;

class MonkeyBool implements MonkeyObject {
    boolean value;
    final static String BOOLEAN_OBJ = "BOOLEAN";

    public String Type() { return BOOLEAN_OBJ; }

    public String Inspect() { return Boolean.toString(value); }
}
