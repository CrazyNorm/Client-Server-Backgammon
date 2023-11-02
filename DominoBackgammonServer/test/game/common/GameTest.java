package game.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game mockGame;

    @BeforeEach
    void setupGame() {
        // re-initialise a new game object for each test
        mockGame = new Game();
    }



    @Test
    void testCheckOutOfRange() {
        // test start or end positions outside valid range
        assertFalse(mockGame.check(0,2));
        assertFalse(mockGame.check(26,2));
        assertFalse(mockGame.check(6,-1));
        assertFalse(mockGame.check(6,25));
    }


    @Test
    void testCheckBearOffBar() {
        // test attempting to bear off with a piece on the bar
        allBut1WhiteTo6(mockGame);
        // hit remaining piece on point 24
        mockGame.nextTurn();
        mockGame.make(1, 24);

        // try bear off from point 6
        mockGame.nextTurn();
        assertFalse(mockGame.check(6, 0));
    }

    @Test
    void testCheckBearOffAllHome() {
        // test attempting to bear off with pieces outside home board
        assertFalse(mockGame.check(6, 0));
    }

    @Test
    void testCheckBearOffFromEmpty() {
        // test attempting to bear off from an empty point
        allBut1WhiteTo6(mockGame);
        // also move remaining piece to point 6
        mockGame.make(24, 6);

        // try bear off from point 5
        assertFalse(mockGame.check(5, 0));
    }

    @Test
    void testCheckBearOffFromOpposition() {
        // test attempting to bear off from an opposition point
        allBut1WhiteTo6(mockGame);
        // also move remaining piece to point 6
        mockGame.make(24, 6);

        // try bear off from point 1
        assertFalse(mockGame.check(1, 0));
    }

    @Test
    void testCheckBearOffSuccessBlot() {
        // test bearing off successfully from a blot
        allBut1WhiteTo6(mockGame);
        // move remaining piece to point 5
        mockGame.make(24, 5);

        // bear off from point 5
        assertTrue(mockGame.check(5, 0));
    }

    @Test
    void testCheckBearOffSuccessClosed() {
        // test bearing off successfully from a closed point
        allBut1WhiteTo6(mockGame);
        // also move remaining piece to point 6
        mockGame.make(24, 6);

        // bear off from point 6
        assertTrue(mockGame.check(6, 0));
    }


    @Test
    void testCheckEnterEmptyBar() {
        // test attempting to enter without any pieces on the bar
        assertFalse(mockGame.check(25, 24));
    }

    @Test
    void testCheckEnterOutsideHome() {
        // test attempting to enter to a point not in the opposition's home board
        hitWhite24(mockGame);
        // try enter to point 18
        assertFalse(mockGame.check(25, 18));
    }

    @Test
    void testCheckEnterToOppositionClosed() {
        // test attempting to enter to a closed opposition point
        hitWhite24(mockGame);
        // try enter to point 19
        assertFalse(mockGame.check(25, 19));
    }

    @Test
    void testCheckEnterSuccessBlot() {
        // test entering successfully to a blot
        hitWhite24(mockGame);
        // enter to point 23
        assertTrue(mockGame.check(25, 23));
    }

    @Test
    void testCheckEnterSuccessClosed() {
        // test entering successfully to a closed point
        hitWhite24(mockGame);
        // close point 23 (moving backwards)
        mockGame.make(13, 23);
        // enter to point 23
        assertTrue(mockGame.check(25, 23));
    }

    @Test
    void testCheckEnterSuccessOpen() {
        // test entering successfully to an open point
        hitWhite24(mockGame);
        // enter to point 22
        assertTrue(mockGame.check(25, 22));
    }

    @Test
    void testCheckEnterSuccessHit() {
        // test entering successfully to an opposition blot (i.e. hit)
        hitWhite24(mockGame);
        // enter to point 24
        assertTrue(mockGame.check(25, 24));
    }


    @Test
    void testCheckMoveBar() {
        // test attempting to move with a piece on the bar
        hitWhite24(mockGame);

        // try move from point 6
        mockGame.nextTurn();
        assertFalse(mockGame.check(6, 5));
    }

    @Test
    void testCheckMoveBackMenEarly() {
        // test attempting to move back men before turn 4
        assertFalse(mockGame.check(24, 23));
    }

    @Test
    void testCheckMoveWrongDirection() {
        // test attempting to move in the wrong direction
        assertFalse(mockGame.check(6, 7));
    }

    @Test
    void testCheckMoveFromEmpty() {
        // test attempting to move from an empty point
        assertFalse(mockGame.check(7, 6));
    }

    @Test
    void testCheckMoveFromOpposition() {
        // test attempting to move from an opposition point
        assertFalse(mockGame.check(12, 11));
    }

    @Test
    void testCheckMoveToOppositionClosed() {
        // test attempting to move to a closed opposition point
        assertFalse(mockGame.check(6, 1));
    }

    @Test
    void testCheckMoveSuccessFromBlot() {
        // test moving successfully from a blot
        mockGame.make(6, 5);
        assertTrue(mockGame.check(5, 4));
    }

    @Test
    void testCheckMoveSuccessFromClosed() {
        // test moving successfully from a closed point
        assertTrue(mockGame.check(6, 5));
    }

    @Test
    void testCheckMoveSuccessToBlot() {
        // test moving successfully to a blot
        mockGame.make(6, 5);
        assertTrue(mockGame.check(6, 5));
    }

    @Test
    void testCheckMoveSuccessToClosed() {
        // test moving successfully to a closed point
        assertTrue(mockGame.check(8, 6));
    }

    @Test
    void testCheckEnterSuccessToOpen() {
        // test moving successfully to an open point
        assertTrue(mockGame.check(6, 5));
    }

    @Test
    void testCheckMoveSuccessHit() {
        // test moving successfully to an opposition blot (i.e. hit)

        // create black blot
        mockGame.nextTurn();
        mockGame.make(1, 5);
        mockGame.nextTurn();
        // hit the black blot
        assertTrue(mockGame.check(6, 5));
    }

    @Test
    void testCheckMoveSuccessBackMen() {
        // test moving back men after turn 4
        for (int i = 0; i < 6; i++) {
            mockGame.nextTurn();
        }
        assertTrue(mockGame.check(24, 23));
    }



    // make() is mostly an interface to Board methods, so doesn't require thorough tests
    @Test
    void testMakeEnterHit() {
        // test pieces are hit when entering to an opposition blot

        hitWhite24(mockGame);
        // enter to point 24
        mockGame.make(25, 24);
        // check black cannot move (due to piece on bar)
        mockGame.nextTurn();
        assertFalse(mockGame.check(19, 20));
    }

    @Test
    void testMakeMoveHit() {
        // test pieces are hit when moving to an opposition blot

        // create black blot
        mockGame.nextTurn();
        mockGame.make(1, 2);

        // hit black blot
        mockGame.nextTurn();
        mockGame.make(6, 2);

        // check black cannot move (due to piece on bar)
        mockGame.nextTurn();
        assertFalse(mockGame.check(19, 20));
    }



    @Test
    void testCheckWinWhite() {
        // tests when all white pieces are borne off

        // directly bear off all white pieces (bypasses validity checks)
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);

        mockGame.make(8, 0);
        mockGame.make(8, 0);
        mockGame.make(8, 0);

        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);

        mockGame.make(24, 0);
        mockGame.make(24, 0);

        assertEquals(mockGame.checkWin(), Player.White);
    }

    @Test
    void testCheckWinBlack() {
        // tests when all black pieces are borne off

        // directly bear off all black pieces (bypasses validity checks)
        mockGame.nextTurn();

        mockGame.make(19, 0);
        mockGame.make(19, 0);
        mockGame.make(19, 0);
        mockGame.make(19, 0);
        mockGame.make(19, 0);

        mockGame.make(17, 0);
        mockGame.make(17, 0);
        mockGame.make(17, 0);

        mockGame.make(12, 0);
        mockGame.make(12, 0);
        mockGame.make(12, 0);
        mockGame.make(12, 0);
        mockGame.make(12, 0);

        mockGame.make(1, 0);
        mockGame.make(1, 0);

        assertEquals(mockGame.checkWin(), Player.Black);
    }

    @Test
    void testCheckWinNoneInitial() {
        // tests when no player has won yet (game start)
        assertEquals(mockGame.checkWin(), Player.None);
    }

    @Test
    void testCheckWinNoneBoth14() {
        // tests when no player has won yet (both players borne off 14 pieces)

        // bear off 14 white pieces
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(8, 0);
        mockGame.make(8, 0);
        mockGame.make(8, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(24, 0);

        // bear off 14 black pieces
        mockGame.nextTurn();
        mockGame.make(19, 0);
        mockGame.make(19, 0);
        mockGame.make(19, 0);
        mockGame.make(19, 0);
        mockGame.make(19, 0);
        mockGame.make(17, 0);
        mockGame.make(17, 0);
        mockGame.make(17, 0);
        mockGame.make(12, 0);
        mockGame.make(12, 0);
        mockGame.make(12, 0);
        mockGame.make(12, 0);
        mockGame.make(12, 0);
        mockGame.make(1, 0);

        // check neither player has won
        assertEquals(mockGame.checkWin(), Player.None);
        mockGame.nextTurn();
        assertEquals(mockGame.checkWin(), Player.None);
    }



    @Test
    void testCheckWinStandard() {
        // tests for a standard win (1 point)

        // move black pieces from white home & bear off 1 black
        mockGame.nextTurn();
        mockGame.make(1, 7);
        mockGame.make(1, 7);
        mockGame.make(19, 0);
        mockGame.nextTurn();

        // directly bear off all white pieces (bypasses validity checks)
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(8, 0);
        mockGame.make(8, 0);
        mockGame.make(8, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(24, 0);
        mockGame.make(24, 0);

        // check win is only worth 1 point
        assertEquals(mockGame.checkWinType(Player.White), 1);
    }

    @Test
    void testCheckWinGammon() {
        // tests for a gammon win (2 points)

        // move black pieces from white home
        mockGame.nextTurn();
        mockGame.make(1, 7);
        mockGame.make(1, 7);
        mockGame.nextTurn();

        // directly bear off all white pieces (bypasses validity checks)
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(8, 0);
        mockGame.make(8, 0);
        mockGame.make(8, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(24, 0);
        mockGame.make(24, 0);

        // check win is worth 2 points
        assertEquals(mockGame.checkWinType(Player.White), 2);
    }

    @Test
    void testCheckWinBackgammonHome() {
        // tests for a backgammon win (3 points)
        // with opposition pieces in home board

        // directly bear off all white pieces (bypasses validity checks)
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(8, 0);
        mockGame.make(8, 0);
        mockGame.make(8, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(24, 0);
        mockGame.make(24, 0);

        // check win is worth 3 points
        assertEquals(mockGame.checkWinType(Player.White), 3);
    }

    @Test
    void testCheckWinBackgammonBar() {
        // tests for a backgammon win (3 points)
        // with opposition pieces on bar

        // move 1 black piece from white home & hit the other
        mockGame.nextTurn();
        mockGame.make(1, 7);
        mockGame.nextTurn();
        mockGame.make(6, 1);

        // directly bear off all white pieces (bypasses validity checks)
        mockGame.make(1, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(6, 0);
        mockGame.make(8, 0);
        mockGame.make(8, 0);
        mockGame.make(8, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(13, 0);
        mockGame.make(24, 0);
        mockGame.make(24, 0);

        // check win is worth 3 points
        assertEquals(mockGame.checkWinType(Player.White), 3);
    }



    @Test
    void testCheckDominoNotInHand() {
        // test dominoes not in the players' hands
        assertFalse(mockGame.checkDomino(3, 2));
        mockGame.nextTurn();
        assertFalse(mockGame.checkDomino(2, 1));
    }

    @Test
    void testCheckDominoNotAvailable() {
        // test dominoes already been used

        // use a domino for each player
        mockGame.useDomino(2, 1);
        mockGame.nextTurn();
        mockGame.useDomino(3, 2);
        mockGame.nextTurn();

        // check used dominoes
        assertFalse(mockGame.checkDomino(2, 1));
        mockGame.nextTurn();
        assertFalse(mockGame.checkDomino(3, 2));
    }

    @Test
    void testCheckDominoSuccess() {
        // test dominoes that are available for use
        assertTrue(mockGame.checkDomino(2, 1));
        mockGame.nextTurn();
        assertTrue(mockGame.checkDomino(3, 2));
    }

    @Test
    void testUseDomino() {
        // tests domino is no longer available after using it
        mockGame.useDomino(2, 1);
        assertFalse(mockGame.checkDomino(2, 1));
    }



    @Test
    void testCheckDoubleInvalidNonDouble() {
        // test doubles with invalid non-double substitutes
        assertFalse(mockGame.checkDomino(1, 3, 2));
        mockGame.nextTurn();
        assertFalse(mockGame.checkDomino(2, 2, 1));
    }

    @Test
    void testCheckDoubleNotInHand() {
        // test doubles not in the players' hands
        assertFalse(mockGame.checkDomino(2, 2, 1));
        mockGame.nextTurn();
        assertFalse(mockGame.checkDomino(1, 3, 2));
    }

    @Test
    void testCheckDoubleNotAvailable() {
        // test doubles already been used

        // use a double for each player
        mockGame.useDomino(1, 2, 1);
        mockGame.nextTurn();
        mockGame.useDomino(2, 3, 2);
        mockGame.nextTurn();

        // test used doubles
        assertFalse(mockGame.checkDomino(1, 3, 1));
        mockGame.nextTurn();
        assertFalse(mockGame.checkDomino(2, 4, 1));
    }

    @Test
    void testCheckDoubleNotNext() {
        // test doubles that aren't the next in sequence
        assertFalse(mockGame.checkDomino(3, 2, 1));
        mockGame.nextTurn();
        assertFalse(mockGame.checkDomino(4, 3, 2));
    }

    @Test
    void testCheckDoubleSuccess() {
        // tests doubles that are available for use
        assertFalse(mockGame.checkDomino(1, 2, 1));
        mockGame.nextTurn();
        assertFalse(mockGame.checkDomino(2, 3, 2));
    }

    @Test
    void testUseDouble() {
        // tests double & sacrifice domino are no longer available after using
        mockGame.useDomino(1, 2, 1);
        assertFalse(mockGame.checkDomino(1, 1));
        assertFalse(mockGame.checkDomino(2, 1));
    }



    @Test
    void testNextTurnTurnCount() {
        // test turn count increments when turn changes (but only when both players have taken a turn)
        assertEquals(mockGame.getTurnCount(), 1);
        mockGame.nextTurn();
        assertEquals(mockGame.getTurnCount(), 1);
        mockGame.nextTurn();
        assertEquals(mockGame.getTurnCount(), 2);
    }

    @Test
    void testNextTurnPlayer() {
        // test turn player changes when turn changes
        assertEquals(mockGame.getCurrentPlayer(), Player.White);
        mockGame.nextTurn();
        assertEquals(mockGame.getCurrentPlayer(), Player.Black);
        mockGame.nextTurn();
        assertEquals(mockGame.getCurrentPlayer(), Player.White);
    }



    @Test
    void testCheckHandsBothRemaining() {
        // tests check hands when both players have remaining dominoes
        assertFalse(mockGame.checkHands());
    }

    @Test
    void testCheckHandsWhiteRemaining() {
        // tests check hands when white has 1 remaining domino

        // use all but 1 white domino
        mockGame.useDomino(2, 1);
        mockGame.useDomino(3, 1);
        mockGame.useDomino(4, 2);
        mockGame.useDomino(5, 1);
        mockGame.useDomino(5, 3);
        mockGame.useDomino(5, 4);
        mockGame.useDomino(6, 2);

        // use all black dominoes
        mockGame.nextTurn();
        mockGame.useDomino(3, 2);
        mockGame.useDomino(4, 1);
        mockGame.useDomino(4, 3);
        mockGame.useDomino(5, 2);
        mockGame.useDomino(6, 1);
        mockGame.useDomino(6, 3);
        mockGame.useDomino(6, 5);

        assertFalse(mockGame.checkHands());
    }

    @Test
    void testCheckHandsBlackRemaining() {
        // tests check hands when black has 1 remaining domino

        // use all white dominoes
        mockGame.useDomino(2, 1);
        mockGame.useDomino(3, 1);
        mockGame.useDomino(4, 2);
        mockGame.useDomino(5, 1);
        mockGame.useDomino(5, 3);
        mockGame.useDomino(5, 4);
        mockGame.useDomino(6, 2);
        mockGame.useDomino(6, 4);

        // use all but 1 black domino
        mockGame.nextTurn();
        mockGame.useDomino(3, 2);
        mockGame.useDomino(4, 1);
        mockGame.useDomino(4, 3);
        mockGame.useDomino(5, 2);
        mockGame.useDomino(6, 1);
        mockGame.useDomino(6, 3);

        assertFalse(mockGame.checkHands());
    }

    @Test
    void testCheckHandsNoRemaining() {
        // tests check hands when neither player has any remaining dominoes

        // use all white dominoes
        mockGame.useDomino(2, 1);
        mockGame.useDomino(3, 1);
        mockGame.useDomino(4, 2);
        mockGame.useDomino(5, 1);
        mockGame.useDomino(5, 3);
        mockGame.useDomino(5, 4);
        mockGame.useDomino(6, 2);
        mockGame.useDomino(6, 4);

        // use all black dominoes
        mockGame.nextTurn();
        mockGame.useDomino(3, 2);
        mockGame.useDomino(4, 1);
        mockGame.useDomino(4, 3);
        mockGame.useDomino(5, 2);
        mockGame.useDomino(6, 1);
        mockGame.useDomino(6, 3);
        mockGame.useDomino(6, 5);

        assertTrue(mockGame.checkHands());
    }

    @Test
    void swapHands() {
        // just an interface to hand methods, so doesn't require thorough testing
        mockGame.swapHands();
        assertTrue(mockGame.checkDomino(3, 2));
        mockGame.nextTurn();
        assertTrue(mockGame.checkDomino(2, 1));
    }



    // test helper functions

    private static void allBut1WhiteTo6(Game g) {
        // moves all white pieces but 1 to point 6 to test bearing off
        // (1 remaining piece left on point 24)
        g.make(8, 6);
        g.make(8, 6);
        g.make(8, 6);
        g.make(13, 6);
        g.make(13, 6);
        g.make(13, 6);
        g.make(13, 6);
        g.make(13, 6);
        g.make(24, 6);
    }

    private static void hitWhite24(Game g) {
        // moves a piece to the bar from the starting position to test entering
        // leaves a white blot on 23 & black blot on 24

        // move a piece from 24 to create 2 blots
        g.make(24, 23);
        // hit remaining piece on 24
        g.nextTurn();
        g.make(1, 24);
        g.nextTurn();
    }
}