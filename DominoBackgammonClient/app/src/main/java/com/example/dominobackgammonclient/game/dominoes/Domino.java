package com.example.dominobackgammonclient.game.dominoes;

public class Domino {

    private final int side1;
    private final int side2;
    private DominoState state;


    public Domino(int side1, int side2) {
        this.side1 = side1;
        this.side2 = side2;
        this.state = DominoState.Available;
    }


    public int getSide1() {
        return side1;
    }
    public int getSide2() {
        return side2;
    }

    public boolean isAvailable() {
        return (state == DominoState.Available);
    }
    public boolean isSelected() {
        return (state == DominoState.Selected);
    }
    public boolean isUsed() {
        return (state == DominoState.Used);
    }
    public boolean isBlocked() {
        return (state == DominoState.Blocked);
    }
    public void select() {
        state = DominoState.Selected;
    }
    public void deselect() {
        state = DominoState.Available;
    }
    public void use() {
        state = DominoState.Used;
    }
    public void block() {
        state = DominoState.Blocked;
    }
    public void unblock() {
        state = DominoState.Available;
    }

    public boolean isDouble() {
        return (side1 == side2);
    }

    @Override
    public boolean equals(Object o) {
        // used for unit tests

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Domino domino = (Domino) o;

        if (side1 != domino.side1) return false;
        if (side2 != domino.side2) return false;
        return state == domino.state;
    }
}

