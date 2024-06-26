package monkeyobject;

public class MonkeyArray implements MonkeyObject{
    public static String ARRAY_OBJ = "ARRAY";
    public MonkeyObject[] elements;

    public MonkeyArray(MonkeyObject[] elements) {
        this.elements = elements;
    }

    public String Type() { return ARRAY_OBJ; }

    public String Inspect() {
        String[] strEl = new String[elements.length];
        for (int i = 0; i < elements.length; i++) {
            strEl[i] = elements[i].Inspect();
        }

        return "[" + String.join(", ", strEl) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MonkeyArray)) {
            return false;
        } 
        MonkeyArray arr = (MonkeyArray)o;

        if (elements.length != arr.elements.length) {
            return false;
        }

        for (int i = 0; i < elements.length; i++) {
            if (!elements[i].equals(arr.elements[i])) {
                return false;
            }
        }
        return true;
    }
}
