package ai.heursitics;

import game.Game;

public class AggressiveHeuristic extends Heuristic {

    private static final double hitWeight = 3;
    private static final double blockedEntryWeight = 2;
    private static final double blockedPointWeight = 1;
    private static final double pipCountWeight = 1;

    @Override
    public double evaluate(Game game, byte player) {
        // aggressive heuristic - favours hitting, blocking points & blocking entry
        // also favours higher opponent pip count

        byte opponent = (byte) -player;

        double hitCount = game.checkBar(opponent);
        double blockedEntry = getBlockedEntry(game, player);
        double blockedPoints = getBlockedPoints(game, player) - blockedEntry;

        // represents opponent pip count as a ratio against the starting pip count
        double pipRatio = (double) game.getPipCount(opponent) / 167;

        return player * (hitWeight * hitCount + blockedEntryWeight * blockedEntry
                + blockedPointWeight * blockedPoints + pipCountWeight * pipRatio);
    }
}
