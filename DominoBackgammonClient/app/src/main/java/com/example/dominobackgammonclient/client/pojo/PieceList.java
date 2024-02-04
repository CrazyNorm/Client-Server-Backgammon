package com.example.dominobackgammonclient.client.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class PieceList {

    @JacksonXmlProperty(isAttribute = true)
    private final PlayerPojo colour;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "index")
    private final List<Integer> indices;


    public PieceList(PlayerPojo colour) {
        this.colour = colour;
        this.indices = new ArrayList<>();
    }

    public PieceList(
            @JsonProperty("colour") PlayerPojo colour,
            @JsonProperty("index") List<Integer> indices) {
        this.colour = colour;
        this.indices = indices;
    }

    public PlayerPojo getColour() {
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
