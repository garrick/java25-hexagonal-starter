package org.commandline;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestMain {

    @Test
    void testHelloWorld() {
        Main unit = new Main();
        assertEquals("Hello, world!", unit.helloWorld());
    }
}
