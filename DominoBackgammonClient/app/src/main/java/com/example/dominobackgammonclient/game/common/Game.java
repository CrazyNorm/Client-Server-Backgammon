package com.example.dominobackgammonclient.game.common;

import com.example.dominobackgammonclient.game.board.Board;
import com.example.dominobackgammonclient.game.board.Point;
import com.example.dominobackgammonclient.game.dominoes.Domino;
import com.example.dominobackgammonclient.game.dominoes.Hand;
import com.example.dominobackgammonclient.ui.common.BGColour;

import java.util.*;

public class Game {

    // NOTE: for move methods
    // end point = 0 means bear off
    // start point = 25 means enter from bar
    // (doesn't change for each player)

    private Board serverBoard;
    private Board clientBoard;

    private Hand clientHand;
    private Hand opponentHand;
    private final BGColour clientColour;
    private final BGColour opponentColour;

    private Player currentPlayer;
    private int turnCount;
    private final Stack<int[]> turnStack;
    private final Stack<Board> boardStack;

    private MoveTree validMoves;
    private List<Integer> highlightedMoves;
    private int selectedPoint = -1;
    private Domino selectedDomino;
    private Domino selectedDouble;


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


    public Game(Game oldGame) {
        // shallow copy constructor

        this.serverBoard = oldGame.serverBoard;
        this.clientBoard = oldGame.clientBoard;

        this.clientHand = oldGame.clientHand;
        this.clientColour = oldGame.clientColour;
        this.opponentHand = oldGame.opponentHand;
        this.opponentColour = oldGame.opponentColour;

        this.currentPlayer = oldGame.currentPlayer;
        this.turnCount = oldGame.turnCount;
        this.turnStack = oldGame.turnStack;
        this.boardStack = oldGame.boardStack;

        this.validMoves = oldGame.validMoves;
        this.highlightedMoves = oldGame.highlightedMoves;
        this.selectedPoint = oldGame.selectedPoint;
        this.selectedDomino = oldGame.selectedDomino;
        this.selectedDouble = oldGame.selectedDouble;
    }


    public Board getBoard() {
        // returns client board: needed for UI
        return clientBoard;
    }

    public void setServerBoard(List<Integer> whiteIndex, List<Integer> blackIndex) {
        if (clientColour == BGColour.WHITE)
            serverBoard = new Board(whiteIndex, blackIndex);
        else serverBoard = new Board(blackIndex, whiteIndex);
    }

    public Hand getHand(Player player) {
        // returns the given player's entire hand: needed for UI
        if (player == Player.Client) return clientHand;
        else return opponentHand;
    }

    public void setHand(Hand hand, BGColour colour) {
        if (colour == clientColour) clientHand = hand;
        else opponentHand = hand;
    }

