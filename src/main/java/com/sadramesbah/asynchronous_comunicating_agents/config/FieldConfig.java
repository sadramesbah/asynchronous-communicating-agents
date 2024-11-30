package com.sadramesbah.asynchronous_comunicating_agents.config;

public class FieldConfig {

  private String name;
  private String type;
  private int length;
  private boolean required;

  // getters and setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  @Override
  public String toString() {
    return "FieldConfig{" +
        "name='" + name + '\'' +
        ", type='" + type + '\'' +
        ", length=" + length +
        ", required=" + required +
        '}';
  }
}
