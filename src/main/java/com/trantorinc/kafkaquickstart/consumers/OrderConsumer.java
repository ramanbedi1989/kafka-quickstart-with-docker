package com.trantorinc.kafkaquickstart.consumers;

import com.trantorinc.kafkaquickstart.models.Order;
import com.trantorinc.kafkaquickstart.util.ConfigurationLoadingUtil;
import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Log4j2
public class OrderConsumer {
    public static void main(String[] args) {
        if (args.length != 1) {
            log.error("Only one argument with topic name is expected: OrderConsumer <topic>");
            System.exit(1);
        }

        String topicName = args[0];
        Properties consumerProperties = null;
        try {
            consumerProperties = ConfigurationLoadingUtil.fetchKafkaConsumerConfigs();
        } catch (IOException ioException) {
            log.error("Error occurred while fetching Kafka Consumer configurations", ioException);
            System.exit(2);
        }

        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonDeserializer");
        consumerProperties.put(KafkaJsonDeserializerConfig.JSON_VALUE_TYPE, Order.class);

        final Consumer<String, Order> consumer = new KafkaConsumer<String, Order>(consumerProperties);
        consumer.subscribe(Collections.singletonList(topicName));

        try {
            // Indefinitely poll for messages on the topic
            while (true) {
                ConsumerRecords<String, Order> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, Order> record : records) {
                    String orderType = record.key();
                    Order currentOrder = record.value();
                    log.info("Received order of type {}, with details {}, partition {}, offset {}", orderType,
                            currentOrder, record.partition(), record.offset());
                }
            }
        } finally {
            consumer.close();
        }
    }
}
