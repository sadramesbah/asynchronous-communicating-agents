package com.sadramesbah.asynchronous_comunicating_agents.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "messages.soap")
public class SoapMessageConfig extends MessageConfig {

  public SoapMessageConfig() {
  }

  public SoapMessageConfig(List<FieldConfig> fields) {
    super(fields);
  }
}
