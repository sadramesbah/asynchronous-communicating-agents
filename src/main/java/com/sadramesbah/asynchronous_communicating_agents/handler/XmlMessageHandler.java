package com.sadramesbah.asynchronous_communicating_agents.handler;

import com.sadramesbah.asynchronous_communicating_agents.message.XmlMessage;
import org.springframework.stereotype.Component;
import java.util.stream.Stream;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class XmlMessageHandler {

  private static final Logger logger = LoggerFactory.getLogger(XmlMessageHandler.class);
  private final Marshaller marshaller;
  private final Unmarshaller unmarshaller;

  public XmlMessageHandler() throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(XmlMessage.class);
    this.marshaller = jaxbContext.createMarshaller();
    this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    this.unmarshaller = jaxbContext.createUnmarshaller();
  }

  // parses XML message in string format and converts it to XmlMessage object
  public XmlMessage parse(String xmlMessageInString) throws JAXBException {
    logger.info("Parsing XML message from string.");
    XmlMessage xmlMessageObject = (XmlMessage) unmarshaller.unmarshal(
        new StringReader(xmlMessageInString));
    if (isInvalidXmlMessage(xmlMessageObject)) {
      logger.warn("Invalid XML message structure occurred while parsing. MessageID: {}, Agent: {}",
          xmlMessageObject.getMessageId(), xmlMessageObject.getLastAgent());
      throw new JAXBException("Invalid XML message");
    }
    logger.info("Parsed XML message successfully. MessageID: {}, Agent: {}",
        xmlMessageObject.getMessageId(), xmlMessageObject.getLastAgent());
    return xmlMessageObject;
  }

  // converts XmlMessage object to XML message in string format
  public String toXmlString(XmlMessage xmlMessageObject) throws JAXBException {
    logger.info("Converting XmlMessage object to XML string. MessageID: {}, Agent: {}",
        xmlMessageObject.getMessageId(), xmlMessageObject.getLastAgent());
    if (isInvalidXmlMessage(xmlMessageObject)) {
      logger.warn(
          "Invalid XML message structure occurred while converting to string. MessageID: {}, Agent: {}",
          xmlMessageObject.getMessageId(), xmlMessageObject.getLastAgent());
      throw new JAXBException("Invalid XML message");
    }
    StringWriter stringWriter = new StringWriter();
    marshaller.marshal(xmlMessageObject, stringWriter);
    return stringWriter.toString();
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
}