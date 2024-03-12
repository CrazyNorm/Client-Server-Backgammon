package game;

import client.pojo.DominoPojo;
import client.pojo.HandPojo;
import client.pojo.PieceList;
import client.pojo.Reset;

import java.util.ArrayList;
import java.util.List;

public class Game {
    // streamlined game representation
    // each game instance uses 52 bytes (plus object overheads?)
    // (plus 36 static bytes)

    private final byte[] points;
    private final byte[] bar;  // black, white
    private final byte[] off;  // black, white

    private final byte[] dominoes; // 1 for white, -1 for black, 0 for neither
    private final static byte[][] dominoList = new byte[][] {
            {1,1}, {2,2}, {3,3}, {4,4}, {5,5}, {6,6},  // doubles
            {6,5}, {6,4}, {6,3}, {6,2}, {6,1},         // 6's
            {5,4}, {5,3}, {5,2}, {5,1},                // 5's
            {4,3}, {4,2}, {4,1},                       // 4's
            {3,2}, {3,1},                              // 3's
            {2,1}                                      // 2's
    };
    private final static byte[][] dominoSets = new byte[][] {
            {7, 9, 11, 12, 14, 16, 19, 20},
            {6, 8, 10, 13, 15, 17, 18}
    };

    private byte player; // 1 = black, -1 = white
    private byte whiteSet;
    private byte turnCount;
    private boolean swapped;


    public Game(byte player) {
        this.points = new byte[24];
        for (byte p: points) p = 0;
        this.bar = new byte[] {0, 0};
        this.off = new byte[] {0, 0};

        this.dominoes = new byte[21];
        for (byte d: dominoes) d = 0;
        this.player = player;
        this.whiteSet = 0;
        this.turnCount = 1;
        this.swapped = false;
    }

    public Game(Game oldGame) {
        // deep copy constructor
        this.points = oldGame.points.clone();
        this.bar = oldGame.bar.clone();
        this.off = oldGame.off.clone();

        this.dominoes = oldGame.dominoes.clone();
        this.player = oldGame.player;
        this.whiteSet = oldGame.whiteSet;
        this.turnCount = oldGame.turnCount;
        this.swapped = oldGame.swapped;
    }

    public static Game gameFromReset(Reset reset) {
        // player
        byte player = 1;
        if (reset.getPlayer() == Player.White) player = -1;
        Game game = new Game(player);

        // turn count
        game.setTurnCount((byte) reset.getTurnCount());
        game.setSwapped(reset.isSwapped());

        // pieces
        for (PieceList pl: reset.getPieces()) {
            byte piecePlayer = 1;
            if (pl.getColour() == Player.White) piecePlayer = -1;
            int[] indices = new int[pl.getIndices().size()];
            for (int i = 0; i < indices.length; i++)
                indices[i] = pl.getIndices().get(i);
            game.addPieces(indices, piecePlayer);
        }

        // dominoes
        for (HandPojo h: reset.getHands()) {
            byte handPlayer = 1;
            if (h.getColour() == Player.White) {
                handPlayer = -1;
                game.setWhiteSet((byte) h.getSet());
            }
            for (DominoPojo dom: h.getDominoes()) {
                if (dom.getSide1() == dom.getSide2() && !reset.isSwapped()) continue;
                if (dom.isAvailable()) game.addDomino(dom.getSide1(), dom.getSide2(), handPlayer);
            }
            // remove extra doubles
            if (game.swapped) {
                if (h.getColour() == Player.White) {
                    if (game.dominoes[2] == -1) game.dominoes[5] = 0;
                    if (game.dominoes[0] == -1) game.dominoes[2] = 0;
                } else {
                    if (game.dominoes[3] == 1) game.dominoes[4] = 0;
                    if (game.dominoes[1] == 1) game.dominoes[3] = 0;
                }
            }
            else
                for (int i = 0; i < 6; i++) game.dominoes[i] = 0;
        }

        return game;
    }


