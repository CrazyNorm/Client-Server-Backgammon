package com.example.dominobackgammonclient.game.dominoes;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DominoTest {

    @Test
    public void testInitialState() {
        Domino d = new Domino(1, 1);
        assertTrue(d.isAvailable());
    }

    @Test
    public void testSelect() {
        Domino d = new Domino(1, 1);
        d.select();
        assertTrue(d.isSelected());
    }

    @Test
    public void testUse() {
        Domino d = new Domino(1, 1);
        d.use();
        assertTrue(d.isUsed());
    }

    @Test
    public void testDouble() {
        Domino d = new Domino(1, 1);
        assertTrue(d.isDouble());
    }

    @Test
    public void testNonDouble() {
        Domino d = new Domino(1, 2);
        assertFalse(d.isDouble());
    }
}
