package com.sadramesbah.asynchronous_communicating_agents.handler;

import com.sadramesbah.asynchronous_communicating_agents.message.XmlMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.xml.bind.JAXBException;
import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class XmlMessageHandlerTest {

  private XmlMessageHandler xmlMessageHandler;

  @BeforeEach
  void setUp() throws JAXBException {
    xmlMessageHandler = new XmlMessageHandler();
  }

  @Test
  void testParseValidXml() throws JAXBException {
    String xmlInString =
        "<Message><MessageID>14</MessageID><MessageTitle>TestTitle</MessageTitle>"
            + "<MessageBody>TestBody</MessageBody><CreationTime>2024-10-10T10:25:00Z</CreationTime>"
            + "<LastModified>2024-10-10T11:45:00Z</LastModified><LastAgent>Agent32</LastAgent>"
            + "<Status>Active</Status></Message>";
    XmlMessage xmlMessageObject = xmlMessageHandler.parse(xmlInString);
    assertNotNull(xmlMessageObject);
    assertEquals(14, xmlMessageObject.getMessageId());
    assertEquals("TestTitle", xmlMessageObject.getMessageTitle());
    assertEquals("TestBody", xmlMessageObject.getMessageBody());
    assertEquals(Timestamp.from(Instant.parse("2024-10-10T10:25:00Z")),
        xmlMessageObject.getCreationTime());
    assertEquals(Timestamp.from(Instant.parse("2024-10-10T11:45:00Z")),
        xmlMessageObject.getLastModified());
    assertEquals("Agent32", xmlMessageObject.getLastAgent());
    assertEquals("Active", xmlMessageObject.getStatus());
  }

  @Test
  void testParseInvalidXmlWithNullAsValue() {
    String xml =
        "<Message><MessageID>14</MessageID><MessageTitle>TestTitle</MessageTitle>"
            + "<MessageBody>TestBody</MessageBody><CreationTime>2024-10-10T10:25:00Z</CreationTime>"
            + "<LastModified>2024-10-10T11:45:00Z</LastModified><LastAgent>null</LastAgent>"
            + "<Status>Active</Status></Message>";
    assertThrows(JAXBException.class, () -> xmlMessageHandler.parse(xml));
  }

  @Test
  void testParseInvalidXmlWithEmptyFieldValue() {
    String xml =
        "<Message><MessageID>14</MessageID><MessageTitle>null</MessageTitle>"
            + "<MessageBody></MessageBody><CreationTime>2024-10-10T10:25:00Z</CreationTime>"
            + "<LastModified>2024-10-10T11:45:00Z</LastModified><LastAgent>Agent32</LastAgent>"
            + "<Status>Active</Status></Message>";
    assertThrows(JAXBException.class, () -> xmlMessageHandler.parse(xml));
  }

  @Test
  void testConvertingXmlObjectToString() throws JAXBException {
    XmlMessage xmlMessageObject = new XmlMessage();
    xmlMessageObject.setMessageId(14);
    xmlMessageObject.setMessageTitle("TestTitle");
    xmlMessageObject.setMessageBody("TestBody");
    xmlMessageObject.setCreationTime(Timestamp.from(Instant.now()));
    xmlMessageObject.setLastModified(Timestamp.from(Instant.now()));
    xmlMessageObject.setLastAgent("Agent32");
    xmlMessageObject.setStatus("Active");
    String xmlInString = xmlMessageHandler.toXmlString(xmlMessageObject);
    assertNotNull(xmlInString);
    assertTrue(xmlInString.contains("<MessageID>14</MessageID>"));
    assertTrue(xmlInString.contains("<MessageTitle>TestTitle</MessageTitle>"));
    assertTrue(xmlInString.contains("<MessageBody>TestBody</MessageBody>"));
    assertTrue(xmlInString.contains("<LastAgent>Agent32</LastAgent>"));
    assertTrue(xmlInString.contains("<Status>Active</Status>"));
  }

  @Test
  void testIsInvalid() {
    XmlMessage validXmlMessageObject = new XmlMessage();
    validXmlMessageObject.setMessageId(14);
    validXmlMessageObject.setMessageTitle("TestTitle");
    validXmlMessageObject.setMessageBody("TestBody");
    validXmlMessageObject.setCreationTime(Timestamp.from(Instant.now()));
    validXmlMessageObject.setLastModified(Timestamp.from(Instant.now()));
    validXmlMessageObject.setLastAgent("Agent32");
    validXmlMessageObject.setStatus("Active");
    assertFalse(xmlMessageHandler.isInvalidXmlMessage(validXmlMessageObject));

    XmlMessage invalidXmlMessageObject = new XmlMessage();
    invalidXmlMessageObject.setMessageBody(null);
    assertTrue(xmlMessageHandler.isInvalidXmlMessage(invalidXmlMessageObject));
  }

  @Test
  void testParseEmptyXml() {
    String xml = "<Message></Message>";
    assertThrows(JAXBException.class, () -> xmlMessageHandler.parse(xml));
  }

  @Test
  void testParseXmlWithAdditionalFields() throws JAXBException {
    String xmlInString =
        "<Message><MessageID>14</MessageID><MessageTitle>TestTitle</MessageTitle>"
            + "<MessageBody>TestBody</MessageBody><ExtraField>ExtraValue</ExtraField>"
            + "<CreationTime>2024-10-10T10:25:00Z</CreationTime>"
            + "<LastModified>2024-10-10T11:45:00Z</LastModified><LastAgent>Agent32</LastAgent>"
            + "<Status>Active</Status></Message>";
    XmlMessage xmlMessageObject = xmlMessageHandler.parse(xmlInString);
    assertNotNull(xmlMessageObject);
    assertEquals(14, xmlMessageObject.getMessageId());
    assertEquals("TestTitle", xmlMessageObject.getMessageTitle());
    assertEquals("TestBody", xmlMessageObject.getMessageBody());
    assertEquals(Timestamp.from(Instant.parse("2024-10-10T10:25:00Z")),
        xmlMessageObject.getCreationTime());
    assertEquals(Timestamp.from(Instant.parse("2024-10-10T11:45:00Z")),
        xmlMessageObject.getLastModified());
    assertEquals("Agent32", xmlMessageObject.getLastAgent());
    assertEquals("Active", xmlMessageObject.getStatus());
  }

  @Test
  void testToXmlWithNullFields() {
    XmlMessage xmlMessageObject = new XmlMessage();
    xmlMessageObject.setMessageId(1);
    xmlMessageObject.setMessageTitle(null);
    xmlMessageObject.setMessageBody(null);
    xmlMessageObject.setCreationTime(null);
    xmlMessageObject.setLastModified(null);
    xmlMessageObject.setLastAgent(null);
    xmlMessageObject.setStatus(null);
    assertThrows(JAXBException.class, () -> xmlMessageHandler.toXmlString(xmlMessageObject));
  }
}