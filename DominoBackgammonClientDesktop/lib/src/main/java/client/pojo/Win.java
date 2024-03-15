package client.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Win {

    @JacksonXmlProperty(isAttribute = true)
    private final PlayerPojo player;
    @JacksonXmlProperty(isAttribute = true)
    private final int type;


    public Win(
            @JsonProperty("player") PlayerPojo player,
            @JsonProperty("type") int type
    ) {
        this.player = player;
        this.type = type;
    }


    public PlayerPojo getPlayer() {
        return player;
    }

    public int getType() {
        return type;
    }
}
