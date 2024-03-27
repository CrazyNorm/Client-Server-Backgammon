package ai;

import ai.heursitics.Heuristic;
import ai.heursitics.HeuristicFactory;
import client.pojo.TurnPojo;
import client.util.TurnFactory;
import game.Game;

import java.util.List;
import java.util.Random;

public class MinimaxAI extends AI {

    private byte colour; // colour of the AI player

    private final Heuristic heuristic;
    private final int targetDepth; // how deep to search before evaluating with heuristic
    private final int searchTimeout; // timeout to give up search and just return best so far

    private final static double unusedDominoPenalty = -5;

    private final Random RAND = new Random();

    public MinimaxAI(String type) {
        this.heuristic = HeuristicFactory.getHeuristic(type);
        this.targetDepth = 3;
        this.searchTimeout = 10000;
    }

    @Override
    public TurnPojo chooseTurn(Game game) {
        // perform minimax search to choose domino(es) and moves
        this.colour = game.getPlayer();

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
            double alpha = Double.NEGATIVE_INFINITY;
            double beta = Double.POSITIVE_INFINITY;

            // ignores the double for now
            if (dom[0] == dom[1]) continue;

            // finds all move sequences for the given domino
            List<byte[]> moves = game.findMoves(dom);

            // maximising (black)
            if (game.getPlayer() == 1) {
                byte[] maxMove = null;
                // checks value of each possible move sequence
                for (byte[] m: moves) {
                    if (alpha >= beta) break;

                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    for (int i = 0; i < m.length; i += 2) tempGame.movePiece(m[i], m[i + 1]);
                    tempGame.nextTurn();

                    double moveVal = minimaxEval(tempGame, depth - 1, alpha, beta);
                    if (moveVal > alpha ||
                            (moveVal == alpha && RAND.nextInt(100) % 3 == 0)
                    ) {
                        alpha = moveVal;
                        maxMove = m;
                    }
                }
                // checks value of using domino without moving
                if (moves.isEmpty()) {
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    tempGame.nextTurn();

                    double domVal = minimaxEval(tempGame, depth - 1, alpha, beta) + unusedDominoPenalty;
                    if (domVal > alpha ||
                            (domVal == alpha && RAND.nextInt(100) % 3 == 0)
                    ) {
                        alpha = domVal;
                        maxMove = new byte[0];
                    }
                }

                // checks if best move for this domino is best move so far for the turn
                if (alpha > bestVal ||
                        (alpha == bestVal && RAND.nextInt(100) % 3 == 0)
                ) {
                    bestVal = alpha;
                    bestDomino = dom;
                    bestMoveSeq = maxMove;
                }
            }

            // minimising (white)
            else if (game.getPlayer() == -1) {
                byte[] minMove = null;
                // checks value of each possible move sequence
                for (byte[] m: moves) {
                    if (alpha >= beta) break;

                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    for (int i = 0; i < m.length; i += 2) tempGame.movePiece(m[i], m[i + 1]);
                    tempGame.nextTurn();

                    double moveVal = minimaxEval(tempGame, depth - 1, alpha, beta);
                    if (moveVal < beta ||
                            (moveVal == beta && RAND.nextInt(100) % 3 == 0)
                    ) {
                        beta = moveVal;
                        minMove = m;
                    }
                }
                // checks value of using domino without moving
                if (moves.isEmpty()) {
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    tempGame.nextTurn();

                    double domVal = minimaxEval(tempGame, depth - 1, alpha, beta) + unusedDominoPenalty;
                    if (domVal > beta ||
                            (domVal == beta && RAND.nextInt(100) % 3 == 0)
                    ) {
                        beta = domVal;
                        minMove = new byte[0];
                    }
                }

                // checks if best move for this domino is best move so far for the turn
                if (beta < bestVal ||
                        (beta == bestVal && RAND.nextInt(100) % 3 == 0)
                ) {
                    bestVal = beta;
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
            byte[] bestSub = new byte[2];
            double bestSubVal = Double.NEGATIVE_INFINITY * game.getPlayer();

            // evaluate without a substitute domino, then decide best substitute afterwards
            double alpha = Double.NEGATIVE_INFINITY;
            double beta = Double.POSITIVE_INFINITY;

            // maximising (black)
            if (game.getPlayer() == 1) {
                byte[] maxMove = new byte[0];
                // checks value of each possible move sequence
                for (byte[] m: moves) {
                    if (alpha >= beta) break;

                    Game tempGame = new Game(game);
                    tempGame.useDomino(dbl);
                    tempGame.useDomino(game.getRandomDomino(game.getPlayer()));
                    for (int i = 0; i < m.length; i += 2) tempGame.movePiece(m[i], m[i + 1]);
                    tempGame.nextTurn();

                    // reduce search depth by 1 when doubles are used
                    double moveVal = minimaxEval(tempGame, depth - 2, alpha, beta);
                    if (moveVal > alpha ||
                            (moveVal == alpha && RAND.nextInt(100) % 3 == 0)
                    ) {
                        alpha = moveVal;
                        maxMove = m;
                    }
                }
                // checks value of using double without moving
                if (moves.isEmpty()) {
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dbl);
                    tempGame.useDomino(game.getRandomDomino(game.getPlayer()));
                    tempGame.nextTurn();

                    // reduce search depth by 1 when doubles are used
                    double domVal = minimaxEval(tempGame, depth - 2, alpha, beta) + unusedDominoPenalty;
                    if (domVal > alpha ||
                            (domVal == alpha && RAND.nextInt(100) % 3 == 0)
                    ) {
                        alpha = domVal;
                        maxMove = new byte[0];
                    }
                }

                // checks for best substitute domino
                for (byte[] dom : game.getAvailableDominoes(game.getPlayer())) {
                    if (dom[0] == dom[1]) continue;

                    // find heuristic evaluation for best double move with this domino as substitute
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    for (int i = 0; i < maxMove.length; i += 2) tempGame.movePiece(maxMove[i], maxMove[i + 1]);
                    double val = heuristic.evaluate(tempGame, game.getPlayer());

                    if (val > bestSubVal) {
                        bestSubVal = val;
                        bestSub = dom;
                    }
                }

                // checks if best double move + best substitute is best move so far for the turn
                if (alpha > bestVal ||
                        (alpha == bestVal && RAND.nextInt(100) % 3 == 0)
                ) {
                    bestVal = alpha;
                    bestDomino = new byte[] {dbl[0], dbl[1], bestSub[0], bestSub[1]};
                    bestMoveSeq = maxMove;
                }
            }

            // minimising (white)
            else if (game.getPlayer() == -1) {
                byte[] minMove = new byte[0];
                // checks value of each possible move sequence
                for (byte[] m: moves) {
                    if (alpha >= beta) break;

                    Game tempGame = new Game(game);
                    tempGame.useDomino(dbl);
                    tempGame.useDomino(game.getRandomDomino(game.getPlayer()));
                    for (int i = 0; i < m.length; i += 2) tempGame.movePiece(m[i], m[i + 1]);
                    tempGame.nextTurn();

                    // reduce search depth by 1 when doubles are used
                    double moveVal = minimaxEval(tempGame, depth - 2, alpha, beta);
                    if (moveVal < beta ||
                            (moveVal == beta && RAND.nextInt(100) % 3 == 0)
                    ) {
                        beta = moveVal;
                        minMove = m;
                    }
                }
                // checks value of using domino + double without moving
                if (moves.isEmpty()) {
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dbl);
                    tempGame.useDomino(game.getRandomDomino(game.getPlayer()));
                    tempGame.nextTurn();

                    // reduce search depth by 1 when doubles are used
                    double domVal = minimaxEval(tempGame, depth - 2, alpha, beta) + unusedDominoPenalty;
                    if (domVal > beta ||
                            (domVal == beta && RAND.nextInt(100) % 3 == 0)
                    ) {
                        beta = domVal;
                        minMove = new byte[0];
                    }
                }

                // checks for best substitute domino
                for (byte[] dom : game.getAvailableDominoes(game.getPlayer())) {
                    if (dom[0] == dom[1]) continue;

                    // find heuristic evaluation for best double move with this domino as substitute
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    for (int i = 0; i < minMove.length; i += 2) tempGame.movePiece(minMove[i], minMove[i + 1]);
                    double val = heuristic.evaluate(tempGame, game.getPlayer());

                    if (val < bestSubVal) {
                        bestSubVal = val;
                        bestSub = dom;
                    }
                }

                // checks if best double move + best substitute is best move so far for the turn
                if (beta < bestVal ||
                        (beta == bestVal && RAND.nextInt(100) % 3 == 0)
                ) {
                    bestVal = beta;
                    bestDomino = new byte[] {dbl[0], dbl[1], bestSub[0], bestSub[1]};
                    bestMoveSeq = minMove;
                }
            }
        }

