package client.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

@JacksonXmlRootElement(localName = "ka")
public class KeepAlive {

    @JacksonXmlText
    private String type;


    public KeepAlive (
            @JacksonXmlText String type
    ) {
        this.type = type;
    }


    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public boolean isMessage() {
        return type.equalsIgnoreCase("m");
    }

    @JsonIgnore
    public boolean isResponse() {
        return type.equalsIgnoreCase("r");
    }
}
