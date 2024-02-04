package com.example.dominobackgammonclient.client.pojo;

import com.example.dominobackgammonclient.game.common.Player;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class TurnPojo {

    @JacksonXmlProperty(isAttribute = true)
    private final Player player;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "domino")
    private final List<DominoPojo> dominoes;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "move")
    private final List<MovePojo> moves;


    public TurnPojo(Player player) {
        this.player = player;
        this.dominoes = new ArrayList<>();
        this.moves = new ArrayList<>();
    }

    public TurnPojo(
            @JsonProperty("player") Player player,
            @JsonProperty("domino") List<DominoPojo> dominoes,
            @JsonProperty("move") List<MovePojo> moves
    ) {
        this.player = player;
        this.dominoes = dominoes;
        this.moves = moves;
    }

    public Player getPlayer() {
        return player;
    }

    public List<DominoPojo> getDominoes() {
        return dominoes;
    }

    public void addDomino(DominoPojo domino) {
        this.dominoes.add(domino);
    }

    public List<MovePojo> getMoves() {
        return moves;
    }

    public void addMove(MovePojo move) {
        this.moves.add(move);
    }
}
