package com.sadramesbah.asynchronous_communicating_agents.handler;

import com.sadramesbah.asynchronous_communicating_agents.message.XmlMessage;
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
  private boolean isInvalid(XmlMessage xmlMessageObject) {
    return xmlMessageObject == null ||
        xmlMessageObject.getMessageId() <= 0 ||
        xmlMessageObject.getMessageTitle() == null ||
        xmlMessageObject.getMessageBody() == null ||
        xmlMessageObject.getCreationTime() == null ||
        xmlMessageObject.getLastModified() == null ||
        xmlMessageObject.getLastAgent() == null ||
        xmlMessageObject.getStatus() == null;
  }
}