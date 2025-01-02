package com.sadramesbah.asynchronous_communicating_agents.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SoapMessageHandlerTest {

  private SoapMessageHandler soapMessageHandler;

  @BeforeEach
  void setUp() throws JAXBException, SOAPException {
    soapMessageHandler = new SoapMessageHandler();
  }

  @Test
  void testParseValidSoapMessage() throws SOAPException, IOException {
    String soapMessageInString = new StringBuilder()
        .append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">")
        .append("<soapenv:Header/>")
        .append("<soapenv:Body>")
        .append("<Message>")
        .append("<MessageID>106</MessageID>")
        .append("<MessageTitle>Test Title</MessageTitle>")
        .append("<MessageBody>Test Body</MessageBody>")
        .append("<CreationTime>2024-11-03T12:00:00Z</CreationTime>")
        .append("<LastModified>2024-11-03T12:25:00Z</LastModified>")
        .append("<LastAgent>Agent32</LastAgent>")
        .append("<Status>Active</Status>")
        .append("</Message>")
        .append("</soapenv:Body>")
        .append("</soapenv:Envelope>")
        .toString();

    SOAPMessage soapMessage = soapMessageHandler.parse(soapMessageInString);
    assertNotNull(soapMessage);
    assertNotNull(soapMessage.getSOAPBody());
    assertNull(soapMessage.getSOAPBody().getFault());
  }

  @Test
  void testParseInvalidSoapMessage() {
    String soapMessageInString = new StringBuilder()
        .append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">")
        .append("<soapenv:Header/>")
        .append("<soapenv:Body>")
        .append("<Message>")
        .append("<MessageID>106")
        .append("<MessageTitle>Test Title</MessageTitle>")
        .append("<MessageBody>Test Body</MessageBody>")
        .append("<CreationTime>2024-11-03T12:00:00Z</CreationTime>")
        .append("<LastModified>2024-11-03T12:2 Modified>")
        .append("< Agent32</LastAgent>")
        .append("<Status>Active</Status>")
        .append("</Message>")
        .append("</soapenv:Body>")
        .append("</soapenv:Envelope>")
        .toString();

    assertThrows(SOAPException.class, () -> soapMessageHandler.parse(soapMessageInString));
  }

  @Test
  void testToSoapString() throws SOAPException, IOException {
    String soapMessageInString = new StringBuilder()
        .append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">")
        .append("<soapenv:Header/>")
        .append("<soapenv:Body>")
        .append("<Message>")
        .append("<MessageID>106</MessageID>")
        .append("<MessageTitle>Test Title</MessageTitle>")
        .append("<MessageBody>Test Body</MessageBody>")
        .append("<CreationTime>2024-11-03T12:00:00Z</CreationTime>")
        .append("<LastModified>2024-11-03T12:25:00Z</LastModified>")
        .append("<LastAgent>Agent32</LastAgent>")
        .append("<Status>Active</Status>")
        .append("</Message>")
        .append("</soapenv:Body>")
        .append("</soapenv:Envelope>")
        .toString();

    SOAPMessage soapMessage = soapMessageHandler.parse(soapMessageInString);
    String soapString = soapMessageHandler.toSoapString(soapMessage);
    assertNotNull(soapString);
    assertTrue(soapString.contains("<MessageID>106</MessageID>"));
    assertTrue(soapString.contains("<MessageTitle>Test Title</MessageTitle>"));
    assertTrue(soapString.contains("<MessageBody>Test Body</MessageBody>"));
    assertTrue(soapString.contains("<CreationTime>2024-11-03T12:00:00Z</CreationTime>"));
    assertTrue(soapString.contains("<LastModified>2024-11-03T12:25:00Z</LastModified>"));
    assertTrue(soapString.contains("<LastAgent>Agent32</LastAgent>"));
    assertTrue(soapString.contains("<Status>Active</Status>"));
  }

  @Test
  void testParseSoapMessageWithMissingFields() {
    String soapMessageInString = new StringBuilder()
        .append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">")
        .append("<soapenv:Header/>")
        .append("<soapenv:Body>")
        .append("<Message>")
        .append("<MessageID>106</MessageID>")
        .append("<MessageTitle>Test Title</MessageTitle>")
        .append("</Message>")
        .append("</soapenv:Body>")
        .append("</soapenv:Envelope>")
        .toString();

    assertThrows(SOAPException.class, () -> soapMessageHandler.parse(soapMessageInString));
  }

  @Test
  void testParseEmptySoapMessage() {
    String soapMessageInString = "";
    assertThrows(SOAPException.class, () -> soapMessageHandler.parse(soapMessageInString));
  }
}