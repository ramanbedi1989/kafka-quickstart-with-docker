package com.trantorinc.kafkaquickstart.util;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;

import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class AdminClientUtil {
    public static void createTopic(final String topic, int numberOfPartitions, final Properties kafkaProperties) {
        final NewTopic newTopic = new NewTopic(topic, Optional.of(numberOfPartitions), Optional.empty());
        try (final AdminClient adminClient = AdminClient.create(kafkaProperties)) {
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        } catch (final InterruptedException | ExecutionException e) {
            // We can ignore the exception if the topic already exists
            if (!(e.getCause() instanceof TopicExistsException)) {
                throw new RuntimeException(e);
            }
        }
    }
}
