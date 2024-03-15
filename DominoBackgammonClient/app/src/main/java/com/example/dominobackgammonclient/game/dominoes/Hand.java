package com.example.dominobackgammonclient.game.dominoes;

public class Hand {

    private Domino[] dominoes;
    private final Domino[] doubles;
    private int nextDouble;
    private int dominoSet;
    private boolean swapped;


    public Hand(int set) {
        this.dominoes = DominoSet.getSet(set);
        this.doubles = DominoSet.getDoubles(set);
        // block doubles until 1st swap
        for (Domino dbl: doubles)
            dbl.block();

        this.nextDouble = 0;
        this.dominoSet = set;
        this.swapped = false;
    }


    public Domino[] getDominoes() {
        return dominoes;
    }
    public boolean isDominoAvailable(int side1, int side2) {
        int index = findDomino(side1, side2);
        if (index != -1) {
            return (dominoes[index].isAvailable() || dominoes[index].isSelected());
        }
        return false;
    }
    public Domino selectDomino(int side1, int side2) {
        int index = findDomino(side1, side2);
        if (index != -1) {
            dominoes[index].select();
            return dominoes[index];
        }
        return null;
    }
    public void useDomino(int side1, int side2) {
        int index = findDomino(side1, side2);
        if (index != -1) {
            dominoes[index].use();
        }
    }
    public void swapDominoSet() {
        // swap between 1 & 2
        this.dominoSet = 3 - this.dominoSet;
        // reinitialize dominoes (not doubles)
        this.dominoes = DominoSet.getSet(this.dominoSet);
        // unblock 1st double on 1st swap
        if (!swapped) {
            swapped = true;
            getNextDouble().unblock();
        }
    }

    private int findDomino(int side1, int side2) {
        // Only 8 dominoes in a set, so linear search

        for (int i = 0; i < dominoes.length; i++) {
            if (dominoes[i] == null) continue;
            if (dominoes[i].getSide1() == side1
                    && dominoes[i].getSide2() == side2
            ) {
                return i;
            }
        }
        return -1; // rogue value: not found
    }


    public Domino[] getDoubles() {
        return doubles;
    }
    public boolean isDoubleAvailable(int val) {
        int index = findDouble(val);
        if (index != -1) {
            return (doubles[index].isAvailable() || doubles[index].isSelected());
        }
        return false;
    }
    public Domino getNextDouble() {
        return doubles[nextDouble];
    }
    public void useDouble(int val) {
        int index = findDouble(val);
        if (index != -1) {
            doubles[index].use();

            // unblock next double
            if (nextDouble < 2) {
                nextDouble++;
                getNextDouble().unblock();
            }
        }
    }
    public Domino selectDouble(int val) {
        int index = findDouble(val);
        if (index != -1) {
            doubles[index].select();
            return doubles[index];
        }
        return null;
    }

    private int findDouble(int val) {
        // Only 3 doubles in a set, so linear search

        for (int i = 0; i < doubles.length; i++) {
            if (doubles[i].getSide1() == val) {
                return i;
            }
        }
        return -1; // rogue value: not found
    }
}

