package client.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class Reset {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "pieceList")
    private final List<PieceList> pieces;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "hand")
    private final List<HandPojo> hands;


    public Reset() {
        this.pieces = new ArrayList<>();
        this.hands = new ArrayList<>();
    }

    public Reset(
            @JsonProperty("pieceList") List<PieceList> pieces,
            @JsonProperty("hand") List<HandPojo> hands) {
        this.pieces = pieces;
        this.hands = hands;
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