    public BGColour getColour(Player player) {
        // returns the given player's colour: needed for UI
        if (player == Player.Client) return clientColour;
        else return opponentColour;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    public List<Integer> getHighlightedMoves() {
        return highlightedMoves;
    }

    public boolean isPieceSelected() {
        return (selectedPoint != -1);
    }


    public void undoMove() {
        // check there is a move to undo
        if (turnStack.isEmpty()) return;

        // undoes the top move from the move stack
        turnStack.pop();
        // restores board to previous state
        this.clientBoard = boardStack.pop();
        // deselects any selected point
        selectedPoint = -1;

        updateHighlightedMoves();
    }

    public boolean isUndoable() {
        // only allows pressing undo if there is something to undo
        return !turnStack.isEmpty();
    }

    public boolean isTurnValid() {
        // only allows submitting turn if a domino is selected, and there are no moves left
        if (currentPlayer != Player.Client) return false;
        if (selectedDomino == null) return false;
        return highlightedMoves.isEmpty();
    }

    public List<int[]> getMoves() {
        return turnStack;
    }

    public List<Domino> getSelectedDominoes() {
        List<Domino> dominoes = new ArrayList<>();
        if (selectedDomino != null) dominoes.add(selectedDomino);
        if (selectedDouble != null) dominoes.add(selectedDouble);
        return dominoes;
    }

    public void nextTurn() {
        // update turn count (only when both player's have taken a turn)
        if (getColour(currentPlayer) == BGColour.BLACK) turnCount++;

        // change current player
        if (currentPlayer == Player.Client) currentPlayer = Player.Opponent;
        else currentPlayer = Player.Client;

        // deactivate selection
        highlightedMoves.clear();
        selectedPoint = -1;
        selectedDomino = null;
        selectedDouble = null;
        // clear undo stack
        turnStack.clear();
        boardStack.clear();
        // reset client board with server board
        clientBoard = new Board(serverBoard);
    }


    public void selectPiece(int point) {
        // deselect if clicking non-highlighted point
        if (!highlightedMoves.contains(point)) selectedPoint = -1;
        // if no point selected, select the point
        else if (selectedPoint == -1) selectedPoint = point;
        // if another point is selected, move selected piece
        else makeClientMove(point);

        updateHighlightedMoves();
    }

    public void makeClientMove(int end) {
        // moves the selected piece to the given end position
        // moving player is always client

        // check a piece is selected to move from
        if (selectedPoint == -1) return;

        // check the end point is valid
        if (!highlightedMoves.contains(end)) return;

        // add move to turn stack
        turnStack.push(new int[]{selectedPoint, end});
        boardStack.push(new Board(clientBoard));

        // select appropriate method for bear off / enter
        if (end == 0) bearOffClient();
        else if (selectedPoint == 25) enterClient(end);
        else moveClient(end);

        // update highlighted points
        updateHighlightedMoves();
        selectedPoint = -1;
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

        if (start == 0 || end == 25) return;

        // flip for opponent moves
        if (player == Player.Opponent) {
            if (start < 25) start = 25 - start;
            if (end > 0) end = 25 - end;
        }

        // select appropriate method for bear off / enter
        if (end == 0) bearOffServer(start, player);
        else if (start == 25) enterServer(end, player);
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


    public void selectDomino(int side1, int side2) {
        // deselects previous domino & selects new domino

        // only works on correct turn
        if (currentPlayer != Player.Client) return;

        if (side1 == side2) selectDomino(side1);
        else {
            // undoes any previously made moves
            if (selectedDouble == null)
                while(!turnStack.isEmpty())
                    undoMove();

            if (!clientHand.isDominoAvailable(side1,side2)) return;
            if (selectedDomino != null) {
                selectedDomino.deselect();
                // if domino was previously selected, just deselect it
                if (!(selectedDomino.getSide1() == side1 && selectedDomino.getSide2() == side2))
                    selectedDomino = clientHand.selectDomino(side1, side2);
                else selectedDomino = null;
            } else {
                selectedDomino = clientHand.selectDomino(side1, side2);
            }
        }

        selectedPoint = -1;
        updateHighlightedMoves();
    }

    public void selectDomino(int val) {
        // deselects previous double & selects new double

        // undoes any previously made moves
        while(!turnStack.isEmpty())
            undoMove();

        if (!clientHand.isDoubleAvailable(val)) return;
        if (selectedDouble != null) {
            selectedDouble.deselect();
            // if domino was previously selected, just deselect it
            if (selectedDouble.getSide1() != val)
                selectedDouble = clientHand.selectDouble(val);
            else selectedDouble = null;
        } else {
            selectedDouble = clientHand.selectDouble(val);
        }
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
        } else {
            opponentHand.useDomino(side1, side2);
            opponentHand.useDouble(val);
        }
    }


    public void swapHands() {
        // swaps player hands according to server instruction
        clientHand.swapDominoSet();
        opponentHand.swapDominoSet();
    }


    public String checksum() {
        // generates a checksum hash for the game state

        String checksum = "";
        Player white, black;
        // current player (needs to convert from client/opponent to white/black)
        if (getColour(currentPlayer) == BGColour.WHITE) {
            checksum += "w;;";
            white = currentPlayer;
            if (currentPlayer == Player.Client) black = Player.Opponent;
            else black = Player.Client;
        }
        else {
            checksum += "b;;";
            black = currentPlayer;
            if (currentPlayer == Player.Client) white = Player.Opponent;
            else white = Player.Client;
        }

        // white
        checksum += "w" + serverBoard.getPipCount(white) + ";";
        Hand whiteHand;
        if (white == Player.Client) whiteHand = clientHand;
        else whiteHand = opponentHand;
        for (Domino dom: whiteHand.getDominoes()) {
            if (dom == null) continue;
            checksum += dom.getSide1() + "" + dom.getSide2();
            if(dom.isUsed()) checksum += "u";
            else checksum += "a";
        }
        for (Domino dbl: whiteHand.getDoubles()) {
            if (dbl == null) continue;
            checksum += dbl.getSide1() + "" + dbl.getSide2();
            if(dbl.isUsed()) checksum += "u";
            else checksum += "a";
        }
        if (serverBoard.getOffCount(white) > 0)
            checksum += ";0x" + serverBoard.getOffCount(white);
        if (white == Player.Client)
            for (int i = 1; i < 25; i++) {
                Point p = serverBoard.getPoint(i);
                if (p.getPlayer() == white) {
                    checksum += ";" + i + "x" + p.getCount();
                }
            }
        else
            for (int i = 24; i > 0; i--) {
                Point p = serverBoard.getPoint(i);
                if (p.getPlayer() == white) {
                    checksum += ";" + (25-i) + "x" + p.getCount();
                }
            }
        if (serverBoard.getBarCount(white) > 0)
            checksum += ";25x" + serverBoard.getBarCount(white);

        checksum += ";;";

        // black
        checksum += "b" + serverBoard.getPipCount(black) + ";";
        Hand blackHand;
        if (black == Player.Client) blackHand = clientHand;
        else blackHand = opponentHand;
        for (Domino dom: blackHand.getDominoes()) {
            if (dom == null) continue;
            checksum += dom.getSide1() + "" + dom.getSide2();
            if(dom.isUsed()) checksum += "u";
            else checksum += "a";
        }
        for (Domino dbl: blackHand.getDoubles()) {
            if (dbl == null) continue;
            checksum += dbl.getSide1() + "" + dbl.getSide2();
            if(dbl.isUsed()) checksum += "u";
            else checksum += "a";
        }
        if (serverBoard.getOffCount(black) > 0)
            checksum += ";0x" + serverBoard.getOffCount(black);
        if (black == Player.Client)
            for (int i = 1; i < 25; i++) {
                Point p = serverBoard.getPoint(i);
                if (p.getPlayer() == black) {
                    checksum += ";" + i + "x" + p.getCount();
                }
            }
        else
            for (int i = 24; i > 0; i--) {
                Point p = serverBoard.getPoint(i);
                if (p.getPlayer() == black) {
                    checksum += ";" + (25-i) + "x" + p.getCount();
                }
            }
        if (serverBoard.getBarCount(black) > 0)
            checksum += ";25x" + serverBoard.getBarCount(black);

        return checksum;
    }


    public void generateValidMoves() {
        validMoves = new MoveTree();
        highlightedMoves = new ArrayList<>();

        if (currentPlayer != Player.Client) return;

        // add child for each usable domino
        for (Domino d : clientHand.getDominoes()) {
            if (d == null) continue;
            if (d.isAvailable()) {
                MoveTree domNode = validMoves.addChild(
                        new DominoNode(d.getSide1(), d.getSide2())
                );

                // expand node with moves for the current domino
                expandNode(domNode, d, clientBoard);

                // trim node to remove all invalid moves
                trimNode((DominoNode) domNode);
            }
        }

        // add child for next double if it is available
        Domino dbl = clientHand.getNextDouble();
        if (dbl.isAvailable()) {
            MoveTree dblNode = validMoves.addChild(
                    new DominoNode(dbl.getSide1(), dbl.getSide2())
            );

            // expand node with moves for the double
            expandNode(dblNode, dbl, clientBoard);

            // trim node to remove all invalid moves
            trimNode((DominoNode) dblNode);
        }

//        validMoves.print();
    }

    private void updateHighlightedMoves() {
        highlightedMoves.clear();

        // get domino tree node (double takes priority)
        Domino targetDomino;
        if (selectedDouble != null) targetDomino = selectedDouble;
        else if (selectedDomino != null) targetDomino = selectedDomino;
        else return;

        MoveTree currentNode = validMoves;
        for (MoveTree child: currentNode.getChildren()) {
            DominoNode d = (DominoNode)child;
            if (d.getSide1() == targetDomino.getSide1() && d.getSide2() == targetDomino.getSide2()) {
                currentNode = child;
                break;
            }
        }

        // traverse the turn stack to get to current state
        List<int[]> turnList = new ArrayList<>(turnStack);
        for (int[] turn: turnList) {
            for (MoveTree child: currentNode.getChildren()) {
                MoveNode m = (MoveNode)child;
                if (m.getStart() == turn[0] && m.getEnd() == turn[1]) {
                    currentNode = child;
                    break;
                }
            }
        }

        // if no point is selected, then highlight start points
        if (selectedPoint == -1) {
            for (MoveTree child: currentNode.getChildren()) {
                MoveNode m = (MoveNode)child;
                highlightedMoves.add((int) m.getStart());
            }
        }
        // if a point is selected, then highlight all end points that start from the selected point
        // if no point is selected, then highlight start points
        else {
            for (MoveTree child: currentNode.getChildren()) {
                MoveNode m = (MoveNode)child;
                if (m.getStart() == selectedPoint)
                    highlightedMoves.add((int) m.getEnd());
            }
        }

//        System.out.println(highlightedMoves);
    }


    private void expandNode(MoveTree node, Domino domino, Board board) {
        // add children to current node
        if (domino.isDouble()) generateDoubleMoves(domino, node, board);
        else generateDominoMoves(domino, node, board);

        // for each non-terminal child
        for (MoveTree child: node.getChildren()) {
            if (child.getMovesLeft() > 0) {
                // generate board after applying current child's move
                Board tempBoard = new Board(board);
                MoveNode move = (MoveNode)child;
                if (move.getStart() == 25) {
                    Point endPoint = tempBoard.getPoint(move.getEnd());
                    if (endPoint.getPlayer() == Player.Opponent && endPoint.isBlot()) {
                        tempBoard.hitPiece(move.getEnd(), Player.Opponent);
                    }
                    tempBoard.enterPiece(
                            move.getEnd(),
                            Player.Client
                    );
                } else if (move.getEnd() == 0)
                    tempBoard.bearOffPiece(
                            move.getStart(),
                            Player.Client
                    );
                else {
                    Point endPoint = tempBoard.getPoint(move.getEnd());
                    if (endPoint.getPlayer() == Player.Opponent && endPoint.isBlot()) {
                        tempBoard.hitPiece(move.getEnd(), Player.Opponent);
                    }
                    tempBoard.movePiece(
                            move.getStart(),
                            move.getEnd(),
                            Player.Client
                    );
                }
                // expand the child with the new board state
                expandNode(child, domino, tempBoard);
            }
        }
    }

    private void generateDominoMoves(Domino domino, MoveTree parent, Board board) {
        // adds child nodes to a node with all valid moves from the given board state
        int moves = parent.getMovesLeft() - 1;
        int dist = 0;

        // if possible, add the parent's distance to the current distance
        try {
            dist += ((MoveNode) parent).getDistance();
        } catch (Exception ignored) { }

        // if any pieces on bar, then only add moves which enter
        if (board.getBarCount(Player.Client) > 0) {
            // check if it's available to enter using side 1
            Point s1EnterPoint = board.getPoint(25 - domino.getSide1());
            if (s1EnterPoint.availableFor(Player.Client))
                parent.addChild(new MoveNode(
                        25,
                        25 - domino.getSide1(),
                        dist,
                        moves)
                );
            // check if it's available to enter using side 2
            Point s2EnterPoint = board.getPoint(25 - domino.getSide2());
            if (s2EnterPoint.availableFor(Player.Client))
                parent.addChild(new MoveNode(
                        25,
                        25 - domino.getSide2(),
                        dist,
                        moves)
                );

            return;
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
            if (i <= 6 && highestPoint <= 6) {
                // exact bear off
                if (i == domino.getSide1() || i == domino.getSide2())
                    parent.addChild(new MoveNode(
                            i,
                            0,
                            dist,
                            moves)
                    );
                // everything higher already borne off: side 1
                if (i < domino.getSide1())
                    // specify a distance for these nodes as domino side is not the same as start - end
                    if (i == highestPoint)
                        parent.addChild(new MoveNode(
                                i,
                                0,
                                domino.getSide1(),
                                dist,
                                moves)
                        );
                // everything higher already borne off: side 2
                if (i < domino.getSide2())
                    if (i == highestPoint)
                        parent.addChild(new MoveNode(
                                i,
                                0,
                                domino.getSide2(),
                                dist,
                                moves)
                        );
            }

            // regular moves: side 1
            if (i > domino.getSide1()) {
                Point s1EndPoint = board.getPoint(i - domino.getSide1());
                if (s1EndPoint.availableFor(Player.Client))
                    parent.addChild(new MoveNode(
                            i,
                            i - domino.getSide1(),
                            dist,
                            moves)
                    );
            }
            // regular moves: side 2
            if (i > domino.getSide2()) {
                Point s2EndPoint = board.getPoint(i - domino.getSide2());
                if (s2EndPoint.availableFor(Player.Client))
                    parent.addChild(new MoveNode(
                            i,
                            i - domino.getSide2(),
                            dist,
                            moves)
                    );
            }
        }
    }

    private void generateDoubleMoves(Domino domino, MoveTree parent, Board board) {
        // adds child nodes to a domino node with all valid moves from the given board state
        // only checks one sde of the domino to avoid duplicates for doubles
        int moves = parent.getMovesLeft() - 1;
        int dist = 0;

        // if any pieces on bar, then only add moves which enter
        if (board.getBarCount(Player.Client) > 0) {
            // check if it's available to enter
            Point s1EnterPoint = board.getPoint(25 - domino.getSide1());
            if (s1EnterPoint.availableFor(Player.Client))
                parent.addChild(new MoveNode(
                        25,
                        25 - domino.getSide1(),
                        dist,
                        moves)
                );

            return;
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
            if (i <= 6 && highestPoint <= 6) {
                // exact bear off
                if (i == domino.getSide1())
                    parent.addChild(new MoveNode(
                            i,
                            0,
                            dist,
                            moves)
                    );
                // everything higher already borne off
                if (i < domino.getSide1())
                    // specify a distance for these nodes as domino side is not the same as start - end
                    if (i == highestPoint)
                        parent.addChild(new MoveNode(
                                i,
                                0,
                                domino.getSide1(),
                                dist,
                                moves)
                        );
            }

            // regular moves
            if (i > domino.getSide1()) {
                Point endPoint = board.getPoint(i - domino.getSide1());
                if (endPoint.availableFor(Player.Client))
                    parent.addChild(new MoveNode(
                            i,
                            i - domino.getSide1(),
                            dist,
                            moves)
                    );
            }
        }
    }


    private void trimNode(DominoNode node) {
        // removes all nodes that don't lead to a terminal with the correct total distance
        // total distance is only correct if correct no. of moves are made
        // AND correct domino sides are used (i.e. each side used at most once, largest possible side used)
        List<Integer> targets;

        // for a non-double, there are only 4 acceptable target distances that use each side at most once:
        // domino total, larger side, smaller side, or 0
        if (node.getSide1() != node.getSide2()) {
            targets = Arrays.asList(
                    (node.getSide1() + node.getSide2()),
                    Math.max(node.getSide1(), node.getSide2()),
                    Math.min(node.getSide1(), node.getSide2()),
                    0
            );
        }

        // for a double, there are 5 acceptable total distances:
        // domino total, double val x 3, double val x 2, double val x 1, or 0
        else {
            targets = Arrays.asList(
                    (node.getSide1() * 4),
                    (node.getSide1() * 3),
                    (node.getSide1() * 2),
                    (int)node.getSide1(),
                    0
            );
        }

        // remove nodes whose distance is none of the acceptable targets
        trimNode(node, targets);

        // find the largest total distance from the remaining nodes
        int target = largestDistance(node);
        // remove the rest of the nodes that don't have the optimal distance
        trimNode(node, Collections.singletonList(target));
    }

    private boolean trimNode(MoveTree node, List<Integer> targetDists) {
        // removes invalid moves

        // trim all children of the current node
        ArrayList<MoveTree> toTrim = new ArrayList<>();
        for (MoveTree child : node.getChildren())
            if (trimNode(child, targetDists))
                toTrim.add(child);
        node.getChildren().removeAll(toTrim);

        // the current node can only be removed if it now has no children
        if (node.getChildren().isEmpty()) {
            // remove node if total distance is not a valid target distance
            try {
                int dist = ((MoveNode) node).getDistance();
                return !targetDists.contains(dist);
            } catch (Exception ignored) {
                // Exception just means this node is a domino node
                // i.e. doesn't have a distance property
            }
        }
        return false;
    }

    private int largestDistance(MoveTree node) {
        // recursive depth-first search for max terminal value

        // if node has no children, return node's distance
        if (node.getChildren().isEmpty())
            try {
                return ((MoveNode) node).getDistance();
            } catch (Exception ignored) {
                // ignored exception just means a domino node has no children
            }

        int maxDist = 0;
        // finds the largest distance from all children of this node
        for (MoveTree child: node.getChildren()) {
            int dist = largestDistance(child);
            maxDist = Math.max(maxDist, dist);
        }
        return maxDist;
    }
}