    public void addPieces(int[] indices, byte player) {
        for (int ind: indices) {
            if (player == 1 && ind != 0 && ind != 25) ind = 25 - ind;
            if (ind == 0) {
                if (player == 1) off[0]++;
                else off[1]++;
            }
            else if (ind == 25) {
                if (player == 1) bar[0]++;
                else bar[1]++;
            }
            else points[ind - 1] += player;
        }
    }

    public void addDomino(int s1, int s2, byte player) {
        // always at most 21 dominoes to search through for correct index
        for (int i = 0; i < dominoList.length; i++)
            if (dominoList[i][0] == s1 && dominoList[i][1] == s2) {
                dominoes[i] += player;
                break;
            }
    }

    public byte getPlayer() {
        return player;
    }

    public void setPlayer(byte player) {
        this.player = player;
    }

    public void nextTurn() {
        player *= -1;
        turnCount++;

        // check for swapping domino sets
        boolean swap = true;
        for (int i = 6; i < dominoes.length; i++) {
            if (dominoes[i] != 0) {
                swap = false;
                break;
            }
        }
        if (swap) {
            for (byte ind: dominoSets[whiteSet - 1]) dominoes[ind] = -1;
            whiteSet = (byte) (3 - whiteSet); // 3 - x swaps x between 1 & 2
            for (byte ind: dominoSets[whiteSet - 1]) dominoes[ind] = 1;
            if (!swapped) {
                swapped = true;
                dominoes[0] = -1;
                dominoes[1] = 1;
            }
        }
    }

    public byte getWhiteSet() {
        return whiteSet;
    }

    public void setWhiteSet(byte whiteSet) {
        this.whiteSet = whiteSet;
    }

    public byte getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(byte turnCount) {
        this.turnCount = turnCount;
    }

    public boolean isSwapped() {
        return swapped;
    }

    public void setSwapped(boolean swapped) {
        this.swapped = swapped;
    }

    public boolean checkDomino(int index) {
        // checks if domino at given index is available for current player
        return (dominoes[index] == player);
    }

    public boolean checkDomino(int index, byte player) {
        // checks if domino at given index is available for the given player
        return (dominoes[index] == player);
    }

    public static byte[] getDomino(int index) {
        // returns the value of the domino at the given index
        return dominoList[index];
    }

    public static int getDominoIndex(byte[] domino) {
        // finds the index for a given domino value
        for (int i = 0; i < dominoList.length; i++)
            if (dominoList[i] == domino) return i;
        return -1;
    }

    public List<byte[]> getAvailableDominoes(byte player) {
        // gets all dominoes available to be used by the given player
        List<byte[]> availableDominoes = new ArrayList<>();

        for (int i = 0; i < dominoes.length; i++)
            if (dominoes[i] == player) availableDominoes.add(dominoList[i]);

        return availableDominoes;
    }

    public void useDomino(int index) {
        // uses the domino at the given index
        dominoes[index] = 0;

        // checks for making next double available
        byte[] dom = dominoList[index];
        if (dom[0] == dom[1])
            switch (dom[0]) {
                case 1:
                    // use 1,1 -> make 3,3 available to white
                    dominoes[2] = player;
                    break;
                case 2:
                    // use 2,2 -> make 4,4 available to black
                    dominoes[3] = player;
                    break;
                case 3:
                    // use 3,3 -> make 6,6 available to white
                    dominoes[5] = player;
                    break;
                case 4:
                    // use 4,4 -> make 5,5 available to black
                    dominoes[4] = player;
                    break;
            }
    }

    public void useDomino(byte[] domino) {
        // uses the domino with the given value
        int index = Game.getDominoIndex(domino);
        useDomino(index);
    }


    public byte checkPoint(int index) {
        // checks how many pieces & what colour are at a given point
        return points[index - 1];
    }

