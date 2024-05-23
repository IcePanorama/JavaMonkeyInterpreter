package monkeyobject;

public class MonkeyError implements MonkeyObject{
    public String message;
    public final static String ERROR_OBJ = "ERROR";

    public String Type() { return ERROR_OBJ; }
    public String Inspect() { return "ERROR: " + message; }
}
