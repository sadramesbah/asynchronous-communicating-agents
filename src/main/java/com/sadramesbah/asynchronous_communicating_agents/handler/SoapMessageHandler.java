package com.sadramesbah.asynchronous_communicating_agents.handler;

import com.sadramesbah.asynchronous_communicating_agents.message.XmlMessage;
import org.springframework.stereotype.Component;
import java.util.stream.Stream;
import jakarta.xml.soap.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SoapMessageHandler {

  private static final Logger logger = LoggerFactory.getLogger(SoapMessageHandler.class);
  private static final String NAMESPACE_URI = "https://realuri.example.com/security";
  private static final String SECURITY_TOKEN_HEADER = "SecurityToken";
  private static final String DEFAULT_SECURITY_TOKEN = "123456789";

  private final Unmarshaller unmarshaller;
  private final MessageFactory messageFactory;

  public SoapMessageHandler() throws JAXBException, SOAPException {
    JAXBContext jaxbContext = JAXBContext.newInstance(XmlMessage.class);
    this.unmarshaller = jaxbContext.createUnmarshaller();
    this.messageFactory = MessageFactory.newInstance();
  }

  // parses SOAP message in string format and converts it to SOAPMessage object
  public SOAPMessage parse(String soapMessageInString) throws IOException, SOAPException {
    logger.info("Parsing SOAP message from string.");
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(
        soapMessageInString.getBytes(StandardCharsets.UTF_8))) {
      SOAPMessage soapMessageObject = messageFactory.createMessage(null, inputStream);
      validateSoapMessage(soapMessageObject);
      addSecurityToken(soapMessageObject.getSOAPHeader());
      XmlMessage innerXmlMessage = extractInnerXmlMessage(soapMessageObject);
      logger.info("Parsed SOAP message successfully. MessageID: {}, Agent: {}",
          innerXmlMessage.getMessageId(), innerXmlMessage.getLastAgent());
      return soapMessageObject;
    } catch (SOAPException | IOException exception) {
      logger.error("Error parsing SOAP message: {}", exception.getMessage(), exception);
      throw exception;
    }
  }

  // converts SOAPMessage object to SOAP message in string format
  public String toSoapString(SOAPMessage soapMessageObject) throws SOAPException, IOException {
    logger.info("Converting SOAPMessage to string format.");
    validateSoapMessage(soapMessageObject);
    XmlMessage innerXmlMessage = extractInnerXmlMessage(soapMessageObject);
    logger.info("Converting SOAPMessage to string format. MessageID: {}, Agent: {}",
        innerXmlMessage.getMessageId(), innerXmlMessage.getLastAgent());
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      soapMessageObject.writeTo(outputStream);
      return outputStream.toString(StandardCharsets.UTF_8);
    } catch (SOAPException | IOException exception) {
      logger.error("Error converting SOAPMessage to string: {}", exception.getMessage(), exception);
      throw exception;
    }
  }

  // extracts the inner XML message from the SOAP message and validates its structure
  private void validateSoapMessage(SOAPMessage soapMessage) throws SOAPException {
    SOAPBody body = soapMessage.getSOAPBody();
    if (body == null || body.getFault() != null) {
      logger.warn("SOAP body is invalid or contains a fault.");
      throw new SOAPException("Invalid SOAP message structure.");
    }

    try {
      Node messageNode = (Node) body.getElementsByTagNameNS("*", "Message").item(0);
      if (messageNode == null) {
        logger.warn("Message element is missing in SOAP body.");
        throw new SOAPException("Invalid SOAP message structure.");
      }

      XmlMessage innerXmlMessage = (XmlMessage) unmarshaller.unmarshal(messageNode);
      if (innerXmlMessage == null || isInvalidXmlMessage(innerXmlMessage)) {
        throw new SOAPException("Invalid SOAP message structure.");
      }
    } catch (JAXBException exception) {
      logger.error("Error unmarshalling SOAP body content: {}", exception.getMessage(), exception);
      throw new SOAPException("Error unmarshalling SOAP body content.", exception);
    }
  }

  // extracts the inner XML message from the SOAP message
  private XmlMessage extractInnerXmlMessage(SOAPMessage soapMessage) throws SOAPException {
    try {
      SOAPBody body = soapMessage.getSOAPBody();
      Node messageNode = (Node) body.getElementsByTagNameNS("*", "Message").item(0);
      return (XmlMessage) unmarshaller.unmarshal(messageNode);
    } catch (JAXBException exception) {
      logger.error("Error extracting inner XML message: {}", exception.getMessage(), exception);
      throw new SOAPException("Error extracting inner XML message.", exception);
    }
  }

  // checks if XmlMessage object has the expected structure
  boolean isInvalidXmlMessage(XmlMessage xmlMessageObject) {
    if (xmlMessageObject == null) {
      logger.warn("XmlMessage object is null.");
      return true;
    }

    return Stream.of(
        xmlMessageObject.getMessageId() <= 0,
        isMissingNullOrEmpty(xmlMessageObject.getMessageTitle()),
        isMissingNullOrEmpty(xmlMessageObject.getMessageBody()),
        xmlMessageObject.getCreationTime() == null,
        xmlMessageObject.getLastModified() == null,
        isMissingNullOrEmpty(xmlMessageObject.getLastAgent()),
        isMissingNullOrEmpty(xmlMessageObject.getStatus())
    ).anyMatch(Boolean::booleanValue);
  }

  private boolean isMissingNullOrEmpty(String value) {
    return value == null || value.isEmpty() || "null".equals(value);
  }

  private void addSecurityToken(SOAPHeader header) throws SOAPException {
    logger.info("Adding SecurityToken to the SOAP header.");
    SOAPFactory soapFactory = SOAPFactory.newInstance();
    Name securityTokenName = soapFactory.createName(SECURITY_TOKEN_HEADER, "", NAMESPACE_URI);
    SOAPHeaderElement securityTokenElement = header.addHeaderElement(securityTokenName);
    securityTokenElement.setTextContent(DEFAULT_SECURITY_TOKEN);
  }
}