    public byte checkBar(byte player) {
        // returns the number of pieces on the bar for the given player
        if (player == 1) return bar[0];
        return bar[1];
    }

    public byte checkBorneOff(byte player) {
        // returns the number of pieces borne off for the given player
        if (player == 1) return off[0];
        return off[1];
    }

    public byte checkWin() {
        // returns if either player has won (0 if neither)
        if (off[0] == 15) return 1;
        else if (off[1] == 15) return -1;
        return 0;
    }

    public void movePiece(int start, int end) {
        // moves a piece from start to end for the current player
        // assumes validity is already checked

        // check for enter
        if (start == 25) {
            if (player == 1) bar[0]--;
            else bar[1]--;
        }
        else points[start-1] -= player;

        // checks for bear off
        if (end == 0) {
            if (player == 1) off[0]++;
            else off[1]++;
        }
        else {
            // checks for hit
            if (points[end-1] == -player) {
                points[end-1] += player;
                if (player == 1) bar[1]++;
                else bar[0]++;
            }
            points[end-1] += player;
        }
    }


    public int getPipCount(byte player) {
        int pipCount = 0;
        if (player == 1) pipCount += bar[0] * 25;
        else pipCount += bar[1] * 25;
        for (int i = 0; i < points.length; i++)
            if (player == 1) {
                if (points[i] > 0) pipCount += (24 - i) * points[i];
            } else {
                if (points[i] < 0) pipCount += (i + 1) * points[i];
            }
        return pipCount;
    }



    public List<byte[]> findMoves(byte[] domino) {
        // finds all available moves using the given domino
        List<byte[]> moves = new ArrayList<>();

        // check for doubles
        if (domino[0] == domino[1]) {
            return findMovesDouble(domino[0]);
        }

        // side 1 first
        List<byte[]> s1Moves = findMoves(domino[0]);
        for (byte[] m1: s1Moves) {
            // apply move to temp game state
            Game tempGame = new Game(this);
            tempGame.movePiece(m1[0], m1[1]);
            // find moves for other domino side
            List<byte[]> s2Moves2 = tempGame.findMoves(domino[1]);
            for (byte[] m2: s2Moves2)
                moves.add(
                        new byte[] {m1[0], m1[1], m2[0], m2[1]}
                );
        }
        // side 2 first
        List<byte[]> s2Moves = findMoves(domino[1]);
        for (byte[] m1: s2Moves) {
            // apply move to temp game state
            Game tempGame = new Game(this);
            tempGame.movePiece(m1[0], m1[1]);
            // find moves for other domino side
            List<byte[]> s1Moves2 = tempGame.findMoves(domino[0]);
            for (byte[] m2: s1Moves2)
                moves.add(
                        new byte[] {m1[0], m1[1], m2[0], m2[1]}
                );
        }

        // if moves is empty, it is impossible to use both sides
        // add the moves that use just the larger side
        if (moves.isEmpty()) {
            if (domino[0] > domino[1]) moves.addAll(s1Moves);
            else moves.addAll(s2Moves);
        }

        // if still empty, it is impossible to use the larger side at all
        // add the moves that use just the smaller side
        if (moves.isEmpty()) {
            if (domino[0] > domino[1]) moves.addAll(s2Moves);
            else moves.addAll(s1Moves);
        }

        // if still empty, it is impossible to make any moves with this domino
        // so return the list still empty

        return moves;
    }

