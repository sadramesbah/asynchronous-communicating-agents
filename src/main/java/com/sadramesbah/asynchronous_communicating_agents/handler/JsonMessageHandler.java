package com.sadramesbah.asynchronous_communicating_agents.handler;

import com.sadramesbah.asynchronous_communicating_agents.message.JsonMessage;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JsonMessageHandler {

  private static final Logger logger = LoggerFactory.getLogger(JsonMessageHandler.class);
  private final ObjectMapper objectMapper;

  public JsonMessageHandler() {
    this.objectMapper = new ObjectMapper();
    // allows to ignore unknown properties in JSON message
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  // parses JSON message in string format and converts it to JsonMessage object
  public JsonMessage parse(String jsonMessageInString) throws JsonProcessingException {
    logger.info("Parsing JSON message from string.");
    JsonMessage jsonMessageObject = objectMapper.readValue(jsonMessageInString, JsonMessage.class);
    if (isInvalidJsonMessage(jsonMessageObject)) {
      logger.warn("Invalid JSON message structure occurred while parsing. MessageID: {}, Agent: {}",
          jsonMessageObject.getMessageId(), jsonMessageObject.getLastAgent());
      throw new JsonProcessingException("Invalid JSON message") {
      };
    }
    logger.info("Parsed JSON message successfully. MessageID: {}, Agent: {}",
        jsonMessageObject.getMessageId(), jsonMessageObject.getLastAgent());
    return jsonMessageObject;
  }

  // converts JsonMessage object to JSON message in string format
  public String toJsonString(JsonMessage jsonMessageObject) throws JsonProcessingException {
    logger.info("Converting JsonMessage object to JSON string. MessageID: {}, Agent: {}",
        jsonMessageObject.getMessageId(), jsonMessageObject.getLastAgent());
    if (isInvalidJsonMessage(jsonMessageObject)) {
      logger.warn(
          "Invalid JSON message structure occurred while converting to string. MessageID: {}, Agent: {}",
          jsonMessageObject.getMessageId(), jsonMessageObject.getLastAgent());
      throw new JsonProcessingException("Invalid JSON message") {
      };
    }
    return objectMapper.writeValueAsString(jsonMessageObject);
  }

  // checks if JsonMessage object has the expected structure
  boolean isInvalidJsonMessage(JsonMessage jsonMessageObject) {
    if (jsonMessageObject == null) {
      logger.warn("JsonMessage object is null.");
      return true;
    }

    return Stream.of(
        jsonMessageObject.getMessageId() <= 0,
        isMissingNullOrEmpty(jsonMessageObject.getMessageTitle()),
        isMissingNullOrEmpty(jsonMessageObject.getMessageBody()),
        jsonMessageObject.getCreationTime() == null,
        jsonMessageObject.getLastModified() == null,
        isMissingNullOrEmpty(jsonMessageObject.getLastAgent()),
        isMissingNullOrEmpty(jsonMessageObject.getStatus())
    ).anyMatch(Boolean::booleanValue);
  }

  private boolean isMissingNullOrEmpty(String value) {
    return value == null || value.isEmpty() || "null".equals(value);
  }
}