package ai.heursitics;

import game.Game;

public abstract class Heuristic {

    // evaluates the game state for the given player (+ve = good for black, -ve = good for white)
    public abstract double evaluate(Game game, byte player);



    protected int getBlockedPoints(Game game, byte player) {
        int blocked = 0;
        for (int i = 1; i < 25; i++)
            if (game.checkPoint(i) * player >= 2) blocked++;
        return blocked;
    }

    protected int getBlockedEntry(Game game, byte player) {
        int blocked = 0;
        int i;
        if (player == 1) {
            for (i = 19; i < 25; i++)
                if (game.checkPoint(i) >= 2) blocked++;
        }
        else {
            for (i = 1; i < 7; i++)
                if (game.checkPoint(i) <= -2) blocked++;
        }
        return blocked;
    }
}
