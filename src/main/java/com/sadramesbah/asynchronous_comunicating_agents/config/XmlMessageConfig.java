package com.sadramesbah.asynchronous_comunicating_agents.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "messages.xml")
public class XmlMessageConfig extends MessageConfig {

  public XmlMessageConfig() {
  }

  public XmlMessageConfig(List<FieldConfig> fields) {
    super(fields);
  }
}
