package com.example.dominobackgammonclient.client.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Deny {

    @JacksonXmlProperty(isAttribute = true)
    private final String reason;


    public Deny(
            @JsonProperty("reason") String reason
    ) {
        this.reason = reason;
    }


    public String getReason() {
        return reason;
    }
}
