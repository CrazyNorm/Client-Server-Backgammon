package server.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "message")
public class Message {

    @JacksonXmlProperty(localName = "idem_key", isAttribute = true)
    private final long idempotencyKey;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Connect connect;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Start start;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TurnPojo turn;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Reset reset;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private NextTurn nextTurn;


    public Message() {
        this(0L);
    }

    public Message(long idempotencyKey) {
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

    public Connect getConnect() {
        return connect;
    }

    public void setConnect(Connect connect) {
        this.connect = connect;
    }

    public Start getStart() {
        return start;
    }

    public void setStart(Start start) {
        this.start = start;
    }

    public Reset getReset() {
        return reset;
    }

    public void setReset(Reset reset) {
        this.reset = reset;
    }

    public NextTurn getNextTurn() {
        return nextTurn;
    }

    public void setNextTurn(NextTurn nextTurn) {
        this.nextTurn = nextTurn;
    }
}
