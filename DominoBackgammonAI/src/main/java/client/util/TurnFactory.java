package client.util;

import client.pojo.DominoPojo;
import client.pojo.MovePojo;
import client.pojo.TurnPojo;
import game.Player;

public class TurnFactory {

    public static TurnPojo buildTurn(byte[] domino, byte[] moves, byte player) {
        // converts byte[] turn representation to a TurnPojo for sending to the server

        // convert player to pojo
        Player playerPojo = Player.White;
        if (player == 1) playerPojo = Player.Black;
        TurnPojo turn = new TurnPojo(playerPojo);

        // convert domino to pojo
        turn.addDomino(new DominoPojo(domino[0], domino[1], true));

        // convert moves to pojo
        for (int i = 0; i < moves.length; i += 2) {
            if (player == -1) turn.addMove(new MovePojo(moves[i], moves[i + 1]));
            else if (moves[i] == 25) turn.addMove(new MovePojo(moves[i], 25 - moves[i + 1]));
            else if (moves[i + 1] == 0) turn.addMove(new MovePojo(25 - moves[i], moves[i + 1]));
            else turn.addMove(new MovePojo(25 - moves[i], 25 - moves[i + 1]));
        }


        return turn;
    }

    public static TurnPojo buildTurn(byte[] domino, byte[] dbl, byte[] moves, byte player) {
        // converts byte[] turn representation to a TurnPojo for sending to the server

        // convert player to pojo
        Player playerPojo = Player.White;
        if (player == 1) playerPojo = Player.Black;
        TurnPojo turn = new TurnPojo(playerPojo);

        // convert domino to pojo
        turn.addDomino(new DominoPojo(domino[0], domino[1], true));

        // convert double to pojo
        turn.addDomino(new DominoPojo(dbl[0], dbl[1], true));

        // convert moves to pojo
        for (int i = 0; i < moves.length; i += 2)
            turn.addMove(new MovePojo(moves[i], moves[i + 1]));


        return turn;
    }
}