    private List<byte[]> findMovesDouble(byte dbl) {
        // finds all available moves using the given double
        List<byte[]> moves = new ArrayList<>();

        int longestChain = 0; // records longest chain of moves

        // 4x nested for loop :(
        // need to know result of applying 4 moves in order
        // could refactor to recursion?

        // finds set of 1st moves
        List<byte[]> dblMoves1 = findMoves(dbl);
        for (byte[] m1: dblMoves1) {
            // apply move to temp game state
            Game tempGame1 = new Game(this);
            tempGame1.movePiece(m1[0], m1[1]);

            // find next set of moves
            List<byte[]> dblMoves2 = tempGame1.findMoves(dbl);
            if (dblMoves2.isEmpty() && longestChain <= 1) {
                longestChain = 1;
                moves.add(
                        new byte[]{m1[0], m1[1]}
                );
            }
            for (byte[] m2: dblMoves2) {
                // apply move to temp game state
                Game tempGame2 = new Game(tempGame1);
                tempGame2.movePiece(m2[0], m2[1]);

                // find next set of moves
                List<byte[]> dblMoves3 = tempGame2.findMoves(dbl);
                if (dblMoves3.isEmpty() && longestChain <= 2) {
                    longestChain = 2;
                    moves.add(
                            new byte[]{m1[0], m1[1], m2[0], m2[1]}
                    );
                }
                for (byte[] m3 : dblMoves3) {
                    // apply move to temp game state
                    Game tempGame3 = new Game(tempGame2);
                    tempGame3.movePiece(m3[0], m3[1]);

                    // find next set of moves
                    List<byte[]> dblMoves4 = tempGame3.findMoves(dbl);
                    if (!dblMoves4.isEmpty()) longestChain = 4;
                    else if (longestChain <= 3) {
                        longestChain = 3;
                        moves.add(
                                new byte[]{m1[0], m1[1], m2[0], m2[1], m3[0], m3[1]}
                        );
                    }
                    for (byte[] m4 : dblMoves4)
                        moves.add(
                                new byte[]{m1[0], m1[1], m2[0], m2[1], m3[0], m3[1], m4[0], m4[1]}
                        );
                }
            }
        }

        // remove moves that are shorter than the longest chain
        int targetLen = longestChain * 2;
        moves.removeIf(x -> x.length < targetLen);


        return moves;
    }

    private List<byte[]> findMoves(byte dist) {
        // finds all available moves for a single distance
        List<byte[]> moves = new ArrayList<>();

        // check the bar
        byte barInd = 0;
        if (player == -1) barInd = 1;
        if (bar[barInd] > 0) {
            int entryInd;
            if (player == 1) entryInd = dist;
            else entryInd = 25 - dist;
            int entryPoint = points[entryInd - 1] * player;
            if (entryPoint >= -1) moves.add(
                    new byte[]{25, (byte) entryInd}
            );
        }
        else {
            // find normal moves
            int furthestPoint = -1;
            for (int i = 1; i < points.length + 1; i++) {
                if (player == 1 && points[i-1] > 0 && furthestPoint == -1) furthestPoint = i;
                else if (player == -1 && points[i-1] < 0) furthestPoint = i;

                // skip if point has none of the correct player's pieces
                if (points[i-1] * player < 1) continue;

                // skip if move would fall off board
                if (player == 1 && i + dist > 24) continue;
                if (player == -1 && i - dist < 1) continue;

                // skip if moving back men too early
                if (turnCount < 4) {
                    if (player == 1 && i == 1) continue;
                    if (player == -1 && i == 24) continue;
                }

                int endInd;
                if (player == 1) endInd = i + dist;
                else endInd = i - dist;
                int endPoint = points[endInd-1] * player;
                if (endPoint >= -1) moves.add(
                        new byte[] {(byte) i, (byte) endInd}
                );
            }

            // find bearing off moves
            if (player == 1 && furthestPoint > 18) {
                for (int i = 19; i < 25; i++) {
                    // skip if point has none of the correct player's pieces
                    if (points[i-1] * player < 1) continue;

                    // exact bear off
                    if (i + dist == 25) moves.add(
                            new byte[] {(byte) i, 0}
                    );
                    // bear off highest
                    else if (i + dist > 25 && i == furthestPoint) moves.add(
                            new byte[] {(byte) i, 0}
                    );
                }
            }
        }

        return moves;
    }
}
