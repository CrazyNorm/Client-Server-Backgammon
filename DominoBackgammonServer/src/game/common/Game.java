package game.common;

import game.board.Board;
import game.board.Point;
import game.dominoes.Hand;

public class Game {

    // NOTE: for check & make move methods
    // end point = 0 means bear off
    // start point = 25 means enter from bar
    // (doesn't change for each player)

    private final Board boardState;
    private final Hand whiteDominoes;
    private final Hand blackDominoes;
    private Player currentPlayer;
    private int turnCount;


    public Game() {
        this.boardState = new Board();
        this.whiteDominoes = new Hand(Player.White, 1);
        this.blackDominoes = new Hand(Player.Black, 2);
        this.currentPlayer = Player.White;
        this.turnCount = 1;
    }


    public boolean check(int start, int end) {
        // check start & end are in acceptable range
        if (start < 1 || start > 25) return false;
        if (end < 0 || end > 24) return false;

        // select appropriate check for bear off / enter
        if (end == 0) return checkBearOff(start);
        if (start == 25) return checkEnter(end);
        return checkMove(start, end);
    }

    private boolean checkMove(int start, int end) {
        // check the current player doesn't have any pieces to enter first
        if (boardState.getBarCount(currentPlayer) > 0) return false;
        // check for trying to move the back men before turn 4
        if (turnCount < 4) {
            if (currentPlayer == Player.White && start == 24) return false;
            else if (currentPlayer == Player.Black && start == 1) return false;
        }
        // check move direction
        if (currentPlayer == Player.White && start < end) return false;
        else if (currentPlayer == Player.Black && start > end) return false;
        // check start point has a piece available to move
        if (boardState.getPoint(start).getPlayer() != currentPlayer) return false;
        // check end point has at most 1 piece of the opposing player
        Point endPoint = boardState.getPoint(end);
        if (endPoint.getPlayer() == currentPlayer) return true;
        else return !endPoint.isClosed();
    }

    private boolean checkBearOff(int start) {
        // check the current player doesn't have any pieces to enter first
        if (boardState.getBarCount(currentPlayer) > 0) return false;
        // check all pieces are in home board
        int homeTotal = boardState.getOffCount(currentPlayer);
        for (int i = 1; i < 7; i++) {
            // count total for all pieces in home board controlled by current player
            Point p;
            if (currentPlayer == Player.White) p = boardState.getPoint(i);
            else p = boardState.getPoint(25 - i);
            if (p.getPlayer() == currentPlayer) homeTotal += p.getCount();
        }
        if (homeTotal < 15) return false;
        // check start point has a piece available to bear off
        return (boardState.getPoint(start).getPlayer() == currentPlayer);
    }

    private boolean checkEnter(int end) {
        // check the current player has a piece on the bar to enter
        if (boardState.getBarCount(currentPlayer) < 1) return false;
        // check end point is in the opposition's home board
        if (currentPlayer == Player.White && end < 19) return false;
        else if (currentPlayer == Player.Black && end > 6) return false;
        // check end point has at most 1 piece of the opposing player
        Point endPoint = boardState.getPoint(end);
        if (endPoint.getPlayer() == currentPlayer) return true;
        else return !endPoint.isClosed();
    }


    public void make(int start, int end) {
        // Assumes move validity has already been checked

        // select appropriate check for bear off / enter
        if (end == 0) bearOff(start);
        else if (start == 25) enter(end);
        else move(start, end);
    }

    private void move(int start, int end) {
        // check for hitting a blot
        Point endPoint = boardState.getPoint(end);
        if (endPoint.getPlayer() != currentPlayer && endPoint.isBlot()) {
            if (currentPlayer == Player.White)
                boardState.hitPiece(end, Player.Black);
            else boardState.hitPiece(end, Player.White);
        }

        // move piece
        boardState.movePiece(start, end, currentPlayer);
    }

    private void bearOff(int start) {
        boardState.bearOffPiece(start, currentPlayer);
    }

