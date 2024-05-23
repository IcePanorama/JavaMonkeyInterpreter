package monkeyobject;

import java.util.HashMap;

public class Environment {
    HashMap<String, MonkeyObject> store = new HashMap<>();
    Environment outer;

    public Environment() { }
    
    public Environment(Environment outer) {
        this.outer = outer;
    }


    public MonkeyObject Get(String name) {
        MonkeyObject obj = store.get(name);
        if (obj == null && outer != null) {
            obj = outer.Get(name);
        }

        return obj;
    }

    public MonkeyObject Set(String name, MonkeyObject val) {
        store.put(name, val);
        return val;
    }
}
