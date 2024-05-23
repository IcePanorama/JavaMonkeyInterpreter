package monkeyobject;

import java.util.ArrayList;

import ast.BlockStatement;
import ast.Identifier;

public class MonkeyFunction implements MonkeyObject{
    public final static String FUNCTION_OBJ = "FUNCTION";
    public ArrayList<Identifier> parameters;
    public BlockStatement body;
    Environment env;

    public MonkeyFunction(ArrayList<Identifier> parameters, BlockStatement body,
        Environment env) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
    }

    public String Type() { return FUNCTION_OBJ; }

    public String Inspect() {
        String[] params = new String[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            params[i] = parameters.get(i).toString();
        }

        String output = "fn (";
        output += String.join(", ", params);
        return output + String.format(") {\n%s\n}", body.toString());
    }
}
