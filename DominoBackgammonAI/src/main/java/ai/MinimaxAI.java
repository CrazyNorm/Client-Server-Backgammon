package ai;

import ai.heursitics.Heuristic;
import ai.heursitics.HeuristicFactory;
import client.pojo.TurnPojo;
import client.util.TurnFactory;
import game.Game;

import java.util.List;

public class MinimaxAI extends AI {

    private final Heuristic heuristic;
    private final int targetDepth; // how deep to search before evaluating with heuristic
    private final int searchTimeout; // timeout to give up search and just return best so far

    public MinimaxAI(String type) {
        this.heuristic = HeuristicFactory.getHeuristic(type);
        this.targetDepth = 2;
        this.searchTimeout = 10000;
    }

    @Override
    public TurnPojo chooseTurn(Game game) {
        // perform minimax search to choose domino(es) and moves
        byte[][] chosenTurn = minimaxSearch(game, targetDepth);

        // build pojo for the chosen turn
        TurnPojo turn;
        if (chosenTurn[0].length > 2)
            turn = TurnFactory.buildTurn(
                    new byte[] {chosenTurn[0][0], chosenTurn[0][1]},
                    new byte[] {chosenTurn[0][2], chosenTurn[0][3]},
                    chosenTurn[1],
                    game.getPlayer());
        else turn = TurnFactory.buildTurn(
                chosenTurn[0],
                chosenTurn[1],
                game.getPlayer());

        return turn;
    }


