package game;

public class Game {
    // streamlined game representation
    // each game instance uses 51 bytes (plus object overheads?)
    // (plus 36 static bytes)

    private final byte[] points;
    private final byte[] bar;
    private final byte[] off;

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

    private byte player; // 1 = white, -1 = black
    private byte whiteSet;


    public Game(byte player) {
        this.points = new byte[24];
        for (byte p: points) p = 0;
        this.bar = new byte[] {0, 0};
        this.off = new byte[] {0, 0};

        this.dominoes = new byte[21];
        for (byte d: dominoes) d = 0;
        this.player = player;
        this.whiteSet = 0;
    }

    public Game(Game oldGame) {
        // deep copy constructor
        this.points = oldGame.points.clone();
        this.bar = oldGame.bar.clone();
        this.off = oldGame.off.clone();

        this.dominoes = oldGame.dominoes.clone();
        this.player = oldGame.player;
        this.whiteSet = oldGame.whiteSet;
    }


    public void addPieces(int[] indices, byte player) {
        for (int ind: indices) {
            if (ind == 0) off[ind] += player;
            else if (ind == 25) bar[ind] += player;
            else points[ind + 1] += player;
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
    }

    public byte getWhiteSet() {
        return whiteSet;
    }

    public void setWhiteSet(byte whiteSet) {
        this.whiteSet = whiteSet;
    }

    public boolean checkDomino(int index) {
        // checks if domino at given index is available for current player
        return (dominoes[index] == player);
    }

    public byte[] getDomino(int index) {
        // returns the value of the domino at the given index
        return dominoList[index];
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

        // checks for swapping domino sets
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
        }
    }


    public byte checkPoint(int index) {
        // checks how many pieces & what colour are at a given point
        return points[index - 1];
    }

    public void movePiece(int start, int end) {
        // moves a piece from start to end for the current player
        // assumes validity is already checked

        // check for enter
        if (start == 25) {
            if (player == 1) bar[0]--;
            else bar[1]--;
        }
        else dominoes[start] -= player;

        // checks for bear off
        if (end == 0) {
            if (player == 1) off[0]++;
            else off[1]++;
        }
        else {
            // checks for hit
            if (dominoes[end] == -player) {
                dominoes[end] += player;
                if (player == 1) bar[1]++;
                else bar[0]++;
            }
            dominoes[end] += player;
        }
    }
}
