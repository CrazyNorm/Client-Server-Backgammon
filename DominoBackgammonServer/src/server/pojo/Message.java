package server.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "message")
public class Message {

    @JacksonXmlProperty(localName = "idem_key", isAttribute = true)
    private final long idempotencyKey;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Connect connect; // client
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Start start; // server
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TurnPojo turn; // client / server
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Reset reset; // server
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private NextTurn nextTurn; // server


    public Message() {
        this(0L);
    }

    public Message(
            @JsonProperty("idem_key") long idempotencyKey
    ) {
        this.idempotencyKey = idempotencyKey;
    }


    public long getIdempotencyKey() {
        return idempotencyKey;
    }

    public TurnPojo getTurn() {
        return turn;
    }

    public void setTurn(TurnPojo turn) {
        this.turn = turn;
    }

    public boolean isTurn() {
        return turn != null;
    }

    public Connect getConnect() {
        return connect;
    }

    public void setConnect(Connect connect) {
        this.connect = connect;
    }

    public boolean isConnect() {
        return connect != null;
    }

    public Start getStart() {
        return start;
    }

    public void setStart(Start start) {
        this.start = start;
    }

    public boolean isStart() {
        return start != null;
    }

    public Reset getReset() {
        return reset;
    }

    public void setReset(Reset reset) {
        this.reset = reset;
    }

    public boolean isReset() {
        return reset != null;
    }

    public NextTurn getNextTurn() {
        return nextTurn;
    }

    public void setNextTurn(NextTurn nextTurn) {
        this.nextTurn = nextTurn;
    }

    public boolean isNextTurn() {
        return nextTurn != null;
    }

    @JsonIgnore
    public boolean isMalformed() {
        return (connect == null) && (start == null) && (turn == null) && (reset == null) && (nextTurn == null);
    }
}
