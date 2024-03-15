package ui;

import client.pojo.PlayerPojo;
import client.pojo.Reset;
import client.pojo.TurnPojo;

abstract public class AbstractViewModel {

    abstract public void connectionFailed();

    abstract public void setConnected(boolean con);

    abstract public void startGame(PlayerPojo clientColour, String opponentName);

    abstract public void turnDenied();

    abstract public void applyTurn(TurnPojo turn);

    abstract public String checksum();

    abstract public void resetGame(Reset reset);

    abstract public void nextTurn();

    abstract public void swapHands();

    abstract public void gameOver();

    abstract public void disconnect(BGColour colour);

    abstract public void disconnect();

    abstract public void win(BGColour colour, int type);
}
