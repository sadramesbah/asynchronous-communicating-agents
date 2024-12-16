package com.sadramesbah.asynchronous_communicating_agents.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.sql.Timestamp;
import java.time.Instant;

public class TimestampAdapter extends XmlAdapter<String, Timestamp> {

  @Override
  public Timestamp unmarshal(String xmlMessageInString) {
    return xmlMessageInString != null ? Timestamp.from(Instant.parse(xmlMessageInString)) : null;
  }

  @Override
  public String marshal(Timestamp timestamp) {
    return timestamp != null ? timestamp.toInstant().toString() : null;
  }
}
