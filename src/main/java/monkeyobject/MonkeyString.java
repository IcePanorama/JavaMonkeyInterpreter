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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof MonkeyString)) {
            return false;
        }
        return value.equals(((MonkeyString)o).value);
    }
}