        return new byte[][] {bestDomino, bestMoveSeq};
    }


    public double minimaxEval(Game game, int depth, double alpha, double beta) {
        // recursively performs minimax evaluation, with a heuristic evaluation at a given depth

        // check for wins
        if (game.checkWin() == 1) return Double.POSITIVE_INFINITY;
        if (game.checkWin() == -1) return Double.NEGATIVE_INFINITY;

        // use heuristic at set depth
        if (depth <= 0) return heuristic.evaluate(game, colour);


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
                alpha = Double.NEGATIVE_INFINITY; // maximise alpha
                // checks value of each possible move sequence
                for (byte[] m: moves) {
                    if (alpha >= beta) break;

                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    for (int i = 0; i < m.length; i += 2) tempGame.movePiece(m[i], m[i + 1]);
                    tempGame.nextTurn();

                    double moveVal = minimaxEval(tempGame, depth - 1, alpha, beta);
                    alpha = Math.max(alpha, moveVal);
                }
                // checks value of using domino without moving
                if (moves.isEmpty()) {
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    tempGame.nextTurn();

                    double domVal = minimaxEval(tempGame, depth - 1, alpha, beta) + unusedDominoPenalty;
                    alpha = Math.max(alpha, domVal);
                }

                // checks if best move for this domino is best move so far for the turn
                bestVal = Math.max(alpha, bestVal);
            }

            // minimising (white)
            else if (game.getPlayer() == -1) {
                beta = Double.POSITIVE_INFINITY; // minimise beta
                // checks value of each possible move sequence
                for (byte[] m: moves) {
                    if (alpha >= beta) break;

                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    for (int i = 0; i < m.length; i += 2) tempGame.movePiece(m[i], m[i + 1]);
                    tempGame.nextTurn();

                    double moveVal = minimaxEval(tempGame, depth - 1, alpha, beta);
                    beta = Math.min(beta, moveVal);
                }
                // checks value of using domino without moving
                if (moves.isEmpty()) {
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dom);
                    tempGame.nextTurn();

                    double domVal = minimaxEval(tempGame, depth - 1, alpha, beta) + unusedDominoPenalty;
                    beta = Math.min(beta, domVal);
                }

                // checks if best move for this domino is best move so far for the turn
                bestVal = Math.min(beta, bestVal);
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
            // evaluate without a substitute domino, then decide best substitute afterwards


            // maximising (black)
            if (game.getPlayer() == 1) {
                alpha = Double.NEGATIVE_INFINITY;
                // checks value of each possible move sequence
                for (byte[] m: moves) {
                    if (alpha >= beta) break;

                    Game tempGame = new Game(game);
                    tempGame.useDomino(dbl);
                    tempGame.useDomino(game.getRandomDomino(game.getPlayer()));
                    for (int i = 0; i < m.length; i += 2) tempGame.movePiece(m[i], m[i + 1]);
                    tempGame.nextTurn();

                    // reduce search depth by 1 when doubles are used
                    double moveVal = minimaxEval(tempGame, depth - 2, alpha, beta);
                    alpha = Math.max(alpha, moveVal);
                }
                // checks value of using double without moving
                if (moves.isEmpty()) {
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dbl);
                    tempGame.useDomino(game.getRandomDomino(game.getPlayer()));
                    tempGame.nextTurn();

                    // reduce search depth by 1 when doubles are used
                    double domVal = minimaxEval(tempGame, depth - 2, alpha, beta) + unusedDominoPenalty;
                    alpha = Math.max(alpha, domVal);
                }

                // checks if best move for this domino + double is best move so far for the turn
                bestVal = Math.max(alpha, bestVal);
            }

            // minimising (white)
            else if (game.getPlayer() == -1) {
                beta = Double.POSITIVE_INFINITY;
                // checks value of each possible move sequence
                for (byte[] m: moves) {
                    if (alpha >= beta) break;

                    Game tempGame = new Game(game);
                    tempGame.useDomino(dbl);
                    tempGame.useDomino(game.getRandomDomino(game.getPlayer()));
                    for (int i = 0; i < m.length; i += 2) tempGame.movePiece(m[i], m[i + 1]);
                    tempGame.nextTurn();

                    // reduce search depth by 1 when doubles are used
                    double moveVal = minimaxEval(tempGame, depth - 2, alpha, beta);
                    beta = Math.min(beta, moveVal);
                }
                // checks value of using domino + double without moving
                if (moves.isEmpty()) {
                    Game tempGame = new Game(game);
                    tempGame.useDomino(dbl);
                    tempGame.useDomino(game.getRandomDomino(game.getPlayer()));
                    tempGame.nextTurn();

                    // reduce search depth by 1 when doubles are used
                    double domVal = minimaxEval(tempGame, depth - 2, alpha, beta) + unusedDominoPenalty;
                    beta = Math.min(beta, domVal);
                }

                // checks if best move for this domino + double is best move so far for the turn
                bestVal = Math.min(beta, bestVal);
            }
        }

        return bestVal;
    }
}
