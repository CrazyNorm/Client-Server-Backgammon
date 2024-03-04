package client.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "response")
public class Response {

    @JacksonXmlProperty(localName = "to", isAttribute = true)
    private final String responseTo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Approve approve; // server
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Deny deny; // server
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Acknowledge acknowledge; // client
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Hash hash; // client


    public Response() {
        this("");
    }

    public Response(
            @JsonProperty("to") String responseTo
    ) {
        this.responseTo = responseTo;
    }


    public String getResponseTo() {
        return responseTo;
    }

    public Approve getApprove() {
        return approve;
    }

    public void setApprove(Approve approve) {
        this.approve = approve;
    }

    public boolean isApprove() {
        return approve != null;
    }

    public Deny getDeny() {
        return deny;
    }

    public void setDeny(Deny deny) {
        this.deny = deny;
    }

    public boolean isDeny() {
        return deny != null;
    }

    public Acknowledge getAcknowledge() {
        return acknowledge;
    }

    public void setAcknowledge(Acknowledge acknowledge) {
        this.acknowledge = acknowledge;
    }

    public boolean isAcknowledge() {
        return acknowledge != null;
    }

    public Hash getHash() {
        return hash;
    }

    public void setHash(Hash hash) {
        this.hash = hash;
    }

    public boolean isHash() {
        return hash != null;
    }

    @JsonIgnore
    public boolean isMalformed() {
        return (approve == null) && (deny == null) && (acknowledge == null) && (hash == null);
    }
}
