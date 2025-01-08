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
  private static final String SOAP_ENVELOPE_TAG = "<soapenv:Envelope " +
      "xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">";
  private static final String SOAP_HEADER_END_TAG = "<soapenv:Header/>";
  private static final String SOAP_BODY_TAG = "<soapenv:Body>";
  private static final String MESSAGE_TAG = "<Message>";
  private static final String MESSAGE_ID = "<MessageID>106</MessageID>";
  private static final String MESSAGE_TITLE = "<MessageTitle>Test Title</MessageTitle>";
  private static final String MESSAGE_BODY = "<MessageBody>Test Body</MessageBody>";
  private static final String CREATION_TIME = "<CreationTime>2024-11-03T12:00:00Z</CreationTime>";
  private static final String LAST_MODIFIED_TIME = "<LastModified>2024-11-03T12:25:00Z</LastModified>";
  private static final String LAST_AGENT_ID = "<LastAgent>Agent-32</LastAgent>";
  private static final String ACTIVE_STATUS = "<Status>Active</Status>";
  private static final String MESSAGE_END_TAG = "</Message>";
  private static final String SOAP_BODY_END_TAG = "</soapenv:Body>";
  private static final String SOAP_ENVELOPE_END_TAG = "</soapenv:Envelope>";

  @BeforeEach
  void setUp() throws JAXBException, SOAPException {
    soapMessageHandler = new SoapMessageHandler();
  }

  @Test
  void testParseValidSoapMessage() throws SOAPException, IOException {
    String soapMessageInString = new StringBuilder()
        .append(SOAP_ENVELOPE_TAG)
        .append(SOAP_HEADER_END_TAG)
        .append(SOAP_BODY_TAG)
        .append(MESSAGE_TAG)
        .append(MESSAGE_ID)
        .append(MESSAGE_TITLE)
        .append(MESSAGE_BODY)
        .append(CREATION_TIME)
        .append(LAST_MODIFIED_TIME)
        .append(LAST_AGENT_ID)
        .append(ACTIVE_STATUS)
        .append(MESSAGE_END_TAG)
        .append(SOAP_BODY_END_TAG)
        .append(SOAP_ENVELOPE_END_TAG)
        .toString();

    SOAPMessage soapMessage = soapMessageHandler.parse(soapMessageInString);
    assertNotNull(soapMessage);
    assertNotNull(soapMessage.getSOAPBody());
    assertNull(soapMessage.getSOAPBody().getFault());
  }

  @Test
  void testParseInvalidSoapMessage() {
    String soapMessageInString = new StringBuilder()
        .append(SOAP_ENVELOPE_TAG)
        .append(SOAP_HEADER_END_TAG)
        .append(SOAP_BODY_TAG)
        .append(MESSAGE_TAG)
        .append("<MessageID>106")
        .append(MESSAGE_TITLE)
        .append(MESSAGE_BODY)
        .append(CREATION_TIME)
        .append("<LastModified>2024-11-03T12:2 Modified>")
        .append("< Agent-32</LastAgent>")
        .append(ACTIVE_STATUS)
        .append(MESSAGE_END_TAG)
        .append(SOAP_BODY_END_TAG)
        .append(SOAP_ENVELOPE_END_TAG)
        .toString();

    assertThrows(SOAPException.class, () -> soapMessageHandler.parse(soapMessageInString));
  }

  @Test
  void testToSoapString() throws SOAPException, IOException {
    String soapMessageInString = new StringBuilder()
        .append(SOAP_ENVELOPE_TAG)
        .append(SOAP_HEADER_END_TAG)
        .append(SOAP_BODY_TAG)
        .append(MESSAGE_TAG)
        .append(MESSAGE_ID)
        .append(MESSAGE_TITLE)
        .append(MESSAGE_BODY)
        .append(CREATION_TIME)
        .append(LAST_MODIFIED_TIME)
        .append(LAST_AGENT_ID)
        .append(ACTIVE_STATUS)
        .append(MESSAGE_END_TAG)
        .append(SOAP_BODY_END_TAG)
        .append(SOAP_ENVELOPE_END_TAG)
        .toString();

    SOAPMessage soapMessage = soapMessageHandler.parse(soapMessageInString);
    String soapString = soapMessageHandler.toSoapString(soapMessage);
    assertNotNull(soapString);
    assertTrue(soapString.contains(MESSAGE_ID));
    assertTrue(soapString.contains(MESSAGE_TITLE));
    assertTrue(soapString.contains(MESSAGE_BODY));
    assertTrue(soapString.contains(CREATION_TIME));
    assertTrue(soapString.contains(LAST_MODIFIED_TIME));
    assertTrue(soapString.contains(LAST_AGENT_ID));
    assertTrue(soapString.contains(ACTIVE_STATUS));
  }

  @Test
  void testParseSoapMessageWithMissingFields() {
    String soapMessageInString = new StringBuilder()
        .append(SOAP_ENVELOPE_TAG)
        .append(SOAP_HEADER_END_TAG)
        .append(SOAP_BODY_TAG)
        .append(MESSAGE_TAG)
        .append(MESSAGE_ID)
        .append(MESSAGE_TITLE)
        .append(MESSAGE_END_TAG)
        .append(SOAP_BODY_END_TAG)
        .append(SOAP_ENVELOPE_END_TAG)
        .toString();

    assertThrows(SOAPException.class, () -> soapMessageHandler.parse(soapMessageInString));
  }

  @Test
  void testParseEmptySoapMessage() {
    String soapMessageInString = "";
    assertThrows(SOAPException.class, () -> soapMessageHandler.parse(soapMessageInString));
  }
}