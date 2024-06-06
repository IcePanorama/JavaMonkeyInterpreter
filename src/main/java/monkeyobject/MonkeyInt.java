package monkeyobject;

public class MonkeyInt implements MonkeyObject {
    public long value;
    public final static String INTEGER_OBJ = "INTEGER";

    public MonkeyInt(long value) {
        this.value = value;
    }

    public String Type() { return INTEGER_OBJ; }

    public String Inspect() { 
        return Long.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof MonkeyInt)) {
            return false;
        }

        return this.value == ((MonkeyInt)o).value;
    }
}
