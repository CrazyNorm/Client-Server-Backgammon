package client.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Connect {

    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private final String playerName;
    @JacksonXmlProperty(localName = "opponent", isAttribute = true)
    private final String opponentType;
    @JacksonXmlProperty(localName = "ai", isAttribute = true)
    private final boolean isAI;

    public Connect(
            @JsonProperty("name") String playerName,
            @JsonProperty("opponent") String opponentType,
            @JsonProperty("ai") boolean ai
    ) {
        this.playerName = playerName;
        this.opponentType = opponentType;
        this.isAI = ai;
    }

    @JsonIgnore
    public Connect(String playerName, String opponentType) {
        this.playerName = playerName;
        this.opponentType = opponentType;
        this.isAI = false;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getOpponentType() {
        return opponentType;
    }

    public boolean isAI() {
        return isAI;
    }
}
