package ai;

import client.pojo.TurnPojo;
import game.Game;

public abstract class AI {

    public abstract TurnPojo chooseTurn(Game game);
}
