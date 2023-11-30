package com.example.dominobackgammonclient.game.dominoes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class HandTest {

    @Test
    public void testGetDominoesSet1() {
        Domino[] d = new Hand(1).getDominoes();

        // check all dominoes are from the right set
        for (int i = 0; i < d.length; i++) {
            assertEquals(DominoSet.SET_1()[i], d[i]);
        }

        assertEquals(d.length, 8);
    }

    @Test
    public void testGetDominoesSet2() {
        Domino[] d = new Hand(2).getDominoes();

        // check all dominoes are from the right set
        for (int i = 0; i < d.length; i++) {
            assertEquals(DominoSet.SET_2()[i], d[i]);
        }

        assertEquals(d.length, 8);
    }

    @Test
    public void testUseDomino() {
        Hand h = new Hand(1);
        h.useDomino(2, 1);
        assertTrue(h.getDominoes()[0].isUsed());
    }

    @Test
    public void testSwapSetTo1() {
        Hand h = new Hand(2);
        h.swapDominoSet();

        // check all dominoes are from the right set
        for (int i = 0; i < h.getDominoes().length; i++) {
            assertEquals(DominoSet.SET_1()[i], h.getDominoes()[i]);
        }
    }

    @Test
    public void testSwapSetTo2() {
        Hand h = new Hand(1);
        h.swapDominoSet();

        // check all dominoes are from the right set
        for (int i = 0; i < h.getDominoes().length; i++) {
            assertEquals(DominoSet.SET_2()[i], h.getDominoes()[i]);
        }
    }


    @Test
    public void testGetDoublesSet1() {
        Domino[] d = new Hand(1).getDoubles();

        // check all dominoes are from the right set
        for (int i = 0; i < d.length; i++) {
            assertEquals(DominoSet.DOUBLES_1()[i], d[i]);
        }

        assertEquals(d.length, 3);
    }

    @Test
    public void testGetDoublesSet2() {
        Domino[] d = new Hand(2).getDoubles();

        // check all dominoes are from the right set
        for (int i = 0; i < d.length; i++) {
            assertEquals(DominoSet.DOUBLES_2()[i], d[i]);
        }

        assertEquals(d.length, 3);
    }

    @Test
    public void testGetNextDouble() {
        Hand h = new Hand(1);
        assertEquals(h.getNextDouble(), new Domino(1, 1));
    }

    @Test
    public void testSwapDoubles() {
        Hand h = new Hand(1);
        h.swapDominoSet();

        // check all doubles are still from the starting set
        for (int i = 0; i < h.getDoubles().length; i++) {
            assertEquals(DominoSet.DOUBLES_1()[i], h.getDoubles()[i]);
        }
    }

    @Test
    public void testUseDouble() {
        // tests the next double changes correctly when a double is used
        Hand h = new Hand(1);
        h.useDouble(1);

        assertTrue(h.getDoubles()[0].isUsed());
        assertEquals(h.getNextDouble(), new Domino(3, 3));
    }
}
