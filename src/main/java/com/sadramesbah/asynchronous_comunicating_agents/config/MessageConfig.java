package com.sadramesbah.asynchronous_comunicating_agents.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "messages")
public class MessageConfig {

  private Map<String, List<FieldConfig>> xml;
  private Map<String, List<FieldConfig>> soap;
  private Map<String, List<FieldConfig>> json;

  // getters and setters
  public Map<String, List<FieldConfig>> getXml() {
    return xml;
  }

  public void setXml(Map<String, List<FieldConfig>> xml) {
    this.xml = xml;
  }

  public Map<String, List<FieldConfig>> getSoap() {
    return soap;
  }

  public void setSoap(Map<String, List<FieldConfig>> soap) {
    this.soap = soap;
  }

  public Map<String, List<FieldConfig>> getJson() {
    return json;
  }

  public void setJson(Map<String, List<FieldConfig>> json) {
    this.json = json;
  }

  // returns the field configurations for the given message type
  public List<FieldConfig> getFieldConfigs(String type) {
    return switch (type.toLowerCase()) {
      case "xml" -> xml.get("fields");
      case "soap" -> soap.get("fields");
      case "json" -> json.get("fields");
      default -> throw new IllegalArgumentException("Unsupported message type: " + type);
    };
  }
}
