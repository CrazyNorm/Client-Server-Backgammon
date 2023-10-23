package game.board;

import game.common.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class BoardTest {

    private Board mockBoard;

    @BeforeEach
    void setupBoard() {
        // re-initialises the board for each test
        // Board is just a data structure, so moves don't have to be valid
        mockBoard = new Board();
    }


    @Test
    void testMoveStartClosed() {
        // tests moving pieces when the start point is still closed afterward
        mockBoard.movePiece(12,14, Player.Black);
        Point p = mockBoard.getPoint(12);

        assertEquals(p.getCount(), 4);
        assertTrue(p.isClosed());
        assertEquals(p.getPlayer(), Player.Black);
    }

    @Test
    void testMoveStartBlot() {
        // tests moving pieces when the start point becomes a blot
        mockBoard.movePiece(1,2, Player.Black);
        Point p = mockBoard.getPoint(1);

        assertEquals(p.getCount(), 1);
        assertTrue(p.isBlot());
        assertEquals(p.getPlayer(), Player.Black);
    }

    @Test
    void testMoveStartOpen() {
        // tests moving pieces when the start point becomes open
        mockBoard.movePiece(1,2, Player.Black);
        mockBoard.movePiece(1,2, Player.Black);
        Point p = mockBoard.getPoint(1);

        assertEquals(p.getCount(), 0);
        assertTrue(p.isOpen());
        assertEquals(p.getPlayer(), Player.None);
    }

    @Test
    void testMoveEndBlot() {
        // tests moving pieces when the end point becomes a blot
        mockBoard.movePiece(1,2, Player.Black);
        Point p = mockBoard.getPoint(2);

        assertEquals(p.getCount(), 1);
        assertTrue(p.isBlot());
        assertEquals(p.getPlayer(), Player.Black);
    }

    @Test
    void testMoveEndClosed() {
        // tests moving pieces when the start point becomes closed
        mockBoard.movePiece(1,2, Player.Black);
        mockBoard.movePiece(1,2, Player.Black);
        Point p = mockBoard.getPoint(2);

        assertEquals(p.getCount(), 2);
        assertTrue(p.isClosed());
        assertEquals(p.getPlayer(), Player.Black);
    }


    @Test
    void testBearOffStartClosed() {
        // tests bearing off when the start point is still closed afterward
        mockBoard.bearOffPiece(6, Player.White);
        Point p = mockBoard.getPoint(6);

        assertEquals(p.getCount(), 4);
        assertTrue(p.isClosed());
        assertEquals(p.getPlayer(), Player.White);
    }

    @Test
    void testBearOffStartBlot() {
        // tests bearing off when the start point becomes a blot
        mockBoard.bearOffPiece(6, Player.White);
        mockBoard.bearOffPiece(6, Player.White);
        mockBoard.bearOffPiece(6, Player.White);
        mockBoard.bearOffPiece(6, Player.White);
        Point p = mockBoard.getPoint(6);

        assertEquals(p.getCount(), 1);
        assertTrue(p.isBlot());
        assertEquals(p.getPlayer(), Player.White);
    }

    @Test
    void testBearOffStartOpen() {
        // tests bearing off when the start point becomes open
        mockBoard.bearOffPiece(6, Player.White);
        mockBoard.bearOffPiece(6, Player.White);
        mockBoard.bearOffPiece(6, Player.White);
        mockBoard.bearOffPiece(6, Player.White);
        mockBoard.bearOffPiece(6, Player.White);
        Point p = mockBoard.getPoint(6);

        assertEquals(p.getCount(), 0);
        assertTrue(p.isOpen());
        assertEquals(p.getPlayer(), Player.None);
    }

    @Test
    void testBearOffOffBoardCount() {
        // tests count for borne off pieces changes correctly
        mockBoard.bearOffPiece(6, Player.White);
        mockBoard.bearOffPiece(6, Player.White);

        assertEquals(mockBoard.getOffCount(Player.White), 2);
        assertEquals(mockBoard.getOffCount(Player.Black), 0);
    }


    @Test
    void testEnterEndBlot() {
        // tests entering off the bar when the end point becomes a blot
        mockBoard.hitPiece(1, Player.Black);
        mockBoard.enterPiece(2, Player.Black);
        Point p = mockBoard.getPoint(2);

        assertEquals(p.getCount(), 1);
        assertTrue(p.isBlot());
        assertEquals(p.getPlayer(), Player.Black);
    }

    @Test
    void testEnterEndClosed() {
        // tests entering off the bar when the end point becomes closed
        mockBoard.hitPiece(1, Player.Black);
        mockBoard.hitPiece(1, Player.Black);
        mockBoard.enterPiece(2, Player.Black);
        mockBoard.enterPiece(2, Player.Black);
        Point p = mockBoard.getPoint(2);

        assertEquals(p.getCount(), 2);
        assertTrue(p.isClosed());
        assertEquals(p.getPlayer(), Player.Black);
    }

    @Test
    void testEnterBarCount() {
        // tests bar count changes correctly
        mockBoard.hitPiece(1, Player.Black);
        mockBoard.hitPiece(1, Player.Black);
        mockBoard.enterPiece(2, Player.Black);

        assertEquals(mockBoard.getBarCount(Player.White), 0);
        assertEquals(mockBoard.getBarCount(Player.Black), 1);
    }


    @Test
    void testHitStartOpen() {
        // tests hitting when the start point becomes open (should be always)
        mockBoard.hitPiece(1, Player.Black);
        mockBoard.hitPiece(1, Player.Black);
        Point p = mockBoard.getPoint(1);

        assertEquals(p.getCount(), 0);
        assertTrue(p.isOpen());
        assertEquals(p.getPlayer(), Player.None);
    }

    @Test
    void testHitBarCount() {
        // tests bar count changes correctly
        mockBoard.hitPiece(1, Player.Black);
        mockBoard.hitPiece(1, Player.Black);

        assertEquals(mockBoard.getBarCount(Player.White), 0);
        assertEquals(mockBoard.getBarCount(Player.Black), 2);
    }


    @Test
    void testPipCountStart() {
        // tests pip count at the board starting position
        // verifies starting configuration is correct
        assertEquals(mockBoard.getPipCount(Player.White), 167);
        assertEquals(mockBoard.getPipCount(Player.Black), 167);
    }

    @Test
    void testPipCountMove() {
        // tests pip count updates appropriately when pieces are moved
        mockBoard.movePiece(1, 9, Player.Black); // diff -8
        mockBoard.movePiece(6, 3, Player.White); // diff -3

        assertEquals(mockBoard.getPipCount(Player.White), 164);
        assertEquals(mockBoard.getPipCount(Player.Black), 159);
    }

    @Test
    void testPipCountBearOff() {
        // tests pip count updates appropriately when pieces are borne off
        mockBoard.bearOffPiece(19, Player.Black); // diff -6
        mockBoard.bearOffPiece(19, Player.Black); // diff -6
        mockBoard.movePiece(8, 3, Player.White); // diff -5
        mockBoard.bearOffPiece(3, Player.White); // diff -3

        assertEquals(mockBoard.getPipCount(Player.White), 159);
        assertEquals(mockBoard.getPipCount(Player.Black), 155);
    }

    @Test
    void testPipCountEnter() {
        // tests pip count updates appropriately when pieces are entered from the bar
        mockBoard.hitPiece(12, Player.Black); // diff +12
        mockBoard.enterPiece(3, Player.Black); // diff -3
        mockBoard.hitPiece(8, Player.White); // diff +17
        mockBoard.hitPiece(8, Player.White); // diff +17
        mockBoard.enterPiece(21, Player.White); // diff -4

        assertEquals(mockBoard.getPipCount(Player.White), 197);
        assertEquals(mockBoard.getPipCount(Player.Black), 176);
    }

    @Test
    void testPipCountHit() {
        // tests pip count updates appropriately when pieces are hit
        mockBoard.hitPiece(12, Player.Black); // diff +12
        mockBoard.hitPiece(8, Player.White); // diff +17
        mockBoard.hitPiece(8, Player.White); // diff +17

        assertEquals(mockBoard.getPipCount(Player.White), 201);
        assertEquals(mockBoard.getPipCount(Player.Black), 179);
    }
}