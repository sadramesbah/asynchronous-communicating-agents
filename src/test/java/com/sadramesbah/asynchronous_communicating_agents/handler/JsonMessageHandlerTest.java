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

  @BeforeEach
  void setUp() {
    jsonMessageHandler = new JsonMessageHandler();
  }

  @Test
  void testParseValidJson() throws IOException, InvalidJsonMessageException {
    String jsonInString =
        "{\"MessageID\":14,\"MessageTitle\":\"TestTitle\",\"MessageBody\":\"TestBody\","
            + "\"CreationTime\":\"2024-10-10T10:25:00Z\",\"LastModified\":\"2024-10-10T11:45:00Z\""
            + ",\"LastAgent\":\"Agent32\",\"Status\":\"Active\"}";
    JsonMessage jsonMessageObject = jsonMessageHandler.parse(jsonInString);
    assertNotNull(jsonMessageObject);
    assertEquals(14, jsonMessageObject.getMessageId());
    assertEquals("TestTitle", jsonMessageObject.getMessageTitle());
    assertEquals("TestBody", jsonMessageObject.getMessageBody());
    assertEquals(Timestamp.from(Instant.parse("2024-10-10T10:25:00Z")),
        jsonMessageObject.getCreationTime());
    assertEquals(Timestamp.from(Instant.parse("2024-10-10T11:45:00Z")),
        jsonMessageObject.getLastModified());
    assertEquals("Agent32", jsonMessageObject.getLastAgent());
    assertEquals("Active", jsonMessageObject.getStatus());
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
    jsonMessageObject.setMessageTitle("TestTitle");
    jsonMessageObject.setMessageBody("TestBody");
    jsonMessageObject.setCreationTime(new Timestamp(System.currentTimeMillis()));
    jsonMessageObject.setLastModified(new Timestamp(System.currentTimeMillis()));
    jsonMessageObject.setLastAgent("Agent32");
    jsonMessageObject.setStatus("Active");
    String jsonInString = jsonMessageHandler.toJsonString(jsonMessageObject);
    assertNotNull(jsonInString);
    assertTrue(jsonInString.contains("\"MessageID\":1"));
    assertTrue(jsonInString.contains("\"MessageTitle\":\"TestTitle\""));
    assertTrue(jsonInString.contains("\"MessageBody\":\"TestBody\""));
    assertTrue(jsonInString.contains("\"LastAgent\":\"Agent32\""));
    assertTrue(jsonInString.contains("\"Status\":\"Active\""));

  }

  @Test
  void testIsInvalid() {
    JsonMessage validJsonMessageObject = new JsonMessage();
    validJsonMessageObject.setMessageId(14);
    validJsonMessageObject.setMessageTitle("TestTitle");
    validJsonMessageObject.setMessageBody("TestBody");
    validJsonMessageObject.setCreationTime(new Timestamp(System.currentTimeMillis()));
    validJsonMessageObject.setLastModified(new Timestamp(System.currentTimeMillis()));
    validJsonMessageObject.setLastAgent("Agent32");
    validJsonMessageObject.setStatus("Active");
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
            + "\"LastModified\":\"2024-10-10T11:45:00Z\",\"LastAgent\":\"Agent32\","
            + "\"Status\":\"Active\"}";
    JsonMessage jsonMessageObject = jsonMessageHandler.parse(jsonInString);
    assertNotNull(jsonMessageObject);
    assertEquals(14, jsonMessageObject.getMessageId());
    assertEquals("TestTitle", jsonMessageObject.getMessageTitle());
    assertEquals("TestBody", jsonMessageObject.getMessageBody());
    assertEquals(Timestamp.from(Instant.parse("2024-10-10T10:25:00Z")),
        jsonMessageObject.getCreationTime());
    assertEquals(Timestamp.from(Instant.parse("2024-10-10T11:45:00Z")),
        jsonMessageObject.getLastModified());
    assertEquals("Agent32", jsonMessageObject.getLastAgent());
    assertEquals("Active", jsonMessageObject.getStatus());
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
