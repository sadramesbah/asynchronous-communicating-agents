package com.sadramesbah.asynchronous_communicating_agents.agent;

import com.sadramesbah.asynchronous_communicating_agents.handler.JsonMessageHandler;
import com.sadramesbah.asynchronous_communicating_agents.message.JsonMessage;
import com.sadramesbah.asynchronous_communicating_agents.message.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.soap.SOAPException;
import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class MessagingAgentTest {

  private MessagingAgent messagingAgent;
  private static final String AGENT_ID = "Agent-319";

  @BeforeEach
  void setUp() throws JAXBException, SOAPException {
    messagingAgent = new MessagingAgent(AGENT_ID);
  }

  @Test
  void testHandleSoapMessage() throws Exception {
    String soapMessageInString = new StringBuilder()
        .append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">")
        .append("<soapenv:Header/>")
        .append("<soapenv:Body>")
        .append("<Message>")
        .append("<MessageID>10852</MessageID>")
        .append("<MessageTitle>Test Title</MessageTitle>")
        .append("<MessageBody>Test Body</MessageBody>")
        .append("<CreationTime>2024-12-20T12:00:00Z</CreationTime>")
        .append("<LastModified>2024-12-20T12:45:00Z</LastModified>")
        .append("<LastAgent>Agent-108</LastAgent>")
        .append("<Status>Active</Status>")
        .append("</Message>")
        .append("</soapenv:Body>")
        .append("</soapenv:Envelope>")
        .toString();

    String result = messagingAgent.handleInputMessage(soapMessageInString, MessageType.SOAP);

    assertNotNull(result);
    assertTrue(result.contains("Test Body Processed by Agent: " + AGENT_ID));
    assertTrue(result.contains("<LastAgent>" + AGENT_ID + "</LastAgent>"));
  }

  @Test
  void testHandleXmlMessage() throws Exception {
    String xmlMessage =
        "<Message><MessageID>10853</MessageID><MessageTitle>Test Title</MessageTitle>"
            + "<MessageBody>Test Body</MessageBody><CreationTime>2024-12-20T10:25:00Z</CreationTime>"
            + "<LastModified>2024-12-20T11:45:00Z</LastModified><LastAgent>Agent-109</LastAgent>"
            + "<Status>Active</Status></Message>";

    String result = messagingAgent.handleInputMessage(xmlMessage, MessageType.XML);

    assertNotNull(result);
    assertTrue(result.contains("<MessageBody>Test Body Processed by Agent: " + AGENT_ID));
    assertTrue(result.contains("<LastAgent>" + AGENT_ID + "</LastAgent>"));
  }

  @Test
  void testHandleJsonMessage() throws Exception {
    String jsonMessage =
        "{\"MessageID\":10854,\"MessageTitle\":\"Test Title\",\"MessageBody\":\"Test Body\","
            + "\"CreationTime\":\"2024-12-20T11:25:00Z\",\"LastModified\":\"2024-12-20T11:50:00Z\""
            + ",\"LastAgent\":\"Agent-108\",\"Status\":\"Active\"}";

    String result = messagingAgent.handleInputMessage(jsonMessage, MessageType.JSON);

    assertNotNull(result);
    assertTrue(result.contains("\"MessageBody\":\"Test Body Processed by Agent: " + AGENT_ID));
    assertTrue(result.contains("\"LastAgent\":\"" + AGENT_ID + "\""));
  }

  @Test
  void testHandleInvalidSoapMessage() {
    String invalidSoapMessage = new StringBuilder()
        .append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">")
        .append("<soapenv:Body>")
        .append("<InvalidFormat>")
        .append("</InvalidFormat>")
        .append("</soapenv:Body>")
        .append("</soapenv:Envelope>")
        .toString();

    assertThrows(Exception.class, () ->
        messagingAgent.handleInputMessage(invalidSoapMessage, MessageType.SOAP));
  }

  @Test
  void testHandleInvalidXmlMessage() {
    String invalidXml =
        "<Message><MessageID>10855</MessageID><MessageTitle>null</MessageTitle>"
            + "<MessageBody></MessageBody><CreationTime>2024-12-20T11:50:00Z</CreationTime>"
            + "<LastModified>2024-12-20T13:30:00Z</LastModified><LastAgent>null</LastAgent>"
            + "<Status>Active</Status></Message>";

    assertThrows(Exception.class, () ->
        messagingAgent.handleInputMessage(invalidXml, MessageType.XML));
  }

  @Test
  void testHandleInvalidJsonMessage() {
    String invalidJson =
        "{\"MessageID\":10856,\"MessageTitle\":null,\"MessageBody\":\"Test Body\","
            + "\"CreationTime\":\"2024-12-20T14:25:00Z\",\"LastModified\":\"2024-12-20T14:40:00Z\""
            + ",\"LastAgent\":null,\"Status\":\"Active\"}";

    assertThrows(Exception.class, () ->
        messagingAgent.handleInputMessage(invalidJson, MessageType.JSON));
  }

  @Test
  void testGetAgentId() {
    assertEquals(AGENT_ID, messagingAgent.getId());
  }

  @Test
  void testHandleEmptyMessage() {
    String emptyMessage = "";
    assertThrows(Exception.class, () ->
        messagingAgent.handleInputMessage(emptyMessage, MessageType.JSON));
    assertThrows(Exception.class, () ->
        messagingAgent.handleInputMessage(emptyMessage, MessageType.XML));
    assertThrows(Exception.class, () ->
        messagingAgent.handleInputMessage(emptyMessage, MessageType.SOAP));
  }

  @Test
  void testHandleNullMessage() {
    assertThrows(Exception.class, () ->
        messagingAgent.handleInputMessage(null, MessageType.JSON));
    assertThrows(Exception.class, () ->
        messagingAgent.handleInputMessage(null, MessageType.XML));
    assertThrows(Exception.class, () ->
        messagingAgent.handleInputMessage(null, MessageType.SOAP));
  }

  @Test
  void testMessageTimestampUpdate() throws Exception {
    String jsonMessage =
        "{\"MessageID\":10856,\"MessageTitle\":\"Test Title\",\"MessageBody\":\"Test Body\","
            + "\"CreationTime\":\"2024-12-20T16:15:00Z\",\"LastModified\":\"2024-12-20T16:35:00Z\""
            + ",\"LastAgent\":\"Agent-108\",\"Status\":\"Active\"}";

    String result = messagingAgent.handleInputMessage(jsonMessage, MessageType.JSON);
    Timestamp originalTimestamp = Timestamp.from(Instant.parse("2024-12-20T16:35:00Z"));
    JsonMessage updatedMessage = new JsonMessageHandler().parse(result);
    assertNotNull(updatedMessage.getLastModified());
    assertTrue(updatedMessage.getLastModified().after(originalTimestamp),
        "LastModified timestamp should be updated to a more recent time.");
  }
}