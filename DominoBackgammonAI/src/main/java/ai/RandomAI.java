package ai;

import client.pojo.TurnPojo;
import client.util.TurnFactory;
import game.Game;

import java.util.List;
import java.util.Random;

public class RandomAI extends AI{

    private static Random RAND = new Random();

    @Override
    public TurnPojo chooseTurn(Game game) {
        // pick random domino
        List<byte[]> dominoes = game.getAvailableDominoes(game.getPlayer());
        byte[] chosenDomino = dominoes.get(RAND.nextInt(dominoes.size()));

        // pick random set of moves for the chosen domino
        List<byte[]> moves = game.findMoves(chosenDomino);
        byte[] chosenMoveSeq = moves.get(RAND.nextInt(moves.size()));

        // build pojo for the chosen turn
        TurnPojo turn;
        if (chosenDomino[0] == chosenDomino[1]) {
            // pick extra domino if double was chosen
            byte[] extraDomino = chosenDomino;
            while (extraDomino == chosenDomino)
                extraDomino = dominoes.get(RAND.nextInt(dominoes.size()));

            turn = TurnFactory.buildTurn(chosenDomino, extraDomino, chosenMoveSeq, game.getPlayer());
        }
        else turn = TurnFactory.buildTurn(chosenDomino, chosenMoveSeq, game.getPlayer());

        return turn;
    }
}
