package monkeyobject;

import java.util.HashMap;

public class MonkeyHash implements MonkeyObject {
    public static String HASH_OBJ = "HASH";
    public HashMap<HashKey, HashPair> pairs;

    public String Type() { return HASH_OBJ; }

    public String Inspect() {
        String[] pairStrs = new String[pairs.size()];
        int i = 0;
        for (HashKey key : pairs.keySet()) {
            pairStrs[i] = String.format("%s: %s", pairs.get(key).key.Inspect(),
                pairs.get(key).value.Inspect());
            i++;
        }
        String output = "{";
        output += String.join(",", pairStrs);
        return output + "}";
    }
}
