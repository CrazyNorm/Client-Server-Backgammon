package game.common;

import game.board.Board;
import game.board.Point;
import game.dominoes.Domino;
import game.dominoes.Hand;
import server.pojo.DominoPojo;
import server.pojo.MovePojo;
import server.pojo.TurnPojo;

import java.util.ArrayList;
import java.util.List;

public class Game {

    // NOTE: for check & make move methods
    // end point = 0 means bear off
    // start point = 25 means enter from bar
    // (doesn't change for each player)

    private Board boardState;
    private final Hand whiteDominoes;
    private final Hand blackDominoes;
    private Player currentPlayer;
    private int turnCount;
    private boolean swapped;

    private Player disconnect; // used to inform all players of a disconnection


    public Game() {
        this.boardState = new Board();
        this.whiteDominoes = new Hand(Player.White, 1);
        this.blackDominoes = new Hand(Player.Black, 2);
        this.currentPlayer = Player.White;
        this.turnCount = 1;
        this.disconnect = Player.None;
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

        // check already swapped once
        if (!swapped) return false;

        // check non-double is available
        if (!checkDomino(side1, side2)) return false;

        // check current player has the double in their hand
        if (!dominoes.hasDouble(val)) return false;
        // check double is available to be used
        if (!dominoes.isDoubleAvailable(val)) return false;
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

    public int getTurnCount() {
        return this.turnCount;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public boolean checkHands() {
        // check both hands to decide if domino swap is needed
        if (whiteDominoes.getRemaining() > 0) return false;
        else return (blackDominoes.getRemaining() <= 0);
    }

    public void swapHands() {
        whiteDominoes.swapDominoSet();
        blackDominoes.swapDominoSet();
        if (!swapped) swapped = true;
    }


    public boolean checkTurn(TurnPojo turn) {
        // TODO check for using wrong number of moves
        // checks an entire turn is valid & applies it if it is

        // check correct player
        if (currentPlayer != turn.getPlayer()) return false;

        // check the domino(s) are valid
        if (turn.getDominoes().isEmpty() || turn.getDominoes().size() > 2) return false;
        DominoPojo dom = turn.getDominoes().get(0);
        DominoPojo dbl = null;
        if (turn.getDominoes().size() == 1) {
            // 1 domino - check domino is non-double & valid
            if (dom.getSide1() == dom.getSide2()) return false;
            if (!checkDomino(dom.getSide1(), dom.getSide2())) return false;
        }
        else if (turn.getDominoes().size() == 2) {
            // 2 dominoes - check only 1 is double & both valid
            DominoPojo d1 = turn.getDominoes().get(0);
            DominoPojo d2 = turn.getDominoes().get(1);

            if (d1.getSide1() == d1.getSide2()) {
                if (d2.getSide1() == d2.getSide2()) return false;
                dom = d2;
                dbl = d1;
            } else if (d2.getSide1() == d2.getSide2()) {
                dom = d1;
                dbl = d2;
            } else return false;

            if (!checkDomino(dbl.getSide1(), dom.getSide1(), dom.getSide2())) return false;
        }

        Board restoreBoard = new Board(this.boardState);
        // check moves are valid
        for (MovePojo move: turn.getMoves()) {
            int start = move.getStart();
            int end = move.getEnd();
            // flip moves for black
            if (currentPlayer == Player.Black) {
                if (start < 25) start = 25 - start;
                if (end > 0) end = 25 - end;
            }

            if (!check(start, end)) {
                // restore board to before any moves were made
                this.boardState = restoreBoard;
                return false;
            }
            make(start, end);
        }

        // player, dominoes & moves all valid
        // mark dominoes as used
        if (dbl == null) useDomino(dom.getSide1(), dom.getSide2());
        else useDomino(dbl.getSide1(), dom.getSide1(), dom.getSide2());

        return true;
    }


    public String checksum() {
        // hashes the game state to compare as a checksum
        // for piece positions, both black and white move from 24 to 1

        String checksum = "";
        // current player (reversed as server has already progressed to next turn)
        if (currentPlayer == Player.White) checksum += "b;;";
        else checksum += "w;;";

        // white
        checksum += "w" + boardState.getPipCount(Player.White) + ";";
        for (Domino dom: whiteDominoes.getDominoes()) {
            if (dom == null) continue;
            checksum += dom.getSide1() + "" + dom.getSide2();
            if (dom.isUsed()) checksum += "u";
            else checksum += "a";
        }
        if (boardState.getOffCount(Player.White) > 0)
            checksum += ";0x" + boardState.getOffCount(Player.White);
        for (int i = 1; i < 25; i++) {
            Point p = boardState.getPoint(i);
            if (p.getPlayer() == Player.White)
                checksum += ";" + i + "x" + p.getCount();
        }
        if (boardState.getBarCount(Player.White) > 0)
            checksum += ";25x" + boardState.getBarCount(Player.White);

        checksum += ";;";

        // black
        checksum += "b" + boardState.getPipCount(Player.Black) + ";";
        for (Domino dom: blackDominoes.getDominoes()) {
            if (dom == null) continue;
            checksum += dom.getSide1() + "" + dom.getSide2();
            if (dom.isUsed()) checksum += "u";
            else checksum += "a";
        }
        if (boardState.getOffCount(Player.Black) > 0)
            checksum += ";0x" + boardState.getOffCount(Player.Black);
        for (int i = 24; i > 0; i--) {
            Point p = boardState.getPoint(i);
            if (p.getPlayer() == Player.Black)
                checksum += ";" + (25-i) + "x" + p.getCount();
        }
        if (boardState.getBarCount(Player.Black) > 0)
            checksum += ";25x" + boardState.getBarCount(Player.Black);

        return checksum;
    }


    public List<Integer> getPieces(Player colour) {
        List<Integer> pieces = new ArrayList<>();
        // count on-board pieces
        for (int i = 1; i < 25; i++) {
            Point p = boardState.getPoint(i);
            if (p.getPlayer() == colour)
                for (int j = 0; j < p.getCount(); j++) {
                    if (colour == Player.White) pieces.add(i);
                    else pieces.add(25-i);
                }
        }
        // count hit pieces
        for (int i = 0; i < boardState.getBarCount(colour); i++)
            pieces.add(25);
        // count borne off pieces
        for (int i = 0; i < boardState.getOffCount(colour); i++)
            pieces.add(0);
        return pieces;
    }

    public List<DominoPojo> getDominoes(Player colour) {
        List<DominoPojo> dominoPojos = new ArrayList<>();
        if (colour == Player.White)
            for (Domino d: whiteDominoes.getDominoes()) {
                if (d == null) continue;
                dominoPojos.add(new DominoPojo(d.getSide1(), d.getSide2(), d.isAvailable()));
            }
        else if (colour == Player.Black)
            for (Domino d: blackDominoes.getDominoes()) {
                if (d == null) continue;
                dominoPojos.add(new DominoPojo(d.getSide1(), d.getSide2(), d.isAvailable()));
            }

        return dominoPojos;
    }

    public int getSet(Player colour) {
        if (colour == Player.White) return whiteDominoes.getDominoSet();
        else return blackDominoes.getDominoSet();
    }

    public Player getDisconnect() {
        return this.disconnect;
    }

    public void setDisconnect(Player disconnect) {
        this.disconnect = disconnect;
    }
}
