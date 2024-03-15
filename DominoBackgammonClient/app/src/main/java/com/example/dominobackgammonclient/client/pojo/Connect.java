package com.example.dominobackgammonclient.client.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Connect {

    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private final String playerName;
    @JacksonXmlProperty(localName = "opponent", isAttribute = true)
    private final String opponentType;


    public Connect(
            @JsonProperty("name") String playerName,
            @JsonProperty("opponent") String opponentType
    ) {
        this.playerName = playerName;
        this.opponentType = opponentType;
    }


    public String getPlayerName() {
        return playerName;
    }

    public String getOpponentType() {
        return opponentType;
    }
}
