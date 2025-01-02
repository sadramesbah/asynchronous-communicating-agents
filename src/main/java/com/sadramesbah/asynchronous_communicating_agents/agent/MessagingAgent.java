package com.sadramesbah.asynchronous_communicating_agents.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sadramesbah.asynchronous_communicating_agents.handler.JsonMessageHandler;
import com.sadramesbah.asynchronous_communicating_agents.handler.SoapMessageHandler;
import com.sadramesbah.asynchronous_communicating_agents.handler.XmlMessageHandler;
import com.sadramesbah.asynchronous_communicating_agents.message.JsonMessage;
import com.sadramesbah.asynchronous_communicating_agents.message.Message;
import com.sadramesbah.asynchronous_communicating_agents.message.MessageType;
import com.sadramesbah.asynchronous_communicating_agents.message.XmlMessage;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;

public class MessagingAgent {

  private final String id;
  private final JsonMessageHandler jsonHandler;
  private final XmlMessageHandler xmlHandler;
  private final SoapMessageHandler soapHandler;

  public MessagingAgent(String agentId) throws JAXBException, SOAPException {
    this.id = agentId;
    this.jsonHandler = new JsonMessageHandler();
    this.xmlHandler = new XmlMessageHandler();
    this.soapHandler = new SoapMessageHandler();
  }

  // handles the input message based on message type
  public String handleInputMessage(String inputMessage, MessageType inputMessageType)
      throws JAXBException, SOAPException, IOException {
    return switch (inputMessageType) {
      case JSON -> updateJsonMessage(jsonHandler.parse(inputMessage));
      case XML -> updateXmlMessage(xmlHandler.parse(inputMessage));
      case SOAP -> updateSoapMessage(soapHandler.parse(inputMessage));
    };
  }

  // updates the Json message attributes and returns the updated Json message in String format
  private String updateJsonMessage(JsonMessage jsonMessageObject) throws JsonProcessingException {
    return jsonHandler.toJsonString((JsonMessage) updateMessageAttributes(jsonMessageObject));
  }

  // updates the Xml message attributes and returns the updated Xml message in String format
  private String updateXmlMessage(XmlMessage xmlMessageObject) throws JAXBException {
    return xmlHandler.toXmlString((XmlMessage) updateMessageAttributes(xmlMessageObject));
  }

  // updates the Soap message attributes and returns the updated Soap message in String format
  private String updateSoapMessage(SOAPMessage soapMessageObject)
      throws SOAPException, IOException {
    SOAPBody body = soapMessageObject.getSOAPPart().getEnvelope().getBody();

    Optional.ofNullable((SOAPElement) body.getElementsByTagName("Message").item(0))
        .ifPresent(messageElement -> {

          Optional.ofNullable(
                  (SOAPElement) messageElement.getElementsByTagName("MessageBody").item(0))
              .ifPresent(messageBodyElement -> messageBodyElement.setTextContent(
                  messageBodyElement.getTextContent() + " Processed by Agent: " + id + " at " +
                      java.time.LocalDateTime.now().format(
                          java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss."))));

          Optional.ofNullable(
                  (SOAPElement) messageElement.getElementsByTagName("LastAgent").item(0))
              .ifPresent(lastAgentElement -> lastAgentElement.setTextContent(id));

          Optional.ofNullable(
                  (SOAPElement) messageElement.getElementsByTagName("LastModified").item(0))
              .ifPresent(lastModifiedElement -> lastModifiedElement.setTextContent(
                  java.time.Instant.now().toString()));
        });

    soapMessageObject.saveChanges();
    return soapHandler.toSoapString(soapMessageObject);
  }

  // updates the message attributes and returns the updated message object
  private Message updateMessageAttributes(Message message) {
    message.setMessageBody(
        message.getMessageBody() + " Processed by Agent: " + id + " at "
            + java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.")));
    message.setLastModified(new Timestamp(System.currentTimeMillis()));
    message.setLastAgent(id);
    return message;
  }

  public String getId() {
    return id;
  }
}