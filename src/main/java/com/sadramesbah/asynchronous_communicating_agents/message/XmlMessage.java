package com.sadramesbah.asynchronous_communicating_agents.message;

import com.sadramesbah.asynchronous_communicating_agents.adapter.TimestampAdapter;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Timestamp;

@XmlRootElement(name = "Message")
public class XmlMessage implements Message {

  private int messageId;
  private String messageTitle;
  private String messageBody;
  private Timestamp creationTime;
  private Timestamp lastModified;
  private String lastAgent;
  private String status;

  @XmlElement(name = "MessageID")
  public int getMessageId() {
    return messageId;
  }

  public void setMessageId(int messageId) {
    this.messageId = messageId;
  }

  @XmlElement(name = "MessageTitle")
  public String getMessageTitle() {
    return messageTitle;
  }

  public void setMessageTitle(String messageTitle) {
    this.messageTitle = messageTitle;
  }

  @XmlElement(name = "MessageBody")
  public String getMessageBody() {
    return messageBody;
  }

  public void setMessageBody(String messageBody) {
    this.messageBody = messageBody;
  }

  @XmlElement(name = "CreationTime")
  @XmlJavaTypeAdapter(TimestampAdapter.class)
  public Timestamp getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(Timestamp creationTime) {
    this.creationTime = creationTime;
  }

  @XmlElement(name = "LastModified")
  @XmlJavaTypeAdapter(TimestampAdapter.class)
  public Timestamp getLastModified() {
    return lastModified;
  }

  public void setLastModified(Timestamp lastModified) {
    this.lastModified = lastModified;
  }

  @XmlElement(name = "LastAgent")
  public String getLastAgent() {
    return lastAgent;
  }

  public void setLastAgent(String lastAgent) {
    this.lastAgent = lastAgent;
  }

  @XmlElement(name = "Status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
