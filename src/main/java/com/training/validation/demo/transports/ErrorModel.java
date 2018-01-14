package com.training.validation.demo.transports;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data Transport Object to represent errors in JSON/XML
 */
public class ErrorModel {

    //favor immutability when possible
    private final List<String> messages;

    @JsonCreator
    public ErrorModel(@JsonProperty("messages") List<String> messages) {
        this.messages = new ArrayList<>(messages);
    }

    public ErrorModel(String message) {
        this.messages = Collections.singletonList(message);
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                       .append("messages", messages)
                       .toString();
    }
}
