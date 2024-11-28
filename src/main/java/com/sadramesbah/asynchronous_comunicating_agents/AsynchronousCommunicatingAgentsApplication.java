package com.sadramesbah.asynchronous_comunicating_agents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class AsynchronousCommunicatingAgentsApplication {

  public static void main(String[] args) {
    SpringApplication.run(AsynchronousCommunicatingAgentsApplication.class, args);
  }

}