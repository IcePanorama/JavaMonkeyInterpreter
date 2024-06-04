package monkeyobject;

public class MonkeyString implements MonkeyObject{
    public static final String STRING_OBJ = "STRING";
    public String value;

    public String Type() { return STRING_OBJ; }

    public String Inspect() { return value; }
}
