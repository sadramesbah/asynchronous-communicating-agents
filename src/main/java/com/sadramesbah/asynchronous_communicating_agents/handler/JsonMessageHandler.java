package com.sadramesbah.asynchronous_communicating_agents.handler;

import com.sadramesbah.asynchronous_communicating_agents.message.JsonMessage;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JsonMessageHandler {

  private final ObjectMapper objectMapper;

  public JsonMessageHandler() {
    this.objectMapper = new ObjectMapper();
  }

  // parses JSON message in string format and converts it to JsonMessage object
  public JsonMessage parse(String jsonMessageInString) throws JsonProcessingException {
    JsonMessage jsonMessageObject = objectMapper.readValue(jsonMessageInString, JsonMessage.class);
    if (isInvalid(jsonMessageObject)) {
      throw new JsonProcessingException("Invalid JSON message") {
      };
    }
    return jsonMessageObject;
  }

  // converts JsonMessage object to JSON message in string format
  public String toJson(JsonMessage jsonMessageObject) throws JsonProcessingException {
    if (isInvalid(jsonMessageObject)) {
      throw new JsonProcessingException("Invalid JSON message") {
      };
    }
    return objectMapper.writeValueAsString(jsonMessageObject);
  }

  // checks if JsonMessage object has the expected structure
  private boolean isInvalid(JsonMessage jsonMessageObject) {
    return jsonMessageObject == null ||
        jsonMessageObject.getMessageID() <= 0 ||
        jsonMessageObject.getMessageTitle() == null ||
        jsonMessageObject.getMessageBody() == null ||
        jsonMessageObject.getCreationTime() == null ||
        jsonMessageObject.getLastModified() == null ||
        jsonMessageObject.getLastAgent() == null ||
        jsonMessageObject.getStatus() == null;
  }
}
