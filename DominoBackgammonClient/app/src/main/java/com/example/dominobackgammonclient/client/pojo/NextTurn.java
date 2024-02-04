package com.example.dominobackgammonclient.client.pojo;

import com.example.dominobackgammonclient.game.common.Player;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class NextTurn {

    @JacksonXmlProperty(isAttribute = true)
    private final Player next;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Win win;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Swap swap;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Disconnect disconnect;


    public NextTurn(
            @JsonProperty("next") Player next
    ) {
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

    public boolean isWin() {
        return (win != null);
    }

    public Swap getSwap() {
        return swap;
    }

    public void setSwap(Swap swap) {
        this.swap = swap;
    }

    public boolean isSwap() {
        return (swap != null);
    }

    public Disconnect getDisconnect() {
        return disconnect;
    }

    public void setDisconnect(Disconnect disconnect) {
        this.disconnect = disconnect;
    }

    public boolean isDisconnect() {
        return (disconnect != null);
    }
}