    private void enter(int end) {
        // check for hitting a blot
        Point endPoint = boardState.getPoint(end);
        if (endPoint.getPlayer() != currentPlayer && endPoint.isBlot()) {
            if (currentPlayer == Player.White)
                boardState.hitPiece(end, Player.Black);
            else boardState.hitPiece(end, Player.White);
        }

        // move piece
        boardState.enterPiece(end, currentPlayer);
    }


    public Player checkWin() {
        // checks if either player has won
        // called for each player's turn, so don't need to check both players
        if (boardState.getOffCount(currentPlayer) == 15) return currentPlayer;
        else return Player.None;
    }

    public int checkWinType(Player player) {
        // decides how many points a win is worth for the given player
        boolean backgammon = false;
        boolean gammon = false;

        if (player == Player.White) {
            // check for backgammon
            if (boardState.getBarCount(Player.Black) > 0) backgammon = true;
            for (int i = 1; i < 7; i++) {
                // check if any pieces in home board controlled by opposition player
                if (boardState.getPoint(i).getPlayer() == Player.Black) {
                    backgammon = true;
                    break;
                }
            }

            // check for gammon
            if (boardState.getOffCount(Player.Black) < 1) gammon = true;
        }
        else {
            // check for backgammon
            if (boardState.getBarCount(Player.White) > 0) backgammon = true;
            for (int i = 1; i < 7; i++) {
                // check if any pieces in home board controlled by opposition player
                if (boardState.getPoint(24 - i).getPlayer() == Player.White) {
                    backgammon = true;
                    break;
                }
            }

            // check for gammon
            if (boardState.getOffCount(Player.White) < 1) gammon = true;
        }


        // return point total based on win type
        if (backgammon) return 3;
        else if (gammon) return 2;
        else return 1;
    }


    public boolean checkDomino(int side1, int side2) {
        Hand dominoes;
        if (currentPlayer == Player.White) dominoes = whiteDominoes;
        else dominoes = blackDominoes;

        // check current player has the domino in their hand
        if (!dominoes.hasDomino(side1, side2)) return false;
        // check domino is available to be used
        return dominoes.isDominoAvailable(side1, side2);
    }

    public boolean checkDomino(int val, int side1, int side2) {
        Hand dominoes;
        if (currentPlayer == Player.White) dominoes = whiteDominoes;
        else dominoes = blackDominoes;

        // check non-double is available
        if (!checkDomino(side1, side2)) return false;

        // check current player has the double in their hand
        if (!dominoes.hasDouble(val)) return false;
        // check double is available to be used
        if (dominoes.isDoubleAvailable(val)) return false;
        // check double is the next lowest un-used double
        return dominoes.isNextDouble(val);
    }

    public void useDomino(int side1, int side2) {
        // Assumes validity has already been checked

        Hand dominoes;
        if (currentPlayer == Player.White) dominoes = whiteDominoes;
        else dominoes = blackDominoes;

        dominoes.useDomino(side1, side2);
    }

    public void useDomino(int val, int side1, int side2) {
        // Assumes validity has already been checked

        Hand dominoes;
        if (currentPlayer == Player.White) dominoes = whiteDominoes;
        else dominoes = blackDominoes;

        dominoes.useDouble(val);
        dominoes.useDomino(side1, side2);
    }


    public void nextTurn() {
        if (currentPlayer == Player.White) currentPlayer = Player.Black;
        else {
            currentPlayer = Player.White;
            // only increment turn count when both players have taken a turn
            turnCount++;
        }
    }

    public boolean checkHands() {
        if (whiteDominoes.getRemaining() > 0) return false;
        else return (blackDominoes.getRemaining() <= 0);
    }

    public void swapHands() {
        whiteDominoes.swapDominoSet();
        blackDominoes.swapDominoSet();
    }

    // check turn (side1, side2, (val,) start1, end1, start2, end2(, start3, end3, start4, end4))?
    // check turn (encoded turn)?
    // just call check domino & check move & check win from socket thread?
    // check turn (side1, start1, end1)?
}
