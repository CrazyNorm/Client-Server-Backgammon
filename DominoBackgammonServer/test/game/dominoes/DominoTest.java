package game.dominoes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DominoTest {

    @Test
    void testInitialState() {
        Domino d = new Domino(1, 1);
        assertTrue(d.isAvailable());
    }

    @Test
    void testUse() {
        Domino d = new Domino(1, 1);
        d.use();
        assertTrue(d.isUsed());
    }

    @Test
    void testDouble() {
        Domino d = new Domino(1, 1);
        assertTrue(d.isDouble());
    }

    @Test
    void testNonDouble() {
        Domino d = new Domino(1, 2);
        assertFalse(d.isDouble());
    }
}