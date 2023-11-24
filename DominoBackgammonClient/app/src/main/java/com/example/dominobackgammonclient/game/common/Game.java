package com.example.dominobackgammonclient.game.common;

import com.example.dominobackgammonclient.game.board.Board;
import com.example.dominobackgammonclient.game.board.Point;
import com.example.dominobackgammonclient.game.dominoes.Hand;
import com.example.dominobackgammonclient.ui.common.BGColour;

import java.util.Stack;

public class Game {

    // NOTE: for move methods
    // end point = 0 means bear off
    // start point = 25 means enter from bar
    // (doesn't change for each player)

    private final Board serverBoard;
    private Board clientBoard;

    private final Hand clientHand;
    private final Hand opponentHand;
    private final BGColour clientColour;
    private final BGColour opponentColour;

    private Player currentPlayer;
    private int turnCount;
    private final Stack<int[]> turnStack;
    private final Stack<Board> boardStack;

//    private MoveTree validMoves;
    private int[] highlightedMoves;
    private int selectedPoint;


    public Game(BGColour clientColour) {
        this.serverBoard = new Board();
        this.clientBoard = new Board();
        this.clientColour = clientColour;

        if (clientColour == BGColour.WHITE) {
            this.clientHand = new Hand(1);
            this.opponentHand = new Hand(2);
            this.opponentColour = BGColour.BLACK;
            this.currentPlayer = Player.Client;
        } else {
            this.clientHand = new Hand(2);
            this.opponentHand = new Hand(1);
            this.opponentColour = BGColour.WHITE;
            this.currentPlayer = Player.Opponent;
        }

        this.turnCount = 1;
        this.turnStack = new Stack<>();
        this.boardStack = new Stack<>();
    }


    public Board getBoard() {
        // returns client board: needed for UI
        return clientBoard;
    }
    public Hand getHand(Player player) {
        // returns the given player's entire hand: needed for UI
        if (player == Player.Client) return clientHand;
        else return opponentHand;
    }
    public BGColour getColour(Player player) {
        // returns the given player's colour: needed for UI
        if (player == Player.Client) return clientColour;
        else return opponentColour;
    }


    public void undoMove() {
        // undoes the top move from the move stack
        turnStack.pop();
        // restores board to previous state
        this.clientBoard = boardStack.pop();
    }

    public void endTurn() {
        // send chosen domino & move stack to socket
    }

    public void nextTurn() {
        // change current player
        // update turn count
        // deactivate domino selection
        // reset client board with server board
    }


    public void selectPiece(int point) {
        this.selectedPoint = point;
    }

    public void makeClientMove(int end) {
        // moves the selected piece to the given end position
        // moving player is always client

        // add move to turn stack
        turnStack.push(new int[]{selectedPoint, end});
        boardStack.push(clientBoard);

        // select appropriate method for bear off / enter
        if (end == 0) bearOffClient();
        else if (selectedPoint == 25) enterClient(end);
        else moveClient(end);
    }

    private void moveClient(int end) {
        // check for hitting a blot
        Point endPoint = clientBoard.getPoint(end);
        if (endPoint.getPlayer() == Player.Opponent && endPoint.isBlot()) {
            clientBoard.hitPiece(end, Player.Opponent);
        }

        // move piece
        clientBoard.movePiece(selectedPoint, end, Player.Client);
    }

    private void bearOffClient() {
        clientBoard.bearOffPiece(selectedPoint, Player.Client);
    }

    private void enterClient(int end) {
        // check for hitting a blot
        Point endPoint = clientBoard.getPoint(end);
        if (endPoint.getPlayer() == Player.Opponent && endPoint.isBlot()) {
            clientBoard.hitPiece(end, Player.Opponent);
        }

        // move piece
        clientBoard.enterPiece(end, Player.Client);
    }


    public void makeServerMove(int start, int end, Player player) {
        // makes a move according to server instruction

        // select appropriate method for bear off / enter
        if (end == 0) bearOffServer(start, player);
        else if (selectedPoint == 25) enterServer(end, player);
        else moveServer(start, end, player);
    }

    private void moveServer(int start, int end, Player player) {
        // check for hitting a blot
        Point endPoint = serverBoard.getPoint(end);
        if (endPoint.getPlayer() != player && endPoint.isBlot()) {
            if (player == Player.Client)
                serverBoard.hitPiece(end, Player.Opponent);
            else serverBoard.hitPiece(end, Player.Client);
        }

        // move piece
        serverBoard.movePiece(start, end, player);
    }

    private void bearOffServer(int start, Player player) {
        serverBoard.bearOffPiece(start, player);
    }

    private void enterServer(int end, Player player) {
        // check for hitting a blot
        Point endPoint = serverBoard.getPoint(end);
        if (endPoint.getPlayer() != player && endPoint.isBlot()) {
            if (player == Player.Client)
                serverBoard.hitPiece(end, Player.Opponent);
            else serverBoard.hitPiece(end, Player.Client);
        }

        // move piece
        serverBoard.enterPiece(end, player);
    }


    public void useDomino(int side1, int side2, Player player) {
        // marks domino as used according to server instruction

        if (player == Player.Client)
            clientHand.useDomino(side1, side2);
        else opponentHand.useDomino(side1, side2);
    }

    public void useDomino(int val, int side1, int side2, Player player) {
        // marks double + domino as used according to server instruction

        if (player == Player.Client) {
            clientHand.useDomino(side1, side2);
            clientHand.useDouble(val);
        }
        else {
            opponentHand.useDomino(side1, side2);
            opponentHand.useDouble(val);
        }
    }


    public void swapHands() {
        // swaps player hands according to server instruction
        clientHand.swapDominoSet();
        opponentHand.swapDominoSet();
    }


    public void win(Player winner, int type) {
        // change UI to reflect end of game
    }


    public void generateValidMoves() {
        // generate all valid moves for the client for the current board state
    }
}

