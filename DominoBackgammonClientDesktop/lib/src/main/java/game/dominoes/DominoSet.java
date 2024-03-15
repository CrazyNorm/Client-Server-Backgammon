package game.dominoes;

public class DominoSet {

    // Utility for defining arrays of dominoes, following sets described at:
    // https://www.bkgm.com/variants/DominoBackgammon.html

    public static Domino[] SET_1() {
        return new Domino[]{
                new Domino(2, 1),
                new Domino(3, 1),
                new Domino(4, 2),
                new Domino(5, 1),
                new Domino(5, 3),
                new Domino(5, 4),
                new Domino(6, 2),
                new Domino(6, 4)
        };
    }
    public static Domino[] SET_2() {
        return new Domino[]{
                new Domino(3, 2),
                new Domino(4, 1),
                new Domino(4, 3),
                new Domino(5, 2),
                new Domino(6, 1),
                new Domino(6, 3),
                new Domino(6, 5),
                null // to keep equal length with set 1
        };
    }

    public static Domino[] DOUBLES_1() {
        return new Domino[]{
                new Domino(1, 1),
                new Domino(3, 3),
                new Domino(6, 6)
        };
    }
    public static Domino[] DOUBLES_2() {
        return new Domino[]{
                new Domino(2, 2),
                new Domino(4, 4),
                new Domino(5, 5)
        };
    }


    public static Domino[] getSet(int set) {
        if (set == 1) return SET_1();
        else if (set == 2) return SET_2();
        else return new Domino[8];
    }

    public static Domino[] getDoubles(int set) {
        if (set == 1) return DOUBLES_1();
        else if (set == 2) return DOUBLES_2();
        else return new Domino[3];
    }
}


