package com.sadramesbah.asynchronous_communicating_agents.kafka;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collection;

public class KafkaManager {

  private static final Logger logger = LoggerFactory.getLogger(KafkaManager.class);
  private final AdminClient adminClient;

  @Value("${default.kafka.port}")
  private int defaultKafkaPort;

  @Value("${default.kafka.zookeeper.port}")
  private int defaultZookeeperPort;

  // constructor to create an AdminClient instance
  public KafkaManager(@Value("${kafka.bootstrap.server}") String bootstrapServer) {
    Properties config = new Properties();
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    this.adminClient = AdminClient.create(config);
  }

  // checks if Zookeeper and Kafka servers are active
  @PostConstruct
  public void checkServersStatus() {
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

  // checks if a server is running on the provided port
  private boolean isServerRunning(int port) {
    try (Socket socket = new Socket("localhost", port)) {
      return true;
    } catch (IOException ioException) {
      return false;
    }
  }

  // creates a new topic
  public void createTopic(String topicName, int numPartitions, short replicationFactor) {
    if (!topicExists(topicName)) {
      NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
      try {
        adminClient.createTopics(Collections.singleton(newTopic)).all().get();
        logger.info("Topic created: {}", topicName);
      } catch (InterruptedException intException) {
        Thread.currentThread().interrupt();
        logger.error("Failed to create topic {} with InterruptedException: ", topicName,
            intException);
      } catch (ExecutionException exeException) {
        logger.error("Failed to create topic {} with ExecutionException: ", topicName,
            exeException);
      }
    } else {
      logger.info("Topic {} already exists.", topicName);
    }
  }

  // deletes a topic
  public void deleteTopic(String topicName) {
    try {
      adminClient.deleteTopics(Collections.singleton(topicName)).all().get();
      logger.info("Topic deleted: {}", topicName);
    } catch (InterruptedException intException) {
      Thread.currentThread().interrupt();
      logger.error("Failed to delete topic {} with InterruptedException: ", topicName,
          intException);
    } catch (ExecutionException exeException) {
      logger.error("Failed to delete topic {} with ExecutionException: ", topicName, exeException);
    }
  }

  // deletes all existing topics
  public void deleteAllTopics() {
    try {
      Set<String> topics = adminClient.listTopics(new ListTopicsOptions().listInternal(false))
          .names().get();
      adminClient.deleteTopics(topics).all().get();
      logger.info("All topics deleted: {}", topics);
    } catch (InterruptedException intException) {
      Thread.currentThread().interrupt();
      logger.error("Failed to delete all topics with InterruptedException: ", intException);
    } catch (ExecutionException exeException) {
      logger.error("Failed to delete all topics with ExecutionException: ", exeException);
    }
  }

  // checks if a topic exists
  public boolean topicExists(String topicName) {
    try {
      Set<String> topics = adminClient.listTopics(new ListTopicsOptions().listInternal(false))
          .names().get();
      boolean exists = topics.contains(topicName);
      logger.info("Topic {} exists: {}", topicName, exists);
      return exists;
    } catch (InterruptedException intException) {
      Thread.currentThread().interrupt();
      logger.error("Failed to check if topic {} exists with InterruptedException: ", topicName,
          intException);
    } catch (ExecutionException exeException) {
      logger.error("Failed to check if topic {} exists with ExecutionException: ", topicName,
          exeException);
    }
    return false;
  }

  // lists all existing topics
  public Set<String> listTopics() {
    try {
      Set<String> topics = adminClient.listTopics(new ListTopicsOptions().listInternal(false))
          .names().get();
      logger.info("List of topics: {}", topics);
      return topics;
    } catch (InterruptedException intException) {
      Thread.currentThread().interrupt();
      logger.error("Failed to list all topics with InterruptedException: ", intException);
    } catch (ExecutionException exeException) {
      logger.error("Failed to list all topics with ExecutionException: ", exeException);
    }
    return Collections.emptySet();
  }

  // updates the configuration of an existing topic
  public void updateTopicConfig(String topicName, Map<String, String> configs) {
    try {
      ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, topicName);
      Collection<AlterConfigOp> configOps = configs.entrySet().stream()
          .map(entry -> new AlterConfigOp(new ConfigEntry(entry.getKey(), entry.getValue()),
              AlterConfigOp.OpType.SET))
          .toList();
      adminClient.incrementalAlterConfigs(Collections.singletonMap(configResource, configOps)).all()
          .get();
      logger.info("Updated configuration for topic: {}", topicName);
    } catch (InterruptedException intException) {
      Thread.currentThread().interrupt();
      logger.error("Failed to update configuration for topic {} with InterruptedException",
          topicName,
          intException);
    } catch (ExecutionException exeException) {
      logger.error("Failed to update configuration for topic {} with ExecutionException", topicName,
          exeException);
    }
  }