    private byte[][] minimaxSearch(Game game, int depth) {
        // performs minimax search with a heuristic evaluation at the given depth
        // returns a 2D array consisting of [dominoes, moves]

        // best choice so far
        byte[] bestDomino = new byte[2];
        byte[] bestMoveSeq = new byte[0];
        double bestVal = Double.NEGATIVE_INFINITY * game.getPlayer();

        // perform search for each domino choice then take the "best of the best"
        for (byte[] dom: game.getAvailableDominoes(game.getPlayer())) {
            // ignores the double for now
            if (dom[0] == dom[1]) continue;

            // finds all move sequences for the given domino
            List<byte[]> moves = game.findMoves(dom);

            // maximising (black)
            if (game.getPlayer() == 1) {
                double maxVal = Double.NEGATIVE_INFINITY;
                byte[] maxMove = null;
                // checks value of each possible move sequence
                for (byte[] m: moves) {
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    for (int i = 0; i < m.length; i += 2) tempGame.movePiece(m[i], m[i + 1]);
                    tempGame.nextTurn();

                    double moveVal = minimaxEval(tempGame, depth - 1);
                    if (moveVal > maxVal) {
                        maxVal = moveVal;
                        maxMove = m;
                    }
                }
                // checks value of using domino without moving
                if (moves.isEmpty()) {
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    tempGame.nextTurn();

                    double domVal = minimaxEval(tempGame, depth - 1);
                    if (domVal > maxVal) {
                        maxVal = domVal;
                        maxMove = new byte[0];
                    }
                }

                // checks if best move for this domino is best move so far for the turn
                if (maxVal > bestVal) {
                    bestVal = maxVal;
                    bestDomino = dom;
                    bestMoveSeq = maxMove;
                }
            }

            // minimising (white)
            else if (game.getPlayer() == -1) {
                double minVal = Double.POSITIVE_INFINITY;
                byte[] minMove = null;
                // checks value of each possible move sequence
                for (byte[] m: moves) {
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    for (int i = 0; i < m.length; i += 2) tempGame.movePiece(m[i], m[i + 1]);
                    tempGame.nextTurn();

                    double moveVal = minimaxEval(tempGame, depth - 1);
                    if (moveVal < minVal) {
                        minVal = moveVal;
                        minMove = m;
                    }
                }
                // checks value of using domino without moving
                if (moves.isEmpty()) {
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    tempGame.nextTurn();

                    double domVal = minimaxEval(tempGame, depth - 1);
                    if (domVal > minVal) {
                        minVal = domVal;
                        minMove = new byte[0];
                    }
                }

                // checks if best move for this domino is best move so far for the turn
                if (minVal < bestVal) {
                    bestVal = minVal;
                    bestDomino = dom;
                    bestMoveSeq = minMove;
                }
            }
        }

        // find available double
        byte[] dbl = null;
        for (int i = 0; i < 6; i++)
            if (game.checkDomino(i)) {
                dbl = Game.getDomino(i);
                break;
            }
        if (dbl != null) {
            // finds all move sequences for the double
            List<byte[]> moves = game.findMoves(dbl);

            // try evaluating with each possible "substitute" domino
            for (byte[] dom: game.getAvailableDominoes(game.getPlayer())) {
                if (dom[0] == dom[1]) continue;

                // maximising (black)
                if (game.getPlayer() == 1) {
                    double maxVal = Double.NEGATIVE_INFINITY;
                    byte[] maxMove = null;
                    // checks value of each possible move sequence
                    for (byte[] m: moves) {
                        Game tempGame = new Game(game);
                        tempGame.useDomino(dom);
                        tempGame.useDomino(dbl);
                        for (int i = 0; i < m.length; i += 2) tempGame.movePiece(m[i], m[i + 1]);
                        tempGame.nextTurn();

                        double moveVal = minimaxEval(tempGame, depth - 1);
                        if (moveVal > maxVal) {
                            maxVal = moveVal;
                            maxMove = m;
                        }
                    }
                    // checks value of using domino + double without moving
                    if (moves.isEmpty()) {
                        Game tempGame = new Game(game);
                        tempGame.useDomino(dom);
                        tempGame.useDomino(dbl);
                        tempGame.nextTurn();

                        double domVal = minimaxEval(tempGame, depth - 1);
                        if (domVal > maxVal) {
                            maxVal = domVal;
                            maxMove = new byte[0];
                        }
                    }

                    // checks if best move for this domino + double is best move so far for the turn
                    if (maxVal > bestVal) {
                        bestVal = maxVal;
                        bestDomino = new byte[] {dbl[0], dbl[1], dom[0], dom[1]};
                        bestMoveSeq = maxMove;
                    }
                }

                // minimising (white)
                else if (game.getPlayer() == -1) {
                    double minVal = Double.POSITIVE_INFINITY;
                    byte[] minMove = null;
                    // checks value of each possible move sequence
                    for (byte[] m: moves) {
                        Game tempGame = new Game(game);
                        tempGame.useDomino(dom);
                        tempGame.useDomino(dbl);
                        for (int i = 0; i < m.length; i += 2) tempGame.movePiece(m[i], m[i + 1]);
                        tempGame.nextTurn();

                        double moveVal = minimaxEval(tempGame, depth - 1);
                        if (moveVal < minVal) {
                            minVal = moveVal;
                            minMove = m;
                        }
                    }
                    // checks value of using domino + double without moving
                    if (moves.isEmpty()) {
                        Game tempGame = new Game(game);
                        tempGame.useDomino(dom);
                        tempGame.useDomino(dbl);
                        tempGame.nextTurn();

                        double domVal = minimaxEval(tempGame, depth - 1);
                        if (domVal > minVal) {
                            minVal = domVal;
                            minMove = new byte[0];
                        }
                    }

                    // checks if best move for this domino + double is best move so far for the turn
                    if (minVal < bestVal) {
                        bestVal = minVal;
                        bestDomino = new byte[] {dbl[0], dbl[1], dom[0], dom[1]};
                        bestMoveSeq = minMove;
                    }
                }
            }
        }

        return new byte[][] {bestDomino, bestMoveSeq};
    }


