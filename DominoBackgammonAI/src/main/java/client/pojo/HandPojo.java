package client.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import game.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HandPojo {

    @JacksonXmlProperty(isAttribute = true)
    private final Player colour;
    @JacksonXmlProperty(isAttribute = true)
    private final int set;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "domino")
    private final List<DominoPojo> dominoes;


    public HandPojo(Player colour, int set) {
        this.colour = colour;
        this.set = set;
        this.dominoes = new ArrayList<>();
    }

    public HandPojo(
            @JsonProperty("colour") Player colour,
            @JsonProperty("set") int set,
            @JsonProperty("domino") List<DominoPojo> dominoes
    ) {
        this.colour = colour;
        this.set = set;
        this.dominoes = dominoes;
    }

    public Player getColour() {
        return colour;
    }

    public int getSet() {
        return set;
    }

    public List<DominoPojo> getDominoes() {
        return dominoes;
    }

    public void setDominoes(DominoPojo[] dominoes) {
        this.dominoes.clear();
        this.dominoes.addAll(Arrays.asList(dominoes));
    }
}
