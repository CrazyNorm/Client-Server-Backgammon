package ai.heursitics;

import game.Game;

public class DefensiveHeuristic extends Heuristic {
    private static final double hitWeight = -3;
    private static final double reachableBlotWeight = -3;
    private static final double unreachableBlotWeight = -1;
    private static final double pipCountWeight = -0.5;
    private static final double borneOffWeight = 0.5;

    @Override
    public double evaluate(Game game, byte player) {
        // defensive heuristic - favours keeping pieces on board, preventing blots & reachability of blots
        // also favours lower player pip count

        // if in bearoff stage, only care about most borne off
        if (isBearOff(game)) return game.checkBorneOff(player);

        double hitCount = game.checkBar(player);
        double reachableBlots = getReachableBlots(game, player);
        double unreachableBlots = getBlots(game, player) - reachableBlots;

        // represents pip count as a ratio against the starting pip count
        double pipRatio = (double) game.getPipCount(player) / 167;

        return player * (hitWeight * hitCount + reachableBlotWeight * reachableBlots
                + unreachableBlotWeight * unreachableBlots + pipCountWeight * pipRatio
                + borneOffWeight * game.checkBorneOff(player));
    }
}
