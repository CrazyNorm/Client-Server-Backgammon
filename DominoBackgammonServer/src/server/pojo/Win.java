package server.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import game.common.Player;

public class Win {

    @JacksonXmlProperty(isAttribute = true)
    private final Player player;
    @JacksonXmlProperty(isAttribute = true)
    private final int type;


    public Win(
            @JsonProperty("player") Player player,
            @JsonProperty("type") int type
    ) {
        this.player = player;
        this.type = type;
    }


    public Player getPlayer() {
        return player;
    }

    public int getType() {
        return type;
    }
}
