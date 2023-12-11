package server.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Connect {

    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private final String playerName;
    @JacksonXmlProperty(localName = "opponent", isAttribute = true)
    private final String opponentType;


    public Connect(String playerName, String opponentType) {
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
