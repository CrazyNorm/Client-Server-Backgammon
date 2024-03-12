package ai.heursitics;

import game.Game;

public abstract class Heuristic {

    // evaluates the game state for the given player (+ve = good for black, -ve = good for white)
    public abstract double evaluate(Game game, byte player);



    // features:

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

    protected int getBlots(Game game, byte player) {
        int blots = 0;
        for (int i = 1; i < 25; i++)
            if (game.checkPoint(i) * player == 1) blots++;
        return blots;
    }

    protected int getReachableBlots(Game game, byte player) {
        // counts the number of blots reachable by the opponent on the next turn
        byte opponent = (byte) -player;
        int blots = 0;
        for (int i = 1; i < 25; i++)
            if (game.checkPoint(i) * player == 1){
                boolean reachable = false;
                // checks +side1, +side2 & +both for every opponent domino
                for (byte[] dom: game.getAvailableDominoes(opponent)) {
                    if (dom[0] == dom[1]) {
                        reachable = true;
                        int ind = i + dom[0] * opponent;
                        if (ind > 0 && ind < 25 && game.checkPoint(ind) * player > 0) break;
                        ind += dom[0] * opponent;
                        if (ind > 0 && ind < 25 && game.checkPoint(ind) * player > 0) break;
                        ind += dom[0] * opponent;
                        if (ind > 0 && ind < 25 && game.checkPoint(ind) * player > 0) break;
                        ind += dom[0] * opponent;
                        if (ind > 0 && ind < 25 && game.checkPoint(ind) * player > 0) break;
                        reachable = false;
                    } else {
                        reachable = true;
                        int ind = i + dom[0] * opponent;
                        if (ind > 0 && ind < 25 && game.checkPoint(ind) * player > 0) break;
                        ind = i + dom[1] * opponent;
                        if (ind > 0 && ind < 25 && game.checkPoint(ind) * player > 0) break;
                        ind += dom[0] * opponent;
                        if (ind > 0 && ind < 25 && game.checkPoint(ind) * player > 0) break;
                        reachable = false;
                    }
                }
                if (reachable) blots++;
            }
        return blots;
    }

    protected int getRemainingDoubles(Game game, byte player) {
        int doubles = 0;
        if (!game.isSwapped()) doubles = 3;
        else if (player == -1) {
            if (game.checkDomino(0, player)) doubles = 3;
            if (game.checkDomino(2, player)) doubles = 2;
            if (game.checkDomino(5, player)) doubles = 1;
        } else {
            if (game.checkDomino(1, player)) doubles = 3;
            if (game.checkDomino(3, player)) doubles = 2;
            if (game.checkDomino(4, player)) doubles = 1;
        }
        return doubles;
    }

    protected double getNormalisedDoubles(Game game, byte player) {
        // normalises remaining doubles against pip count
        // (i.e. saving doubles is worth less as the game progresses)

        double doubles = getRemainingDoubles(game, player);
        double pipCount = game.getPipCount(player);

        return (doubles * pipCount / 167); // (167 = pip count at start of game)
    }

    protected int getUnusableDominoes(Game game, byte player) {
        // counts how many of the given player's dominoes will be unusable on their next turn

        Game tempGame = new Game(game);
        tempGame.nextTurn();
        // not perfect - assumes the other player won't make any moves
        if (tempGame.getPlayer() != player) tempGame.nextTurn();

        int unusable = 0;
        for (byte[] dom: tempGame.getAvailableDominoes(player))
            if (tempGame.findMoves(dom).isEmpty()) unusable++;

        return unusable;
    }

    protected int getHalfUsedDominoes(Game game, byte player) {
        // similar to unused dominoes, but counts dominoes that can't be fully used
        // (e.g. only using one side or using 3 moves on a double)

        Game tempGame = new Game(game);
        tempGame.nextTurn();
        // not perfect - assumes the other player won't make any moves
        if (tempGame.getPlayer() != player) tempGame.nextTurn();

        int unusable = 0;
        for (byte[] dom: tempGame.getAvailableDominoes(player)) {
            boolean halfUsed = true;
            for (byte[] m: tempGame.findMoves(dom)) {
                if (dom[0] == dom[1] && m.length == 8) halfUsed = false;
                else if (dom[0] != dom[1] && m.length == 4) halfUsed = false;

                if (!halfUsed) {
                    unusable++;
                    break;
                }
            }
        }

        return unusable;
    }

    protected int getPipDiff(Game game) {
        // returns difference between players' pip counts (+ve means black higher, -ve means white higher)
        // used to determine when a game is going REALLY bad (switch to damage limitation?)

        return (game.getPipCount((byte) 1) - game.getPipCount((byte) -1));
    }

    protected int getBackgammonPieces(Game game, byte player) {
        // counts the pieces in the opponent's home board (used to avoid backgammon)

        int pieces = 0;
        int i;
        if (player == 1) {
            for (i = 1; i < 7; i++)
                if (game.checkPoint(i) > 0) pieces += game.checkPoint(i);
        }
        else {
            for (i = 19; i < 25; i++)
                if (game.checkPoint(i) < 0) pieces -= game.checkPoint(i);
        }
        return pieces;
    }

    protected int getGammonPieces(Game game, byte player) {
        // counts the pieces not in the player's home board (used to bear off quicker - avoid gammon)

        int pieces = 0;
        int i;
        if (player == 1) {
            for (i = 1; i < 19; i++)
                if (game.checkPoint(i) > 0) pieces += game.checkPoint(i);
        }
        else {
            for (i = 7; i < 25; i++)
                if (game.checkPoint(i) < 0) pieces -= game.checkPoint(i);
        }
        return pieces;
    }
}
