package com.sadramesbah.asynchronous_comunicating_agents.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "messages.json")
public class JsonMessageConfig extends MessageConfig {

  public JsonMessageConfig() {
  }

  public JsonMessageConfig(List<FieldConfig> fields) {
    super(fields);
  }
}
