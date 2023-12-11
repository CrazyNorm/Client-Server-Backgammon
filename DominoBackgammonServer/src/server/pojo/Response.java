package server.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "response")
public class Response {

    @JacksonXmlProperty(localName = "to", isAttribute = true)
    private final long responseTo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Approve approve;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Deny deny;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Acknowledge acknowledge;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Hash hash;


    public Response() {
        this(0L);
    }

    public Response(long responseTo) {
        this.responseTo = responseTo;
    }


    public long getResponseTo() {
        return responseTo;
    }

    public Approve getApprove() {
        return approve;
    }

    public void setApprove(Approve approve) {
        this.approve = approve;
    }

    public Deny getDeny() {
        return deny;
    }

    public void setDeny(Deny deny) {
        this.deny = deny;
    }

    public Acknowledge getAcknowledge() {
        return acknowledge;
    }

    public void setAcknowledge(Acknowledge acknowledge) {
        this.acknowledge = acknowledge;
    }

    public Hash getHash() {
        return hash;
    }

    public void setHash(Hash hash) {
        this.hash = hash;
    }
}
