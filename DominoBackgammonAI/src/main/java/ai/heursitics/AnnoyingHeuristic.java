package ai.heursitics;

import game.Game;

public class AnnoyingHeuristic extends Heuristic {

    private static final double unreachableBlotWeight = 3;
    private static final double blockedEntryWeight = 2;
    private static final double restrictedDominoWeight = 1;
    private static final double limitedDominoWeight = 1;
    private static final double borneOffWeight = -100;

    @Override
    public double evaluate(Game game, byte player) {
        // 'annoying' heuristic - favours unreachable blocks, restricting opponent choices & blocking entry
        // doesn't care about pip counts - not necessarily trying to win, just piss you off

        // if in bearoff stage, only care about most borne off
        // main heuristic massively discourages bearing off until this point
        if (isBearOff(game)) return game.checkBorneOff(player);

        byte opponent = (byte) -player;

        double unreachableBlots = getBlots(game, player) - getReachableBlots(game, player);
        double blockedEntry = getBlockedEntry(game, player);
        double restrictedDominoes = getUnusableDominoes(game, opponent); // can't make any moves
        double limitedDominoes = getHalfUsedDominoes(game, opponent); // can't make max moves

        return player * (
                unreachableBlotWeight * unreachableBlots
                + blockedEntryWeight * blockedEntry
                + restrictedDominoWeight * restrictedDominoes
                + limitedDominoWeight * limitedDominoes
                + borneOffWeight * game.checkBorneOff(player)
        );
    }
}
