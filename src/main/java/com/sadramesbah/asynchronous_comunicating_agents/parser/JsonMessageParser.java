package com.sadramesbah.asynchronous_comunicating_agents.parser;

import com.sadramesbah.asynchronous_comunicating_agents.config.JsonMessageConfig;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

@Component
public class JsonMessageParser {

  private final JsonMessageConfig jsonConfig;
  private final ObjectMapper objectMapper;

  public JsonMessageParser(JsonMessageConfig jsonConfig) {
    this.jsonConfig = jsonConfig;
    this.objectMapper = new ObjectMapper();
  }

  // parses the JSON message and returns the JsonNode object
  public JsonNode parse(String jsonMessage) throws IOException {
    JsonNode jsonNode = objectMapper.readTree(jsonMessage);
    if (!isValid(jsonNode)) {
      throw new IOException("Invalid JSON message");
    }
    return jsonNode;
  }

  // checks if JSON message contains all required fields
  private boolean isValid(JsonNode jsonNode) {
    return jsonConfig.getFields().stream()
        .allMatch(field -> jsonNode.has(field.getName()));
  }
}
