package com.example.dominobackgammonclient.game.board;

import com.example.dominobackgammonclient.game.common.Player;
import org.junit.Test;

import static org.junit.Assert.*;

public class PointTest {

    @Test
    public void testClosedConstructor() {
        Point testPoint = new Point(3, Player.Client);
        assertTrue(testPoint.isClosed());
    }

    @Test
    public void testBlotConstructor() {
        Point testPoint = new Point(1, Player.Client);
        assertTrue(testPoint.isBlot());
    }

    @Test
    public void testOpenConstructor() {
        Point testPoint = new Point(0, null);
        assertTrue(testPoint.isOpen());
    }

    @Test
    public void testDefaultConstructor() {
        Point testPoint = new Point();

        assertEquals(testPoint.getCount(), 0);
        assertNull(testPoint.getPlayer());
        assertTrue(testPoint.isOpen());
    }

}