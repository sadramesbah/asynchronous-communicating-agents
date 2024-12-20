package com.sadramesbah.asynchronous_communicating_agents.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Timestamp;

public class JsonMessage implements Message {

  @JsonProperty("MessageID")
  private int messageId;

  @JsonProperty("MessageTitle")
  private String messageTitle;

  @JsonProperty("MessageBody")
  private String messageBody;

  @JsonProperty("CreationTime")
  private Timestamp creationTime;

  @JsonProperty("LastModified")
  private Timestamp lastModified;

  @JsonProperty("LastAgent")
  private String lastAgent;

  @JsonProperty("Status")
  private String status;

  // getters and setters
  public int getMessageId() {
    return messageId;
  }

  public void setMessageId(int messageID) {
    this.messageId = messageID;
  }

  public String getMessageTitle() {
    return messageTitle;
  }

  public void setMessageTitle(String messageTitle) {
    this.messageTitle = messageTitle;
  }

  public String getMessageBody() {
    return messageBody;
  }

  public void setMessageBody(String messageBody) {
    this.messageBody = messageBody;
  }

  public Timestamp getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(Timestamp creationTime) {
    this.creationTime = creationTime;
  }

  public Timestamp getLastModified() {
    return lastModified;
  }

  public void setLastModified(Timestamp lastModified) {
    this.lastModified = lastModified;
  }

  public String getLastAgent() {
    return lastAgent;
  }

  public void setLastAgent(String lastAgent) {
    this.lastAgent = lastAgent;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
