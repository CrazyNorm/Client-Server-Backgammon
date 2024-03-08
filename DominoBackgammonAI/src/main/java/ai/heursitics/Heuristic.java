package ai.heursitics;

import game.Game;

public abstract class Heuristic {

    // evaluates the game state (+ve = good for black, -ve = good for white)
    public abstract double evaluate(Game game);
}
