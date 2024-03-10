package ai.heursitics;

import game.Game;

public class AggressiveHeuristic extends Heuristic {

    static double hitWeight = 3;
    static double blockedEntryWeight = 2;
    static double blockedPointWeight = 1;

    @Override
    public double evaluate(Game game, byte player) {
        // aggressive heuristic - favours hitting ... todo
        byte opponent = (byte) -player;

        double hitCount = game.checkBar(opponent);
        double blockedEntry = getBlockedEntry(game, player);
        double blockedPoints = getBlockedPoints(game, player) - blockedEntry;

        return player * (hitWeight * hitCount + blockedEntryWeight * blockedEntry
                + blockedPointWeight * blockedPoints);
    }
}
