package com.example.dominobackgammonclient.game.common;

import com.example.dominobackgammonclient.game.board.Board;
import com.example.dominobackgammonclient.game.board.Point;
import com.example.dominobackgammonclient.game.dominoes.Domino;
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

    private MoveTree validMoves;
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



        // root tree node
        // add child for each domino
        // add children for each possible move
        // if any pieces on bar, only add nodes for entering moves
        // for each point, check if point + domino is not a closed opponent point
        // generate temp board with result of applying move
        // add children for each possible move for remaining move
        // (repeat for double)

        validMoves = new MoveTree();


        // add child for each usable domino
        for (Domino d : clientHand.getDominoes()) {
            if (d.isAvailable()) {
                MoveTree domNode = validMoves.addChild(
                        new DominoNode(d.getSide1(),d.getSide2())
                );

                // expand node with moves for the current domino
                expandNode(domNode, d, clientBoard);
            }
        }

        // track lowest movesLeft for each domino
        // remove any moves which have fewer max moves
        // for each node:
        // if movesLeft < minWastedMoves and this node has no children
        // then remove this node (remove from parent's children)
        // depth first: see notes

        // also remove any nodes where total distance > domino total

        // if domino is not double & wasted moves > 0:
        // search through moves to find max move distance
        // search through moves again to remove moves where distance < max

        validMoves.print();

    }

    private void expandNode(MoveTree node, Domino domino, Board board) {
        // add children to current node
        generateDominoMoves(domino, node, board);

        // for each non-terminal child
        for (MoveTree child: node.getChildren()) {
            if (child.getMovesLeft() > 0) {
                // generate board after applying current child's move
                Board tempBoard = new Board(board);
                tempBoard.movePiece(
                        ((MoveNode)child).getStart(),
                        ((MoveNode)child).getEnd(),
                        Player.Client
                );
                // expand the child with the new board state
                expandNode(child, domino, tempBoard);
            }
        }
    }

    private void generateDominoMoves(Domino domino, MoveTree parent, Board board) {
        // adds child nodes to a node with all valid moves from the given board state

        // if any pieces on bar, then only add moves which enter
        if (board.getBarCount(Player.Client) > 0) {
            // check if it's available to enter using side 1
            Point s1EnterPoint = board.getPoint(25 - domino.getSide1());
            if (s1EnterPoint.availableFor(Player.Client))
                parent.addChild(new MoveNode(
                        25,
                        25 - domino.getSide1(),
                        parent.getMovesLeft() - 1)
                );
            // check if it's available to enter using side 2
            Point s2EnterPoint = board.getPoint(25 - domino.getSide2());
            if (s2EnterPoint.availableFor(Player.Client))
                parent.addChild(new MoveNode(
                        25,
                        25 - domino.getSide2(),
                        parent.getMovesLeft() - 1)
                );
        }

        // track the highest point with at least 1 piece (used for bearing off)
        int highestPoint = 0;
        // for each point, add possible move nodes starting from that point
        for (int i = 24; i > 0; i--) {
            // check there is a piece to move from this point
            if (board.getPoint(i).getPlayer() != Player.Client) continue;

            // update highest point
            if (i > highestPoint) highestPoint = i;

            // don't include back men before turn 4
            if (i == 24 && turnCount < 4) continue;

            // check bearing off
            if (i < 6) {
                // exact bear off
                if (i == domino.getSide1() || i == domino.getSide2())
                    parent.addChild(new MoveNode(
                            i,
                            0,
                            parent.getMovesLeft() - 1)
                    );
                // everything higher already borne off: side 1
                if (i < domino.getSide1())
                    // specify a distance for these nodes as domino side is not the same as start - end
                    if (i == highestPoint)
                        parent.addChild(new MoveNode(
                                i,
                                0,
                                domino.getSide1(),
                                parent.getMovesLeft() - 1)
                        );
                // everything higher already borne off: side 2
                if (i < domino.getSide2())
                    if (i == highestPoint)
                        parent.addChild(new MoveNode(
                                i,
                                0,
                                domino.getSide2(),
                                parent.getMovesLeft() - 1)
                        );
            }

            // regular moves: side 1
            if (i > domino.getSide1()) {
                Point s1EndPoint = board.getPoint(i - domino.getSide1());
                if (s1EndPoint.availableFor(Player.Client))
                    parent.addChild(new MoveNode(
                            i,
                            i - domino.getSide1(),
                            parent.getMovesLeft() - 1)
                    );
            }
            // regular moves: side 2
            if (i > domino.getSide2()) {
                Point s2EndPoint = board.getPoint(i - domino.getSide2());
                if (s2EndPoint.availableFor(Player.Client))
                    parent.addChild(new MoveNode(
                            i,
                            i - domino.getSide2(),
                            parent.getMovesLeft() - 1)
                    );
            }
        }
    }

    private void generateDoubleMoves(Domino domino, MoveTree parent, Board board) {
        // adds child nodes to a domino node with all valid moves from the given board state
        // only checks one sde of the domino to avoid duplicates for doubles

        // if any pieces on bar, then only add moves which enter
        if (board.getBarCount(Player.Client) > 0) {
            // check if it's available to enter
            Point s1EnterPoint = board.getPoint(25 - domino.getSide1());
            if (s1EnterPoint.availableFor(Player.Client))
                parent.addChild(new MoveNode(
                        25,
                        25 - domino.getSide1(),
                        parent.getMovesLeft() - 1)
                );
        }

        // track the highest point with at least 1 piece (used for bearing off)
        int highestPoint = 0;
        // for each point, add possible move nodes starting from that point
        for (int i = 24; i > 0; i--) {
            // check there is a piece to move from this point
            if (board.getPoint(i).getPlayer() != Player.Client) continue;

            // update highest point
            if (i > highestPoint) highestPoint = i;

            // don't include back men before turn 4
            if (i == 24 && turnCount < 4) continue;

            // check bearing off
            if (i < 6) {
                // exact bear off
                if (i == domino.getSide1())
                    parent.addChild(new MoveNode(
                            i,
                            0,
                            parent.getMovesLeft() - 1)
                    );
                // everything higher already borne off
                if (i < domino.getSide1())
                    // specify a distance for these nodes as domino side is not the same as start - end
                    if (i == highestPoint)
                        parent.addChild(new MoveNode(
                                i,
                                0,
                                domino.getSide1(),
                                parent.getMovesLeft() - 1)
                        );
            }

            // regular moves
            if (i > domino.getSide1()) {
                Point endPoint = board.getPoint(i - domino.getSide1());
                if (endPoint.availableFor(Player.Client))
                    parent.addChild(new MoveNode(
                            i,
                            i - domino.getSide1(),
                            parent.getMovesLeft() - 1)
                    );
            }
        }
    }
}

