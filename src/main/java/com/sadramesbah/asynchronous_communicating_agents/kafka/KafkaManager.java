package com.sadramesbah.asynchronous_communicating_agents.kafka;

import org.apache.kafka.clients.admin.*;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

public class KafkaManager {

  private static final Logger logger = LoggerFactory.getLogger(KafkaManager.class);
  private final AdminClient adminClient;

  @Value("${default.kafka.port}")
  private int defaultKafkaPort;

  @Value("${default.kafka.zookeeper.port}")
  private int defaultZookeeperPort;

  public KafkaManager(@Value("${kafka.bootstrap.server}") String bootstrapServer) {
    Properties config = new Properties();
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    this.adminClient = AdminClient.create(config);
  }

  @PostConstruct
  public void checkServers() {
    if (isServerRunning(defaultZookeeperPort)) {
      logger.info("Zookeeper is running.");
    } else {
      logger.error("Zookeeper is not running on port {}", defaultZookeeperPort);
      throw new IllegalStateException("Zookeeper is not running");
    }
    if (isServerRunning(defaultKafkaPort)) {
      logger.info("Kafka is running.");
    } else {
      logger.error("Kafka is not running on port {}", defaultKafkaPort);
      throw new IllegalStateException("Kafka is not running");
    }
  }

  private boolean isServerRunning(int port) {
    try (Socket socket = new Socket("localhost", port)) {
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  public void createTopic(String topicName, int numPartitions, short replicationFactor) {
    if (!topicExists(topicName)) {
      NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
      try {
        adminClient.createTopics(Collections.singleton(newTopic)).all().get();
        logger.info("Topic created: {}", topicName);
      } catch (InterruptedException exception) {
        Thread.currentThread().interrupt();
        logger.error("Failed to create topic {} with InterruptedException: ", topicName, exception);
      } catch (ExecutionException exception) {
        logger.error("Failed to create topic {} with ExecutionException: ", topicName, exception);
      }
    } else {
      logger.info("Topic {} already exists.", topicName);
    }
  }

  public void deleteTopic(String topicName) {
    try {
      adminClient.deleteTopics(Collections.singleton(topicName)).all().get();
      logger.info("Topic deleted: {}", topicName);
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      logger.error("Failed to delete topic {} with InterruptedException: ", topicName, exception);
    } catch (ExecutionException exception) {
      logger.error("Failed to delete topic {} with ExecutionException: ", topicName, exception);
    }
  }

  public void deleteAllTopics() {
    try {
      Set<String> topics = adminClient.listTopics(new ListTopicsOptions().listInternal(false))
          .names().get();
      adminClient.deleteTopics(topics).all().get();
      logger.info("All topics deleted: {}", topics);
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      logger.error("Failed to delete all topics with InterruptedException: ", exception);
    } catch (ExecutionException exception) {
      logger.error("Failed to delete all topics with ExecutionException: ", exception);
    }
  }

  public boolean topicExists(String topicName) {
    try {
      Set<String> topics = adminClient.listTopics(new ListTopicsOptions().listInternal(false))
          .names().get();
      boolean exists = topics.contains(topicName);
      logger.info("Topic {} exists: {}", topicName, exists);
      return exists;
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      logger.error("Failed to check if topic {} exists with InterruptedException: ", topicName,
          exception);
    } catch (ExecutionException exception) {
      logger.error("Failed to check if topic {} exists with ExecutionException: ", topicName,
          exception);
    }
    return false;
  }

  public Set<String> listTopics() {
    try {
      Set<String> topics = adminClient.listTopics(new ListTopicsOptions().listInternal(false))
          .names().get();
      logger.info("List of topics: {}", topics);
      return topics;
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      logger.error("Failed to list all topics with InterruptedException: ", exception);
    } catch (ExecutionException exception) {
      logger.error("Failed to list all topics with ExecutionException: ", exception);
    }
    return Collections.emptySet();
  }

  public void closeAdminClient() {
    adminClient.close();
    logger.info("AdminClient closed");
  }
}