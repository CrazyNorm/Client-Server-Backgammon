package com.example.dominobackgammonclient.client.pojo;

import com.example.dominobackgammonclient.game.common.Player;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class PieceList {

    @JacksonXmlProperty(isAttribute = true)
    private final Player colour;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "index")
    private final List<Integer> indices;


    public PieceList(Player colour) {
        this.colour = colour;
        this.indices = new ArrayList<>();
    }

    public PieceList(
            @JsonProperty("colour") Player colour,
            @JsonProperty("index") List<Integer> indices) {
        this.colour = colour;
        this.indices = indices;
    }

    public Player getColour() {
        return colour;
    }

    public List<Integer> getIndices() {
        return indices;
    }

    public void setIndices(int[] indices) {
        this.indices.clear();
        for (int i : indices)
            this.indices.add(i);
    }
}