    public double minimaxEval(Game game, int depth) {
        // recursively performs minimax evaluation, with a heuristic evaluation at a given depth

        // use heuristic at set depth
        if (depth == 0) return heuristic.evaluate(game);


        // best choice so far
        double bestVal = Double.NEGATIVE_INFINITY * game.getPlayer();

        // perform search for each domino choice then take the "best of the best"
        for (byte[] dom: game.getAvailableDominoes(game.getPlayer())) {
            // ignores the double for now
            if (dom[0] == dom[1]) continue;

            // finds all move sequences for the given domino
            List<byte[]> moves = game.findMoves(dom);

            // maximising (black)
            if (game.getPlayer() == 1) {
                double maxVal = Double.NEGATIVE_INFINITY;
                // checks value of each possible move sequence
                for (byte[] m: moves) {
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    for (int i = 0; i < m.length; i += 2) tempGame.movePiece(m[i], m[i + 1]);
                    tempGame.nextTurn();

                    double moveVal = minimaxEval(tempGame, depth - 1);
                    maxVal = Math.max(maxVal, moveVal);
                }
                // checks value of using domino without moving
                if (moves.isEmpty()) {
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    tempGame.nextTurn();

                    double domVal = minimaxEval(tempGame, depth - 1);
                    maxVal = Math.max(maxVal, domVal);
                }

                // checks if best move for this domino is best move so far for the turn
                bestVal = Math.max(maxVal, bestVal);
            }

            // minimising (white)
            else if (game.getPlayer() == -1) {
                double minVal = Double.POSITIVE_INFINITY;
                // checks value of each possible move sequence
                for (byte[] m: moves) {
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    for (int i = 0; i < m.length; i += 2) tempGame.movePiece(m[i], m[i + 1]);
                    tempGame.nextTurn();

                    double moveVal = minimaxEval(tempGame, depth - 1);
                    minVal = Math.min(minVal, moveVal);
                }
                // checks value of using domino without moving
                if (moves.isEmpty()) {
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    tempGame.nextTurn();

                    double domVal = minimaxEval(tempGame, depth - 1);
                    minVal = Math.min(minVal, domVal);
                }

                // checks if best move for this domino is best move so far for the turn
                bestVal = Math.min(minVal, bestVal);
            }
        }

        // find available double
        byte[] dbl = null;
        for (int i = 0; i < 6; i++)
            if (game.checkDomino(i)) {
                dbl = Game.getDomino(i);
                break;
            }
        if (dbl != null) {
            // finds all move sequences for the double
            List<byte[]> moves = game.findMoves(dbl);

            // try evaluating with each possible "substitute" domino
            for (byte[] dom: game.getAvailableDominoes(game.getPlayer())) {
                if (dom[0] == dom[1]) continue;

                // maximising (black)
                if (game.getPlayer() == 1) {
                    double maxVal = Double.NEGATIVE_INFINITY;
                    // checks value of each possible move sequence
                    for (byte[] m: moves) {
                        Game tempGame = new Game(game);
                        tempGame.useDomino(dom);
                        tempGame.useDomino(dbl);
                        for (int i = 0; i < m.length; i += 2) tempGame.movePiece(m[i], m[i + 1]);
                        tempGame.nextTurn();

                        double moveVal = minimaxEval(tempGame, depth - 1);
                        maxVal = Math.max(maxVal, moveVal);
                    }
                    // checks value of using domino + double without moving
                    if (moves.isEmpty()) {
                        Game tempGame = new Game(game);
                        tempGame.useDomino(dom);
                        tempGame.useDomino(dbl);
                        tempGame.nextTurn();

                        double domVal = minimaxEval(tempGame, depth - 1);
                        maxVal = Math.max(maxVal, domVal);
                    }

                    // checks if best move for this domino + double is best move so far for the turn
                    bestVal = Math.max(maxVal, bestVal);
                }

                // minimising (white)
                else if (game.getPlayer() == -1) {
                    double minVal = Double.POSITIVE_INFINITY;
                    // checks value of each possible move sequence
                    for (byte[] m: moves) {
                        Game tempGame = new Game(game);
                        tempGame.useDomino(dom);
                        tempGame.useDomino(dbl);
                        for (int i = 0; i < m.length; i += 2) tempGame.movePiece(m[i], m[i + 1]);
                        tempGame.nextTurn();

                        double moveVal = minimaxEval(tempGame, depth - 1);
                        minVal = Math.min(minVal, moveVal);
                    }
                    // checks value of using domino + double without moving
                    if (moves.isEmpty()) {
                        Game tempGame = new Game(game);
                        tempGame.useDomino(dom);
                        tempGame.useDomino(dbl);
                        tempGame.nextTurn();

                        double domVal = minimaxEval(tempGame, depth - 1);
                        minVal = Math.min(minVal, domVal);
                    }

                    // checks if best move for this domino + double is best move so far for the turn
                    bestVal = Math.min(minVal, bestVal);
                }
            }
        }

        return bestVal;
    }
}
