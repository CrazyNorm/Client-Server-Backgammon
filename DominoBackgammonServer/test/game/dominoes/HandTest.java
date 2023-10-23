package game.dominoes;

import game.common.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HandTest {

    @Test
    void testInitialSet1(){
        // tests correct dominoes when initialised as set 1
        Hand h = new Hand(Player.White, 1);

        assertTrue(h.hasDomino(2, 1));
        assertTrue(h.hasDomino(3, 1));
        assertTrue(h.hasDomino(4, 2));
        assertTrue(h.hasDomino(5, 1));
        assertTrue(h.hasDomino(5, 3));
        assertTrue(h.hasDomino(5, 4));
        assertTrue(h.hasDomino(6, 2));
        assertTrue(h.hasDomino(6, 4));

        assertTrue(h.hasDouble(1));
        assertTrue(h.hasDouble(3));
        assertTrue(h.hasDouble(6));

        assertEquals(h.getRemaining(), 8);
        assertEquals(h.getPlayer(), Player.White);
    }

    @Test
    void testInitialSet2(){
        // tests correct dominoes when initialised as set 2
        Hand h = new Hand(Player.Black, 2);

        assertTrue(h.hasDomino(3, 2));
        assertTrue(h.hasDomino(4, 1));
        assertTrue(h.hasDomino(4, 3));
        assertTrue(h.hasDomino(5, 2));
        assertTrue(h.hasDomino(6, 1));
        assertTrue(h.hasDomino(6, 3));
        assertTrue(h.hasDomino(6, 5));

        assertTrue(h.hasDouble(2));
        assertTrue(h.hasDouble(4));
        assertTrue(h.hasDouble(5));

        assertEquals(h.getRemaining(), 7);
        assertEquals(h.getPlayer(), Player.Black);
    }


    @Test
    void testHasDomino() {
        // tests presence when the hand does contain a given domino
        Hand h = new Hand(Player.White, 1);
        assertTrue(h.hasDomino(2, 1));
    }

    @Test
    void testHasNoDomino() {
        // tests presence when the hand does not contain a given domino
        Hand h = new Hand(Player.White, 1);
        assertFalse(h.hasDomino(3, 2));
    }

    @Test
    void testIsDominoAvailable() {
        // tests availability when the given domino is not used
        Hand h = new Hand(Player.White, 1);
        assertTrue(h.isDominoAvailable(2, 1));
    }

    @Test
    void testIsDominoNotAvailable() {
        // tests availability when the given domino is used
        Hand h = new Hand(Player.White, 1);
        h.useDomino(2, 1);
        assertFalse(h.isDominoAvailable(2, 1));
    }

    @Test
    void useDominoRemainingCount() {
        // tests the remaining count decreases correctly when a domino is used
        Hand h = new Hand(Player.White, 1);
        h.useDomino(2, 1);
        assertEquals(h.getRemaining(), 7);
    }

    @Test
    void swapDominoSetTo1() {
        // tests has the correct dominoes after swapping from set 2 to 1
        Hand h = new Hand(Player.White, 2);
        h.swapDominoSet();

        assertTrue(h.hasDomino(2, 1));
        assertTrue(h.hasDomino(3, 1));
        assertTrue(h.hasDomino(4, 2));
        assertTrue(h.hasDomino(5, 1));
        assertTrue(h.hasDomino(5, 3));
        assertTrue(h.hasDomino(5, 4));
        assertTrue(h.hasDomino(6, 2));
        assertTrue(h.hasDomino(6, 4));

        // check doubles don't change
        assertTrue(h.hasDouble(2));
        assertTrue(h.hasDouble(4));
        assertTrue(h.hasDouble(5));

        assertEquals(h.getRemaining(), 8);
    }

    @Test
    void swapDominoSetTo2() {
        // tests has the correct dominoes after swapping from set 1 to 2
        Hand h = new Hand(Player.White, 1);
        h.swapDominoSet();

        assertTrue(h.hasDomino(3, 2));
        assertTrue(h.hasDomino(4, 1));
        assertTrue(h.hasDomino(4, 3));
        assertTrue(h.hasDomino(5, 2));
        assertTrue(h.hasDomino(6, 1));
        assertTrue(h.hasDomino(6, 3));
        assertTrue(h.hasDomino(6, 5));

        // check doubles don't change
        assertTrue(h.hasDouble(1));
        assertTrue(h.hasDouble(3));
        assertTrue(h.hasDouble(6));

        assertEquals(h.getRemaining(), 7);
    }


    @Test
    void testHasDouble() {
        // tests presence when the hand does contain a given double
        Hand h = new Hand(Player.White, 1);
        assertTrue(h.hasDouble(1));
    }

    @Test
    void testHasNoDouble() {
        // tests presence when the hand does not contain a given double
        Hand h = new Hand(Player.White, 1);
        assertFalse(h.hasDouble(2));
    }

    @Test
    void testIsDoubleAvailable() {
        // tests availability when the given double is not used
        Hand h = new Hand(Player.White, 1);
        assertTrue(h.isDoubleAvailable(1));
    }

    @Test
    void testIsDoubleNotAvailable() {
        // tests availability when the given double is used
        Hand h = new Hand(Player.White, 1);
        h.useDouble(1);
        assertFalse(h.isDoubleAvailable(1));
    }

    @Test
    void testIsNextDouble() {
        // tests presence when the hand does contain a given double
        Hand h = new Hand(Player.White, 1);
        assertTrue(h.isNextDouble(1));
    }

    @Test
    void testIsNotNextDouble() {
        // tests presence when the hand does not contain a given double
        Hand h = new Hand(Player.White, 1);
        assertFalse(h.isNextDouble(3));
    }

    @Test
    void useDoubleNextDouble() {
        // tests the next double changes correctly when a double is used
        Hand h = new Hand(Player.White, 1);
        h.useDouble(1);
        assertTrue(h.isNextDouble(3));
    }
}