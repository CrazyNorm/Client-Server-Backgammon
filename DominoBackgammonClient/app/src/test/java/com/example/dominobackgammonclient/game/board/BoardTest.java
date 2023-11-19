package com.example.dominobackgammonclient.game.board;

import com.example.dominobackgammonclient.game.common.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class BoardTest {

    private Board mockBoard;

    @Before
    public void setupBoard() {
        // re-initialises the board for each test
        // Board is just a data structure, so moves don't have to be valid
        mockBoard = new Board();
    }


    @Test
    public void testConstructBoardStart() {
        // test index list constructor with starting board position
        int[] iList = new int[] {
                6, 6, 6, 6, 6,
                8, 8, 8,
                13, 13, 13, 13, 13,
                24, 24
        };
        mockBoard = new Board(iList, iList);

        assertEquals(mockBoard.getPoint(6).getCount(), 5);
        assertEquals(mockBoard.getPoint(6).getPlayer(), Player.Client);
        assertEquals(mockBoard.getPoint(8).getCount(), 3);
        assertEquals(mockBoard.getPoint(8).getPlayer(), Player.Client);
        assertEquals(mockBoard.getPoint(13).getCount(), 5);
        assertEquals(mockBoard.getPoint(13).getPlayer(), Player.Client);
        assertEquals(mockBoard.getPoint(24).getCount(), 2);
        assertEquals(mockBoard.getPoint(24).getPlayer(), Player.Client);

        assertEquals(mockBoard.getPoint(19).getCount(), 5);
        assertEquals(mockBoard.getPoint(19).getPlayer(), Player.Opponent);
        assertEquals(mockBoard.getPoint(17).getCount(), 3);
        assertEquals(mockBoard.getPoint(17).getPlayer(), Player.Opponent);
        assertEquals(mockBoard.getPoint(12).getCount(), 5);
        assertEquals(mockBoard.getPoint(12).getPlayer(), Player.Opponent);
        assertEquals(mockBoard.getPoint(1).getCount(), 2);
        assertEquals(mockBoard.getPoint(1).getPlayer(), Player.Opponent);

        assertEquals(mockBoard.getPipCount(Player.Client), 167);
        assertEquals(mockBoard.getPipCount(Player.Opponent), 167);
    }

    @Test
    public void testConstructBoardNormal() {
        // test index list constructor with some "normal looking" data
        int[] cList = new int[] {
                3, 3,
                6, 6, 6, 6,
                8, 8,
                10, 10, 10,
                13, 13,
                20,
                24
        };
        int[] oList = new int[] {
                2,
                4,
                6, 6, 6,
                7,
                8, 8,
                11, 11,
                13, 13,
                16, 16,
                24
        };
        mockBoard = new Board(cList, oList);

        assertEquals(mockBoard.getPoint(3).getCount(), 2);
        assertEquals(mockBoard.getPoint(3).getPlayer(), Player.Client);
        assertEquals(mockBoard.getPoint(6).getCount(), 4);
        assertEquals(mockBoard.getPoint(6).getPlayer(), Player.Client);
        assertEquals(mockBoard.getPoint(8).getCount(), 2);
        assertEquals(mockBoard.getPoint(8).getPlayer(), Player.Client);
        assertEquals(mockBoard.getPoint(10).getCount(), 3);
        assertEquals(mockBoard.getPoint(10).getPlayer(), Player.Client);
        assertEquals(mockBoard.getPoint(13).getCount(), 2);
        assertEquals(mockBoard.getPoint(13).getPlayer(), Player.Client);
        assertEquals(mockBoard.getPoint(20).getCount(), 1);
        assertEquals(mockBoard.getPoint(20).getPlayer(), Player.Client);
        assertEquals(mockBoard.getPoint(24).getCount(), 1);
        assertEquals(mockBoard.getPoint(24).getPlayer(), Player.Client);

        assertEquals(mockBoard.getPoint(23).getCount(), 1);
        assertEquals(mockBoard.getPoint(23).getPlayer(), Player.Opponent);
        assertEquals(mockBoard.getPoint(21).getCount(), 1);
        assertEquals(mockBoard.getPoint(21).getPlayer(), Player.Opponent);
        assertEquals(mockBoard.getPoint(19).getCount(), 3);
        assertEquals(mockBoard.getPoint(19).getPlayer(), Player.Opponent);
        assertEquals(mockBoard.getPoint(18).getCount(), 1);
        assertEquals(mockBoard.getPoint(18).getPlayer(), Player.Opponent);
        assertEquals(mockBoard.getPoint(17).getCount(), 2);
        assertEquals(mockBoard.getPoint(17).getPlayer(), Player.Opponent);
        assertEquals(mockBoard.getPoint(14).getCount(), 2);
        assertEquals(mockBoard.getPoint(14).getPlayer(), Player.Opponent);
        assertEquals(mockBoard.getPoint(12).getCount(), 2);
        assertEquals(mockBoard.getPoint(12).getPlayer(), Player.Opponent);
        assertEquals(mockBoard.getPoint(9).getCount(), 2);
        assertEquals(mockBoard.getPoint(9).getPlayer(), Player.Opponent);
        assertEquals(mockBoard.getPoint(1).getCount(), 1);
        assertEquals(mockBoard.getPoint(1).getPlayer(), Player.Opponent);

        assertEquals(mockBoard.getPipCount(Player.Client), 146);
        assertEquals(mockBoard.getPipCount(Player.Opponent), 151);
    }

    @Test
    public void testConstructBoardOff() {
        // test index list constructor with some pieces borne off
        int[] cList = new int[] {
                0, 0,
                6, 6, 6,
                8, 8, 8,
                13, 13, 13, 13, 13,
                24, 24
        };
        int[] oList = new int[] {
                0, 0, 0,
                6, 6,
                8, 8, 8,
                13, 13, 13, 13, 13,
                24, 24
        };
        mockBoard = new Board(cList, oList);

        assertEquals(mockBoard.getOffCount(Player.Client), 2);
        assertEquals(mockBoard.getPipCount(Player.Client), 155);

        assertEquals(mockBoard.getOffCount(Player.Opponent), 3);
        assertEquals(mockBoard.getPipCount(Player.Opponent), 149);
    }

    @Test
    public void testConstructBoardBar() {
        // test index list constructor with some pieces on the bar
        int[] cList = new int[] {
                6, 6, 6,
                8, 8, 8,
                13, 13, 13, 13, 13,
                24, 24,
                25, 25
        };
        int[] oList = new int[] {
                6, 6,
                8, 8, 8,
                13, 13, 13, 13, 13,
                24, 24,
                25, 25, 25
        };
        mockBoard = new Board(cList, oList);

        assertEquals(mockBoard.getBarCount(Player.Client), 2);
        assertEquals(mockBoard.getPipCount(Player.Client), 205);

        assertEquals(mockBoard.getBarCount(Player.Opponent), 3);
        assertEquals(mockBoard.getPipCount(Player.Opponent), 224);
    }


    @Test
    public void testMoveStartClosed() {
        // tests moving pieces when the start point is still closed afterward
        mockBoard.movePiece(12,14, Player.Opponent);
        Point p = mockBoard.getPoint(12);

        assertEquals(p.getCount(), 4);
        assertTrue(p.isClosed());
        assertEquals(p.getPlayer(), Player.Opponent);
    }

    @Test
    public void testMoveStartBlot() {
        // tests moving pieces when the start point becomes a blot
        mockBoard.movePiece(1,2, Player.Opponent);
        Point p = mockBoard.getPoint(1);

        assertEquals(p.getCount(), 1);
        assertTrue(p.isBlot());
        assertEquals(p.getPlayer(), Player.Opponent);
    }

    @Test
    public void testMoveStartOpen() {
        // tests moving pieces when the start point becomes open
        mockBoard.movePiece(1,2, Player.Opponent);
        mockBoard.movePiece(1,2, Player.Opponent);
        Point p = mockBoard.getPoint(1);

        assertEquals(p.getCount(), 0);
        assertTrue(p.isOpen());
        assertNull(p.getPlayer());
    }

    @Test
    public void testMoveEndBlot() {
        // tests moving pieces when the end point becomes a blot
        mockBoard.movePiece(1,2, Player.Opponent);
        Point p = mockBoard.getPoint(2);

        assertEquals(p.getCount(), 1);
        assertTrue(p.isBlot());
        assertEquals(p.getPlayer(), Player.Opponent);
    }

    @Test
    public void testMoveEndClosed() {
        // tests moving pieces when the start point becomes closed
        mockBoard.movePiece(1,2, Player.Opponent);
        mockBoard.movePiece(1,2, Player.Opponent);
        Point p = mockBoard.getPoint(2);

        assertEquals(p.getCount(), 2);
        assertTrue(p.isClosed());
        assertEquals(p.getPlayer(), Player.Opponent);
    }


    @Test
    public void testBearOffStartClosed() {
        // tests bearing off when the start point is still closed afterward
        mockBoard.bearOffPiece(6, Player.Client);
        Point p = mockBoard.getPoint(6);

        assertEquals(p.getCount(), 4);
        assertTrue(p.isClosed());
        assertEquals(p.getPlayer(), Player.Client);
    }

    @Test
    public void testBearOffStartBlot() {
        // tests bearing off when the start point becomes a blot
        mockBoard.bearOffPiece(6, Player.Client);
        mockBoard.bearOffPiece(6, Player.Client);
        mockBoard.bearOffPiece(6, Player.Client);
        mockBoard.bearOffPiece(6, Player.Client);
        Point p = mockBoard.getPoint(6);

        assertEquals(p.getCount(), 1);
        assertTrue(p.isBlot());
        assertEquals(p.getPlayer(), Player.Client);
    }

    @Test
    public void testBearOffStartOpen() {
        // tests bearing off when the start point becomes open
        mockBoard.bearOffPiece(6, Player.Client);
        mockBoard.bearOffPiece(6, Player.Client);
        mockBoard.bearOffPiece(6, Player.Client);
        mockBoard.bearOffPiece(6, Player.Client);
        mockBoard.bearOffPiece(6, Player.Client);
        Point p = mockBoard.getPoint(6);

        assertEquals(p.getCount(), 0);
        assertTrue(p.isOpen());
        assertNull(p.getPlayer());
    }

    @Test
    public void testBearOffOffBoardCount() {
        // tests count for borne off pieces changes correctly
        mockBoard.bearOffPiece(6, Player.Client);
        mockBoard.bearOffPiece(6, Player.Client);

        assertEquals(mockBoard.getOffCount(Player.Client), 2);
        assertEquals(mockBoard.getOffCount(Player.Opponent), 0);
    }


    @Test
    public void testEnterEndBlot() {
        // tests entering off the bar when the end point becomes a blot
        mockBoard.hitPiece(1, Player.Opponent);
        mockBoard.enterPiece(2, Player.Opponent);
        Point p = mockBoard.getPoint(2);

        assertEquals(p.getCount(), 1);
        assertTrue(p.isBlot());
        assertEquals(p.getPlayer(), Player.Opponent);
    }

    @Test
    public void testEnterEndClosed() {
        // tests entering off the bar when the end point becomes closed
        mockBoard.hitPiece(1, Player.Opponent);
        mockBoard.hitPiece(1, Player.Opponent);
        mockBoard.enterPiece(2, Player.Opponent);
        mockBoard.enterPiece(2, Player.Opponent);
        Point p = mockBoard.getPoint(2);

        assertEquals(p.getCount(), 2);
        assertTrue(p.isClosed());
        assertEquals(p.getPlayer(), Player.Opponent);
    }

    @Test
    public void testEnterBarCount() {
        // tests bar count changes correctly
        mockBoard.hitPiece(1, Player.Opponent);
        mockBoard.hitPiece(1, Player.Opponent);
        mockBoard.enterPiece(2, Player.Opponent);

        assertEquals(mockBoard.getBarCount(Player.Client), 0);
        assertEquals(mockBoard.getBarCount(Player.Opponent), 1);
    }


    @Test
    public void testHitStartOpen() {
        // tests hitting when the start point becomes open (should be always)
        mockBoard.hitPiece(1, Player.Opponent);
        mockBoard.hitPiece(1, Player.Opponent);
        Point p = mockBoard.getPoint(1);

        assertEquals(p.getCount(), 0);
        assertTrue(p.isOpen());
        assertNull(p.getPlayer());
    }

    @Test
    public void testHitBarCount() {
        // tests bar count changes correctly
        mockBoard.hitPiece(1, Player.Opponent);
        mockBoard.hitPiece(1, Player.Opponent);

        assertEquals(mockBoard.getBarCount(Player.Client), 0);
        assertEquals(mockBoard.getBarCount(Player.Opponent), 2);
    }


    @Test
    public void testPipCountStart() {
        // tests pip count at the board starting position
        // verifies starting configuration is correct
        assertEquals(mockBoard.getPipCount(Player.Client), 167);
        assertEquals(mockBoard.getPipCount(Player.Opponent), 167);
    }

    @Test
    public void testPipCountMove() {
        // tests pip count updates appropriately when pieces are moved
        mockBoard.movePiece(1, 9, Player.Opponent); // diff -8
        mockBoard.movePiece(6, 3, Player.Client); // diff -3

        assertEquals(mockBoard.getPipCount(Player.Client), 164);
        assertEquals(mockBoard.getPipCount(Player.Opponent), 159);
    }

    @Test
    public void testPipCountBearOff() {
        // tests pip count updates appropriately when pieces are borne off
        mockBoard.bearOffPiece(19, Player.Opponent); // diff -6
        mockBoard.bearOffPiece(19, Player.Opponent); // diff -6
        mockBoard.movePiece(8, 3, Player.Client); // diff -5
        mockBoard.bearOffPiece(3, Player.Client); // diff -3

        assertEquals(mockBoard.getPipCount(Player.Client), 159);
        assertEquals(mockBoard.getPipCount(Player.Opponent), 155);
    }

    @Test
    public void testPipCountEnter() {
        // tests pip count updates appropriately when pieces are entered from the bar
        mockBoard.hitPiece(12, Player.Opponent); // diff +12
        mockBoard.enterPiece(3, Player.Opponent); // diff -3
        mockBoard.hitPiece(8, Player.Client); // diff +17
        mockBoard.hitPiece(8, Player.Client); // diff +17
        mockBoard.enterPiece(21, Player.Client); // diff -4

        assertEquals(mockBoard.getPipCount(Player.Client), 197);
        assertEquals(mockBoard.getPipCount(Player.Opponent), 176);
    }

    @Test
    public void testPipCountHit() {
        // tests pip count updates appropriately when pieces are hit
        mockBoard.hitPiece(12, Player.Opponent); // diff +12
        mockBoard.hitPiece(8, Player.Client); // diff +17
        mockBoard.hitPiece(8, Player.Client); // diff +17

        assertEquals(mockBoard.getPipCount(Player.Client), 201);
        assertEquals(mockBoard.getPipCount(Player.Opponent), 179);
    }
}