  // gets the details of a specific topic
  public TopicDescription describeTopic(String topicName) {
    try {
      TopicDescription description = adminClient.describeTopics(Collections.singleton(topicName))
          .allTopicNames().get().get(topicName);
      logger.info("Description for topic {}: {}", topicName, description);
      return description;
    } catch (InterruptedException intException) {
      Thread.currentThread().interrupt();
      logger.error("Failed to describe topic {} with InterruptedException", topicName,
          intException);
    } catch (ExecutionException exeException) {
      logger.error("Failed to describe topic {} with ExecutionException", topicName, exeException);
    }
    return null;
  }

  // gets the details of the Kafka cluster
  public DescribeClusterResult describeCluster() {
    DescribeClusterResult clusterDescription = adminClient.describeCluster();
    logger.info("Cluster description: {}", clusterDescription);
    return clusterDescription;
  }

  // lists all existing consumer groups
  public Set<String> listConsumerGroups() {
    try {
      Set<String> consumerGroups = adminClient.listConsumerGroups().all().get().stream()
          .map(ConsumerGroupListing::groupId)
          .collect(Collectors.toSet());
      logger.info("List of consumer groups: {}", consumerGroups);
      return consumerGroups;
    } catch (InterruptedException intException) {
      Thread.currentThread().interrupt();
      logger.error("Failed to list consumer groups with InterruptedException", intException);
    } catch (ExecutionException exeException) {
      logger.error("Failed to list consumer groups with ExecutionException", exeException);
    }
    return Collections.emptySet();
  }

  // gets the details of a specific consumer group
  public ConsumerGroupDescription describeConsumerGroup(String groupId) {
    try {
      ConsumerGroupDescription description = adminClient.describeConsumerGroups(
          Collections.singleton(groupId)).all().get().get(groupId);
      logger.info("Description for consumer group {}: {}", groupId, description);
      return description;
    } catch (InterruptedException intException) {
      Thread.currentThread().interrupt();
      logger.error("Failed to describe consumer group {} with InterruptedException", groupId,
          intException);
    } catch (ExecutionException exeException) {
      logger.error("Failed to describe consumer group {} with ExecutionException", groupId,
          exeException);
    }
    return null;
  }

  // adds partitions to an existing topic
  public void addPartitionsToTopic(String topicName, int numPartitions) {
    try {
      adminClient.createPartitions(
          Collections.singletonMap(topicName, NewPartitions.increaseTo(numPartitions))).all().get();
      logger.info("Added partitions to topic: {}", topicName);
    } catch (InterruptedException intException) {
      Thread.currentThread().interrupt();
      logger.error("Failed to add partitions to topic {} with InterruptedException", topicName,
          intException);
    } catch (ExecutionException exeException) {
      logger.error("Failed to add partitions to topic {} with ExecutionException", topicName,
          exeException);
    }
  }

  // closes the AdminClient instance
  public void closeAdminClient() {
    adminClient.close();
    logger.info("AdminClient closed");
  }
}