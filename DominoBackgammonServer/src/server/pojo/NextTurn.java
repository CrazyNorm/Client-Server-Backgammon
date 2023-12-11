package server.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import game.common.Player;

public class NextTurn {

    @JacksonXmlProperty(isAttribute = true)
    private final Player next;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Win win;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Swap swap;


    public NextTurn(Player next) {
        this.next = next;
    }


    public Player getNext() {
        return next;
    }

    public Win getWin() {
        return win;
    }

    public void setWin(Win win) {
        this.win = win;
    }

    public Swap getSwap() {
        return swap;
    }

    public void setSwap(Swap swap) {
        this.swap = swap;
    }
}
