package com.example.dominobackgammonclient.client.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class MovePojo {

    @JacksonXmlProperty(isAttribute = true)
    private final int start;
    @JacksonXmlProperty(isAttribute = true)
    private final int end;


    public MovePojo(
            @JsonProperty("start") int start,
            @JsonProperty("end") int end
    ) {
        this.start = start;
        this.end = end;
    }


    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
