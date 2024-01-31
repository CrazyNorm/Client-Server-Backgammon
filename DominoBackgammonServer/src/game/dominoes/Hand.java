package game.dominoes;

import game.common.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hand {

    private Domino[] dominoes;
    private final Domino[] doubles;
    private int nextDouble;
    private int remaining;
    private int dominoSet;
    private final Player player;


    public Hand(Player player, int set) {
        this.dominoes = DominoSet.getSet(set);
        this.doubles = DominoSet.getDoubles(set);

        this.nextDouble = 0;
        // init remaining (9-1=8 / 1-2=7)
        this.remaining = 9 - set;
        this.dominoSet = set;
        this.player = player;
    }


    public boolean hasDomino(int side1, int side2) {
        return (findDomino(side1, side2) != -1);
    }
    public boolean isDominoAvailable(int side1, int side2) {
        int index = findDomino(side1, side2);
        if (index == -1) return false;
        return dominoes[index].isAvailable();
    }
    public void useDomino(int side1, int side2) {
        int index = findDomino(side1, side2);
        if (index != -1) {
            dominoes[index].use();
            remaining--;
        }
    }
    public void swapDominoSet() {
        // swap between 1 & 2
        this.dominoSet = 3 - this.dominoSet;
        // reinitialize dominoes (not doubles)
        this.dominoes = DominoSet.getSet(this.dominoSet );
        // reset remaining (9-1=8 / 1-2=7)
        this.remaining = 9 - this.dominoSet;
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


    public boolean hasDouble(int val) {
        return (findDouble(val) != -1);
    }
    public boolean isDoubleAvailable(int val) {
        int index = findDouble(val);
        if (index == -1) return false;
        return doubles[index].isAvailable();
    }
    public boolean isNextDouble(int val) {
        return (findDouble(val) == nextDouble);
    }
    public void useDouble(int val) {
        int index = findDouble(val);
        if (index != -1) {
            doubles[index].use();
            nextDouble++;
        }
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


    public int getRemaining() {
        return remaining;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Domino> getDominoes() {
        List<Domino> allDominoes = new ArrayList<>();
        Collections.addAll(allDominoes, dominoes);
        Collections.addAll(allDominoes, doubles);
        return allDominoes;
    }

    public int getDominoSet() {
        return dominoSet;
    }
}
