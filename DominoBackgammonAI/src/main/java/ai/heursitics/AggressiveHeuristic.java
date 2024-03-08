package ai.heursitics;

import game.Game;

public class AggressiveHeuristic extends Heuristic {
    @Override
    public double evaluate(Game game) {
        // aggressive heuristic - favours hitting ... todo
        byte player = game.getPlayer();
        return game.checkBar(player) * player;
    }
}
