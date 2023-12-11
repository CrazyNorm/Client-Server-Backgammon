package server.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Deny {

    @JacksonXmlProperty(isAttribute = true)
    private final String reason;


    public Deny(String reason) {
        this.reason = reason;
    }


    public String getReason() {
        return reason;
    }
}
