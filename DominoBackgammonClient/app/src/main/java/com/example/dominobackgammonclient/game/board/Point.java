package com.example.dominobackgammonclient.game.board;


import com.example.dominobackgammonclient.game.common.Player;

public class Point {

    private int count;
    private Player player;
    private PointState state;


    public Point(int count, Player player) {
        this.count = count;
        this.player = player;

        // infer point state from starting count
        this.updateState();
    }

    public Point() {
        this.count = 0;
        this.player = null;
        this.state = PointState.Open;
    }


    public int getCount() {
        return count;
    }
    public void addPiece() {
        this.count++;
        updateState();
    }
    public void removePiece() {
        this.count--;
        updateState();
    }

    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isOpen() {
        return (this.state == PointState.Open);
    }
    public boolean isBlot() {
        return (this.state == PointState.Blot);
    }
    public boolean isClosed() {
        return (this.state == PointState.Closed);
    }
    private void updateState() {
        // infers the current point state from the piece count

        if (this.count >= 2) {
            this.state = PointState.Closed;
        }
        else if (this.count == 1) {
            this.state = PointState.Blot;
        }
        else {
            this.state = PointState.Open;
        }
    }
}
