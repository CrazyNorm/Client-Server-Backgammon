package client.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import game.Player;

public class Start {

    @JacksonXmlProperty(localName = "opponent", isAttribute = true)
    private final String opponentName;
    @JacksonXmlProperty(isAttribute = true)
    private final Player colour;


    public Start(
            @JsonProperty("opponent") String opponentName,
            @JsonProperty("colour") Player colour
    ) {
        this.opponentName = opponentName;
        this.colour = colour;
    }


    public String getOpponentName() {
        return opponentName;
    }

    public Player getColour() {
        return colour;
    }
}
