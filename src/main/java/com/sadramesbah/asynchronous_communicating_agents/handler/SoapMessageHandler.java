package com.sadramesbah.asynchronous_communicating_agents.handler;

import com.sadramesbah.asynchronous_communicating_agents.message.XmlMessage;
import org.springframework.stereotype.Component;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class SoapMessageHandler {

  private final Marshaller marshaller;
  private final Unmarshaller unmarshaller;
  private final MessageFactory messageFactory;

  public SoapMessageHandler() throws JAXBException, SOAPException {
    JAXBContext jaxbContext = JAXBContext.newInstance(XmlMessage.class);
    this.marshaller = jaxbContext.createMarshaller();
    this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    this.unmarshaller = jaxbContext.createUnmarshaller();
    this.messageFactory = MessageFactory.newInstance();
  }

  // parses SOAP message in string format and converts it to SOAPMessage object
  public SOAPMessage parse(String soapMessageInString)
      throws IOException, SOAPException, JAXBException {
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(
        soapMessageInString.getBytes(StandardCharsets.UTF_8))) {
      SOAPMessage soapMessage = messageFactory.createMessage(null, inputStream);
      if (isInvalid(soapMessage)) {
        throw new SOAPException("Invalid SOAP message");
      }
      // ensures SOAP header is created
      SOAPHeader header = Optional.ofNullable(soapMessage.getSOAPHeader())
          .orElseGet(() -> {
            try {
              return soapMessage.getSOAPPart().getEnvelope().addHeader();
            } catch (SOAPException e) {
              throw new RuntimeException("Failed to add SOAP header", e);
            }
          });
      // adds default header details
      header.addHeaderElement(
          soapMessage.getSOAPPart().getEnvelope()
              .createName("AgentID", "ns", "http://sadramesbah.com/agent")
      ).addTextNode("Agent1");
      return soapMessage;
    }
  }

  // converts SOAPMessage object to SOAP message in string format
  public String toSoap(SOAPMessage soapMessageObject)
      throws JAXBException, SOAPException, IOException {
    if (isInvalid(soapMessageObject)) {
      throw new JAXBException("Invalid XML message");
    }
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    soapMessageObject.writeTo(outputStream);
    return outputStream.toString(StandardCharsets.UTF_8);
  }

  // checks if SOAPMessage object has the expected structure
  private boolean isInvalid(SOAPMessage soapMessageObject) throws SOAPException, JAXBException {
    SOAPBody body = soapMessageObject.getSOAPBody();
    if (body == null || body.getFault() != null) {
      return true;
    }
    XmlMessage xmlMessageObject = (XmlMessage) unmarshaller.unmarshal(
        body.extractContentAsDocument());
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
