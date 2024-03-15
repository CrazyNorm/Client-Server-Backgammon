package client.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class Hash {

    @JacksonXmlText
    private final String value;


    public Hash(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }
}
