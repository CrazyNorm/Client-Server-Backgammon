package com.example.dominobackgammonclient.client.xml;

import com.example.dominobackgammonclient.client.pojo.Message;
import com.example.dominobackgammonclient.client.pojo.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class ProtocolMapper {

    public String serialize(Object o) {
        // serializes the given object to a string

        ObjectMapper mapper = new XmlMapper();
        String xml;
        try {
            xml = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return xml;
    }


    public Message deserializeMessage(String xml) {
        // deserializes a string to a message

        ObjectMapper mapper = new XmlMapper();
        Message deserialized;
        try {
            deserialized = mapper.readValue(xml, Message.class);
        } catch (UnrecognizedPropertyException u) {
            deserialized = new Message();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return deserialized;
    }

    public Response deserializeResponse(String xml) {
        // deserializes a string to a response

        ObjectMapper mapper = new XmlMapper();
        Response deserialized;
        try {
            deserialized = mapper.readValue(xml, Response.class);
        } catch (UnrecognizedPropertyException u) {
            deserialized = new Response();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return deserialized;
    }
}
