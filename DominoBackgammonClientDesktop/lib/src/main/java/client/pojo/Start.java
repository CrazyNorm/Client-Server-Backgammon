package client.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Start {

    @JacksonXmlProperty(localName = "opponent", isAttribute = true)
    private final String opponentName;
    @JacksonXmlProperty(isAttribute = true)
    private final PlayerPojo colour;


    public Start(
            @JsonProperty("opponent") String opponentName,
            @JsonProperty("colour") PlayerPojo colour
    ) {
        this.opponentName = opponentName;
        this.colour = colour;
    }


    public String getOpponentName() {
        return opponentName;
    }

    public PlayerPojo getColour() {
        return colour;
    }
}
