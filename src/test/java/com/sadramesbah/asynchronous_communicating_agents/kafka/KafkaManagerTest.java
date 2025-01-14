package com.sadramesbah.asynchronous_communicating_agents.kafka;

import java.util.stream.Collectors;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.util.concurrent.ExecutionException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig
@EmbeddedKafka(partitions = 1, topics = {"test-topic"})
class KafkaManagerTest {

  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;

  private AdminClient adminClient;
  private KafkaManager kafkaManager;

  @BeforeEach
  void setUp() {
    Map<String, Object> configs = KafkaTestUtils.producerProps(embeddedKafkaBroker);
    configs.put("bootstrap.servers", embeddedKafkaBroker.getBrokersAsString());
    adminClient = AdminClient.create(configs);
    kafkaManager = new KafkaManager(embeddedKafkaBroker.getBrokersAsString());
  }

  @AfterEach
  void shutdown() {
    adminClient.close();
  }

  @Test
  void testCreateTopic() throws ExecutionException, InterruptedException {
    String topicName = "new-test-topic1";
    boolean created = kafkaManager.createTopic(topicName, 1, (short) 1);
    assertTrue(created, "The topic should be created successfully.");
    Set<String> names = adminClient.listTopics().names().get();
    assertTrue(names.contains(topicName), "The topic should exist in the list of topics.");
  }

  @Test
  void testDeleteTopic() throws ExecutionException, InterruptedException {
    String topicName = "test-topic2";
    kafkaManager.createTopic(topicName, 1, (short) 1); // Ensure the topic is created
    boolean deleted = kafkaManager.deleteTopic(topicName);
    assertTrue(deleted, "The topic should be deleted successfully.");
    Set<String> names = adminClient.listTopics().names().get();
    assertFalse(names.contains(topicName), "The topic should not exist in the list of topics.");
  }

  @Test
  void testDeleteAllTopics() throws ExecutionException, InterruptedException {
    kafkaManager.deleteAllTopics();
    KafkaFuture<Set<String>> namesFuture = adminClient.listTopics().names();
    Set<String> names = namesFuture.get();
    assertTrue(names.isEmpty());
  }

  @Test
  void testTopicExists() {
    String topicName = "test-topic3";
    kafkaManager.createTopic(topicName, 1, (short) 1);
    boolean exists = kafkaManager.topicExists(topicName);
    assertTrue(exists);
  }

  @Test
  void testListTopics() throws ExecutionException, InterruptedException {
    Set<String> topics = kafkaManager.listTopics();
    KafkaFuture<Set<String>> namesFuture = adminClient.listTopics().names();
    Set<String> names = namesFuture.get();
    assertEquals(names, topics);
  }

  @Test
  void testDescribeTopic() throws ExecutionException, InterruptedException {
    String topicName = "test-topic4";
    kafkaManager.createTopic(topicName, 1, (short) 1); // Ensure the topic is created
    TopicDescription description = kafkaManager.describeTopic(topicName);
    KafkaFuture<Map<String, TopicDescription>> future = adminClient.describeTopics(
        Collections.singleton(topicName)).allTopicNames();
    Map<String, TopicDescription> descriptions = future.get();
    assertEquals(descriptions.get(topicName), description);
  }

  @Test
  void testDescribeCluster() throws ExecutionException, InterruptedException {
    DescribeClusterResult clusterDescription = kafkaManager.describeCluster();
    DescribeClusterResult expectedDescription = adminClient.describeCluster();
    assertEquals(expectedDescription.nodes().get().size(), clusterDescription.nodes().get().size());
    assertTrue(expectedDescription.nodes().get().containsAll(clusterDescription.nodes().get()));
  }

  @Test
  void testListConsumerGroups() throws ExecutionException, InterruptedException {
    Set<String> consumerGroups = kafkaManager.listConsumerGroups();
    KafkaFuture<Set<String>> future = adminClient.listConsumerGroups().all().thenApply(
        groups -> groups.stream().map(ConsumerGroupListing::groupId).collect(Collectors.toSet()));
    Set<String> expectedGroups = future.get();
    assertEquals(expectedGroups, consumerGroups);
  }

  @Test
  void testCreateExistingTopic() {
    String topicName = "test-topic6";
    boolean created = kafkaManager.createTopic(topicName, 1, (short) 1);
    assertTrue(created, "The topic should be created successfully.");
    boolean exists = kafkaManager.createTopic(topicName, 1, (short) 1);
    assertFalse(exists, "The topic should already exist and not be created again.");
  }

  @Test
  void testDeleteNonExistingTopic() {
    String topicName = "non-existing-topic1";
    boolean deleted = kafkaManager.deleteTopic(topicName);
    assertFalse(deleted, "The topic should not be deleted as it does not exist.");
    assertFalse(kafkaManager.topicExists(topicName), "The topic should not exist.");
  }

  @Test
  void testTopicDoesNotExist() {
    String topicName = "non-existing-topic2";
    boolean exists = kafkaManager.topicExists(topicName);
    assertFalse(exists);
  }

  @Test
  void testDescribeNonExistingTopic() {
    String topicName = "non-existing-topic3";
    TopicDescription description = kafkaManager.describeTopic(topicName);
    assertNull(description);
  }

  @Test
  void testAddPartitionsToNonExistingTopic() {
    String topicName = "non-existing-topic4";
    assertThrows(ExecutionException.class, () -> {
      if (!kafkaManager.topicExists(topicName)) {
        throw new ExecutionException(
            new org.apache.kafka.common.errors.UnknownTopicOrPartitionException(
                "The topic 'non-existing-topic4' does not exist."));
      }
      kafkaManager.addPartitionsToTopic(topicName, 3);
    });
  }
}