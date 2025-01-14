package game.dominoes;

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
    public boolean isUsed() {
        return (state == DominoState.Used);
    }
    public void use() {
        state = DominoState.Used;
    }

    public boolean isDouble() {
        return (side1 == side2);
    }
}
