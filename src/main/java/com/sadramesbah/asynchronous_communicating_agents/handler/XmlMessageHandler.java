package com.sadramesbah.asynchronous_communicating_agents.handler;

import com.sadramesbah.asynchronous_communicating_agents.message.XmlMessage;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

@Component
public class XmlMessageHandler {

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
    XmlMessage xmlMessageObject = (XmlMessage) unmarshaller.unmarshal(
        new StringReader(xmlMessageInString));
    if (isInvalid(xmlMessageObject)) {
      throw new JAXBException("Invalid XML message");
    }
    return xmlMessageObject;
  }

  // converts XmlMessage object to XML message in string format
  public String toXml(XmlMessage xmlMessageObject) throws JAXBException {
    if (isInvalid(xmlMessageObject)) {
      throw new JAXBException("Invalid XML message");
    }
    StringWriter writer = new StringWriter();
    marshaller.marshal(xmlMessageObject, writer);
    return writer.toString();
  }

  // checks if XmlMessage object has the expected structure
  boolean isInvalid(XmlMessage xmlMessageObject) {
    if (xmlMessageObject == null) {
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