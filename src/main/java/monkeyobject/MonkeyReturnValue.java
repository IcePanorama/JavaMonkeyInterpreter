package monkeyobject;

public class MonkeyReturnValue implements MonkeyObject{
   public MonkeyObject value; 
   public final static String RETURN_VALUE_OBJ = "RETURN_VALUE";

   public MonkeyReturnValue(MonkeyObject value) {
    this.value = value;
   }

   public String Type() { return RETURN_VALUE_OBJ; }
   public String Inspect() { return value.Inspect(); }
}
