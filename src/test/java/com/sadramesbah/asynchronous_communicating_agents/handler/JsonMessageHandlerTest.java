package com.sadramesbah.asynchronous_communicating_agents.handler;

import com.sadramesbah.asynchronous_communicating_agents.exception.InvalidJsonMessageException;
import com.sadramesbah.asynchronous_communicating_agents.message.JsonMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.io.IOException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class JsonMessageHandlerTest {

  private JsonMessageHandler jsonMessageHandler;
  private static final String ACTIVE_STATUS = "Active";
  private static final String MESSAGE_TITLE = "TestTitle";
  private static final String MESSAGE_BODY = "TestBody";
  private static final String LAST_AGENT_ID = "Agent-32";

  @BeforeEach
  void setUp() {
    jsonMessageHandler = new JsonMessageHandler();
  }

  @Test
  void testParseValidJson() throws IOException, InvalidJsonMessageException {
    String jsonInString =
        "{\"MessageID\":14,\"MessageTitle\":\"TestTitle\",\"MessageBody\":\"TestBody\","
            + "\"CreationTime\":\"2024-10-10T10:25:00Z\",\"LastModified\":\"2024-10-10T11:45:00Z\""
            + ",\"LastAgent\":\"Agent-32\",\"Status\":\"Active\"}";
    JsonMessage jsonMessageObject = jsonMessageHandler.parse(jsonInString);
    assertNotNull(jsonMessageObject);
    assertEquals(14, jsonMessageObject.getMessageId());
    assertEquals(MESSAGE_TITLE, jsonMessageObject.getMessageTitle());
    assertEquals(MESSAGE_BODY, jsonMessageObject.getMessageBody());
    assertEquals(Timestamp.from(Instant.parse("2024-10-10T10:25:00Z")),
        jsonMessageObject.getCreationTime());
    assertEquals(Timestamp.from(Instant.parse("2024-10-10T11:45:00Z")),
        jsonMessageObject.getLastModified());
    assertEquals(LAST_AGENT_ID, jsonMessageObject.getLastAgent());
    assertEquals(ACTIVE_STATUS, jsonMessageObject.getStatus());
  }

  @Test
  void testParseInvalidJson() {
    String json = "{\"MessageID\":14,\"MessageTitle\":null\",\"MessageBody\":\"TestBody\","
        + "\"CreationTime\":\"2024-10-10T10:25:00Z\",\"LastModified\":\"2024-10-10T11:45:00Z\""
        + ",\"LastAgent\":null\",\"Status\":\"Active\"}";
    assertThrows(IOException.class, () -> jsonMessageHandler.parse(json));
  }

  @Test
  void testConvertingJsonObjectToString() throws IOException, InvalidJsonMessageException {
    JsonMessage jsonMessageObject = new JsonMessage();
    jsonMessageObject.setMessageId(14);
    jsonMessageObject.setMessageTitle(MESSAGE_TITLE);
    jsonMessageObject.setMessageBody(MESSAGE_BODY);
    jsonMessageObject.setCreationTime(new Timestamp(System.currentTimeMillis()));
    jsonMessageObject.setLastModified(new Timestamp(System.currentTimeMillis()));
    jsonMessageObject.setLastAgent(LAST_AGENT_ID);
    jsonMessageObject.setStatus(ACTIVE_STATUS);
    String jsonInString = jsonMessageHandler.toJsonString(jsonMessageObject);
    assertNotNull(jsonInString);
    assertTrue(jsonInString.contains("\"MessageID\":1"));
    assertTrue(jsonInString.contains("\"MessageTitle\":\"TestTitle\""));
    assertTrue(jsonInString.contains("\"MessageBody\":\"TestBody\""));
    assertTrue(jsonInString.contains("\"LastAgent\":\"Agent-32\""));
    assertTrue(jsonInString.contains("\"Status\":\"Active\""));

  }

  @Test
  void testIsInvalid() {
    JsonMessage validJsonMessageObject = new JsonMessage();
    validJsonMessageObject.setMessageId(14);
    validJsonMessageObject.setMessageTitle(MESSAGE_TITLE);
    validJsonMessageObject.setMessageBody(MESSAGE_BODY);
    validJsonMessageObject.setCreationTime(new Timestamp(System.currentTimeMillis()));
    validJsonMessageObject.setLastModified(new Timestamp(System.currentTimeMillis()));
    validJsonMessageObject.setLastAgent(LAST_AGENT_ID);
    validJsonMessageObject.setStatus(ACTIVE_STATUS);
    assertFalse(jsonMessageHandler.isInvalidJsonMessage(validJsonMessageObject));

    JsonMessage invalidJsonMessageObject = new JsonMessage();
    invalidJsonMessageObject.setMessageBody(null);
    assertTrue(jsonMessageHandler.isInvalidJsonMessage(invalidJsonMessageObject));
  }

  @Test
  void testParseEmptyJson() {
    String json = "{}";
    assertThrows(InvalidJsonMessageException.class, () -> jsonMessageHandler.parse(json));
  }

  @Test
  void testParseJsonWithAdditionalFields() throws IOException, InvalidJsonMessageException {
    String jsonInString =
        "{\"MessageID\":14,\"MessageTitle\":\"TestTitle\",\"MessageBody\":\"TestBody\","
            + "\"ExtraField\":\"ExtraValue\",\"CreationTime\":\"2024-10-10T10:25:00Z\","
            + "\"LastModified\":\"2024-10-10T11:45:00Z\",\"LastAgent\":\"Agent-32\","
            + "\"Status\":\"Active\"}";
    JsonMessage jsonMessageObject = jsonMessageHandler.parse(jsonInString);
    assertNotNull(jsonMessageObject);
    assertEquals(14, jsonMessageObject.getMessageId());
    assertEquals(MESSAGE_TITLE, jsonMessageObject.getMessageTitle());
    assertEquals(MESSAGE_BODY, jsonMessageObject.getMessageBody());
    assertEquals(Timestamp.from(Instant.parse("2024-10-10T10:25:00Z")),
        jsonMessageObject.getCreationTime());
    assertEquals(Timestamp.from(Instant.parse("2024-10-10T11:45:00Z")),
        jsonMessageObject.getLastModified());
    assertEquals(LAST_AGENT_ID, jsonMessageObject.getLastAgent());
    assertEquals(ACTIVE_STATUS, jsonMessageObject.getStatus());
  }

  @Test
  void testToJsonWithNullFields() {
    JsonMessage jsonMessageObject = new JsonMessage();
    jsonMessageObject.setMessageId(1);
    jsonMessageObject.setMessageTitle(null);
    jsonMessageObject.setMessageBody(null);
    jsonMessageObject.setCreationTime(null);
    jsonMessageObject.setLastModified(null);
    jsonMessageObject.setLastAgent(null);
    jsonMessageObject.setStatus(null);
    assertThrows(InvalidJsonMessageException.class,
        () -> jsonMessageHandler.toJsonString(jsonMessageObject));
  }
}
