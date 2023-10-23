package game.board;

import game.common.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class PointTest {

    @Test
    void testClosedConstructor() {
        Point testPoint = new Point(3, Player.White);
        assertEquals(testPoint.isClosed(), true);
    }

    @Test
    void testBlotConstructor() {
        Point testPoint = new Point(1, Player.White);
        assertEquals(testPoint.isBlot(), true);
    }

    @Test
    void testOpenConstructor() {
        Point testPoint = new Point(0, Player.White);
        assertEquals(testPoint.isOpen(), true);
    }

    @Test
    void testDefaultConstructor() {
        Point testPoint = new Point();

        assertEquals(testPoint.getCount(), 0);
        assertEquals(testPoint.getPlayer(), Player.None);
        assertEquals(testPoint.isOpen(), true);
    }
}