package monkeyobject;

import java.util.HashMap;

public class Environment {
    HashMap<String, MonkeyObject> store = new HashMap<>();

    public MonkeyObject Get(String name) {
//TODO: look into how we're handling non-existant identifiers
        return store.get(name);
    }

    public MonkeyObject Set(String name, MonkeyObject val) {
        store.put(name, val);
        return val;
    }
}
