package monkeyobject;

public class MonkeyString implements MonkeyObject{
    public static final String STRING_OBJ = "STRING";
    public String value;

    public MonkeyString(String value) {
        this.value = value;
    }

    public String Type() { return STRING_OBJ; }

    public String Inspect() { return value; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MonkeyString))
            return false;
        return value.equals(((MonkeyString)obj).value);
    }
}
