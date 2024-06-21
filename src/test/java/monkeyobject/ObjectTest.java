package monkeyobject;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class ObjectTest {
    @Test
    void testStringHashKey() {
        MonkeyString hello1 = new MonkeyString("Hello World");
        MonkeyString hello2 = new MonkeyString("Hello World");
        MonkeyString diff1 = new MonkeyString("My name is johnny");
        MonkeyString diff2 = new MonkeyString("My name is johnny");

        assert(hello1.getHashKey().equals(hello2.getHashKey()));
        assert(diff1.getHashKey().equals(diff2.getHashKey()));
        assertFalse(hello1.getHashKey().equals(diff2.getHashKey()));
    }
}
