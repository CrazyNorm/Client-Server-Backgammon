package com.example.dominobackgammonclient.game.board;

import com.example.dominobackgammonclient.game.common.Player;

public class Board {

    private final int POINT_COUNT = 24;
    private final Point[] points;

    // arrays of counters for each player [white, black]
    private final int[] offBoard;
    private final int[] bar;
    private final int[] pipCounts;


    public Board() {
        this.points = new Point[POINT_COUNT];
        // initialise all points to empty
        for (int i = 0; i < POINT_COUNT; i++) {
            this.points[i] = new Point();
        }
        // manually initialise points to construct starting layout
        this.points[0] = new Point(2, Player.Opponent);
        this.points[5] = new Point(5, Player.Client);
        this.points[7] = new Point(3, Player.Client);
        this.points[11] = new Point(5, Player.Opponent);
        this.points[12] = new Point(5, Player.Client);
        this.points[16] = new Point(3, Player.Opponent);
        this.points[18] = new Point(5, Player.Opponent);
        this.points[23] = new Point(2, Player.Client);

        // initialise counter arrays
        int PLAYER_COUNT = 2;
        this.offBoard = new int[PLAYER_COUNT];
        this.bar = new int[PLAYER_COUNT];
        this.pipCounts = new int[PLAYER_COUNT];
        updatePipCount();
    }

    public Board(int[] cIndex, int[] oIndex) {
        // constructs board from a list of piece position indices for each player
        // assumes index lists describe a valid board layout (e.g. only 1 player per point)
        // indices are relative to player (i.e. home board is always 1-6)

        this.points = new Point[POINT_COUNT];
        // initialise all points to empty
        for (int i = 0; i < POINT_COUNT; i++) {
            this.points[i] = new Point();
        }

        // initialise counter arrays
        int PLAYER_COUNT = 2;
        this.offBoard = new int[PLAYER_COUNT];
        this.bar = new int[PLAYER_COUNT];
        this.pipCounts = new int[PLAYER_COUNT];

        // add pieces at each position
        for (int i = 0; i < cIndex.length; i++) {
            // list 1: client pieces
            if (cIndex[i] == 0) this.offBoard[0]++;
            else if (cIndex[i] == 25) this.bar[0]++;
            else {
                Point p = this.points[cIndex[i] - 1];
                p.setPlayer(Player.Client);
                p.addPiece();
            }

            // list 2: opponent pieces
            if (oIndex[i] == 0) this.offBoard[1]++;
            else if (oIndex[i] == 25) this.bar[1]++;
            else {
                Point p = this.points[24 - oIndex[i]];
                p.setPlayer(Player.Opponent);
                p.addPiece();
            }
        }

        updatePipCount();
    }


    public Point getPoint(int p) {
        return points[p - 1];
    }

    public int getBarCount(Player player) {
        if (player == Player.Client) {
            return bar[0];
        }
        return bar[1];
    }

    public int getPipCount(Player player) {
        if (player == Player.Client) {
            return pipCounts[0];
        }
        return pipCounts[1];
    }

    public int getOffCount(Player player) {
        if (player == Player.Client) {
            return offBoard[0];
        }
        return offBoard[1];
    }


    public void movePiece(
            int start,
            int end,
            Player player
    ) {
        // get start & end points
        Point sPoint = points[start - 1];
        Point ePoint = points[end - 1];

        // remove piece from start (remove player if open)
        sPoint.removePiece();
        if (sPoint.isOpen()) sPoint.setPlayer(null);

        // add piece to end (ensure player is correct)
        ePoint.setPlayer(player);
        ePoint.addPiece();

        updatePipCount();
    }

    public void bearOffPiece(
            int start,
            Player player
    ) {
        // get point to bear off from
        Point sPoint = points[start - 1];

        // remove piece from start (remove player if open)
        sPoint.removePiece();
        if (sPoint.isOpen()) sPoint.setPlayer(null);

        // take piece off board
        if (player == Player.Client) offBoard[0]++;
        else offBoard[1]++;

        updatePipCount();
    }

    public void enterPiece(
            int end,
            Player player
    ) {
        // get point to enter onto
        Point ePoint = points[end - 1];

        // remove piece from bar
        if (player == Player.Client) bar[0]--;
        else bar[1]--;

        // add piece to end (ensure player is correct)
        ePoint.setPlayer(player);
        ePoint.addPiece();

        updatePipCount();
    }

    public void hitPiece(
            int start,
            Player player
    ) {
        // get point being hit
        Point sPoint = points[start - 1];

        // remove piece from start (remove player if open)
        sPoint.removePiece();
        if (sPoint.isOpen()) sPoint.setPlayer(null);

        // add piece to bar
        if (player == Player.Client) bar[0]++;
        else bar[1]++;

        updatePipCount();
    }


    private void updatePipCount() {
        pipCounts[0] = 0;
        pipCounts[1] = 0;

        // count each players' pip counts by looping through the points
        // for each point, increase pip count by no. of pieces on the point x point number
        for (int i = 0; i < POINT_COUNT; i++) {
            Point p = points[i];
            if (p.getPlayer() == Player.Client) {
                pipCounts[0] += p.getCount() * (i + 1);
            }
            if (p.getPlayer() == Player.Opponent) {
                pipCounts[1] += p.getCount() * (POINT_COUNT - i);
            }
        }

        // account for pieces on the bar in the pip count
        pipCounts[0] += bar[0] * (POINT_COUNT + 1);
        pipCounts[1] += bar[1] * (POINT_COUNT + 1);
    }
}


