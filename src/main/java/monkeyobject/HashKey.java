package monkeyobject;

/*
    Manually creating a hashkey is likely
    unneccesary in Java, but I'm doing
    this just to follow along with what was
    done in the book
*/
public class HashKey {
    String type;
    long value;

    public HashKey(String type, long value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof HashKey)) {
            return false;
        }

        return type == ((HashKey)o).type 
            && value == ((HashKey)o).value;
    }

    @Override
    public int hashCode() {
        return (int)value;
    }
}
