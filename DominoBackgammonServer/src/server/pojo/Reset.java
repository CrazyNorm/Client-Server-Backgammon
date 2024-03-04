package server.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import game.common.Player;

import java.util.ArrayList;
import java.util.List;

public class Reset {

    @JacksonXmlProperty(isAttribute = true)
    private final Player player;

    @JacksonXmlProperty(isAttribute = true)
    private final int turnCount;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "pieceList")
    private final List<PieceList> pieces;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "hand")
    private final List<HandPojo> hands;


    public Reset(Player player, int turnCount) {
        this.player = player;
        this.turnCount = turnCount;
        this.pieces = new ArrayList<>();
        this.hands = new ArrayList<>();
    }

    public Reset(
            @JsonProperty("player") Player player,
            @JsonProperty("turnCount") int turnCount,
            @JsonProperty("pieceList") List<PieceList> pieces,
            @JsonProperty("hand") List<HandPojo> hands) {
        this.player = player;
        this.turnCount = turnCount;
        this.pieces = pieces;
        this.hands = hands;
    }

    public Player getPlayer() {
        return player;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public List<PieceList> getPieces() {
        return pieces;
    }

    public void addPieceList(PieceList pieceList) {
        pieces.add(pieceList);
    }

    public List<HandPojo> getHands() {
        return hands;
    }

    public void addHand(HandPojo hand) {
        hands.add(hand);
    }
}
