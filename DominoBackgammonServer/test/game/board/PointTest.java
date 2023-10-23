package game.board;

import game.common.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PointTest {

    @Test
    void testClosedConstructor() {
        Point testPoint = new Point(3, Player.White);
        assertTrue(testPoint.isClosed());
    }

    @Test
    void testBlotConstructor() {
        Point testPoint = new Point(1, Player.White);
        assertTrue(testPoint.isBlot());
    }

    @Test
    void testOpenConstructor() {
        Point testPoint = new Point(0, Player.White);
        assertTrue(testPoint.isOpen());
    }

    @Test
    void testDefaultConstructor() {
        Point testPoint = new Point();

        assertEquals(testPoint.getCount(), 0);
        assertEquals(testPoint.getPlayer(), Player.None);
        assertTrue(testPoint.isOpen());
    }
}