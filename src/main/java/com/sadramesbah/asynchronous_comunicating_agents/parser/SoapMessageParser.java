package com.sadramesbah.asynchronous_comunicating_agents.parser;

import com.sadramesbah.asynchronous_comunicating_agents.config.SoapMessageConfig;
import org.springframework.stereotype.Component;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.io.IOException;


@Component
public class SoapMessageParser {

  private final SoapMessageConfig soapConfig;
  private final MessageFactory messageFactory;

  public SoapMessageParser(SoapMessageConfig soapConfig) throws SOAPException {
    this.soapConfig = soapConfig;
    this.messageFactory = MessageFactory.newInstance();
  }

  // parses the SOAP message in string and returns the SOAPMessage object
  public SOAPMessage parse(String soapMessage) throws IOException, SOAPException {
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(
        soapMessage.getBytes(StandardCharsets.UTF_8))) {
      SOAPMessage message = messageFactory.createMessage(null, inputStream);
      if (!isValid(message)) {
        throw new IOException("Invalid SOAP message");
      }
      return message;
    }
  }

  // checks if SOAP message contains all required fields
  private boolean isValid(SOAPMessage soapMessage) throws SOAPException {
    SOAPBody body = soapMessage.getSOAPBody();
    return soapConfig.getFields().stream()
        .allMatch(field -> body.getElementsByTagName(field.getName()).getLength() > 0);
  }
}
