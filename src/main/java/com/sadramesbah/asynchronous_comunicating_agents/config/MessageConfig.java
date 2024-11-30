package com.sadramesbah.asynchronous_comunicating_agents.config;

import java.util.List;
import java.util.Optional;

public class MessageConfig {

  private List<FieldConfig> fields;

  public MessageConfig() {
  }

  public MessageConfig(List<FieldConfig> fields) {
    this.fields = fields;
  }

  public List<FieldConfig> getFields() {
    return fields;
  }

  public void setFields(List<FieldConfig> fields) {
    this.fields = fields;
  }

  // returns the field with the given name
  public Optional<FieldConfig> getFieldByName(String fieldName) {
    return fields.stream()
        .filter(field -> field.getName().equals(fieldName))
        .findFirst();
  }

  // returns true if the field with the given name is present
  public boolean isFieldPresent(String fieldName) {
    return getFieldByName(fieldName).isPresent();
  }
}
