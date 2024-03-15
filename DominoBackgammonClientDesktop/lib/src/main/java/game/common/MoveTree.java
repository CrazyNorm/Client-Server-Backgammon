package game.common;

import game.dominoes.Domino;

import java.util.ArrayList;


// Nodes for valid move tree
// Potentially very large amount of nodes in the tree
// Attributes use bytes instead of ints to keep memory use down

public class MoveTree {
    protected byte movesLeft; // at most 4, so byte is plenty
    protected ArrayList<MoveTree> children = new ArrayList<>();

    public MoveTree addChild(MoveTree child) {
        children.add(child);
        return child;
    }

    public ArrayList<MoveTree> getChildren() {
        return children;
    }

    public byte getMovesLeft() {
        return movesLeft;
    }

    public void print() {
        System.out.println(this);
        for (MoveTree c: children) {
            c.print();
        }
    }
    @Override
    public String toString() {
        return "root:   " + children.size() + " children";
    }
}

class DominoNode extends MoveTree {
    // sides are 1-6 so byte is plenty
    private final byte side1;
    private final byte side2;

    public DominoNode(int side1, int side2) {
        this.side1 = (byte)side1;
        this.side2 = (byte)side2;

        if (side1 == side2) this.movesLeft = 4;
        else this.movesLeft = 2;
    }

    public byte getSide1() {
        return side1;
    }
    public byte getSide2() {
        return side2;
    }

    public Domino getDomino() {
        return new Domino(side1, side2);
    }

    @Override
    public String toString() {
        return "\tdomino:   " + side1 + "," + side2 + "   " + children.size() + " children";
    }
}

class MoveNode extends MoveTree {
    // start & end are 0-25 so byte is plenty
    private final byte start;
    private final byte end;
    private final byte dist;

    public MoveNode(int start, int end, int curDist, int movesLeft) {
        this.start = (byte)start;
        this.end = (byte)end;
        this.dist = (byte)(curDist + start - end);
        this.movesLeft = (byte)movesLeft;
    }

    public MoveNode(int start, int end, int dist, int curDist, int movesLeft) {
        this.start = (byte)start;
        this.end = (byte)end;
        this.dist = (byte)(curDist + dist);
        this.movesLeft = (byte)movesLeft;
    }

    public byte getStart() {
        return start;
    }
    public byte getEnd() {
        return end;
    }
    public byte getDistance() {
        return dist;
    }

    @Override
    public String toString() {
        return "\t\tmove:   " + start + "->" + end + "   " + movesLeft + " left   " + children.size() + " children";
    }
}
