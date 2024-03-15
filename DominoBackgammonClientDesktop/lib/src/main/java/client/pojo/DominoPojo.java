package client.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class DominoPojo {

    @JacksonXmlProperty(isAttribute = true)
    private final int side1;
    @JacksonXmlProperty(isAttribute = true)
    private final int side2;
    @JacksonXmlProperty(isAttribute = true)
    private final boolean available;


    public DominoPojo(
            @JsonProperty("side1") int side1,
            @JsonProperty("side2") int side2,
            @JsonProperty("available") boolean available
    ) {
        this.side1 = side1;
        this.side2 = side2;
        this.available = available;
    }

    public DominoPojo(int side1, int side2) {
        this(side1, side2, false);
    }


    public int getSide1() {
        return side1;
    }

    public int getSide2() {
        return side2;
    }

    public boolean isAvailable() {
        return available;
    }
}
