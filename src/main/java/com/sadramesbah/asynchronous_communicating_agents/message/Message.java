package com.sadramesbah.asynchronous_communicating_agents.message;

import java.sql.Timestamp;

public interface Message {

  int getMessageId();

  void setMessageId(int messageID);

  void setMessageTitle(String messageTitle);

  void setMessageBody(String messageBody);

  void setCreationTime(Timestamp creationTime);

  void setLastModified(Timestamp lastModified);

  void setLastAgent(String lastAgent);

  void setStatus(String status);

  String getMessageTitle();

  String getMessageBody();

  Timestamp getCreationTime();

  Timestamp getLastModified();

  String getLastAgent();

  String getStatus();
}
