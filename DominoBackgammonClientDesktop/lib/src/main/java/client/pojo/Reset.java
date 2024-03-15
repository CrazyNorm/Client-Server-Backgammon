package client.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class Reset {

    @JacksonXmlProperty(isAttribute = true)
    private final PlayerPojo player;

    @JacksonXmlProperty(isAttribute = true)
    private final int turnCount;
    @JacksonXmlProperty(isAttribute = true)
    private final boolean swapped;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "pieceList")
    private final List<PieceList> pieces;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "hand")
    private final List<HandPojo> hands;


    public Reset(PlayerPojo player, int turnCount, boolean swapped) {
        this.player = player;
        this.turnCount = turnCount;
        this.swapped = swapped;
        this.pieces = new ArrayList<>();
        this.hands = new ArrayList<>();
    }

    public Reset(
            @JsonProperty("player") PlayerPojo player,
            @JsonProperty("turnCount") int turnCount,
            @JsonProperty("swapped") boolean swapped,
            @JsonProperty("pieceList") List<PieceList> pieces,
            @JsonProperty("hand") List<HandPojo> hands) {
        this.player = player;
        this.turnCount = turnCount;
        this.swapped = swapped;
        this.pieces = pieces;
        this.hands = hands;
    }

    public PlayerPojo getPlayer() {
        return player;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public boolean isSwapped() {
        return swapped;
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
