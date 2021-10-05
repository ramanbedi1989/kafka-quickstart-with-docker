package com.trantorinc.kafkaquickstart.producers;

import com.trantorinc.kafkaquickstart.constant.ApplicationConstant;
import com.trantorinc.kafkaquickstart.models.Order;
import com.trantorinc.kafkaquickstart.util.AdminClientUtil;
import com.trantorinc.kafkaquickstart.util.ConfigurationLoadingUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

@Log4j2
public class OrderProducer {

    public static void main(String[] args) {
        if (args.length != 1) {
            log.error("Only one argument with topic name is expected: OrderProducer <topic>");
            System.exit(1);
        }

        String topicName = args[0];

        Properties kafkaProducerProperties = null;
        try {
            kafkaProducerProperties = ConfigurationLoadingUtil.fetchKafkaProducerConfigs();
        } catch (IOException ioException) {
            log.error("Error occurred while fetching Kafka Producer configurations", ioException);
            System.exit(2);
        }
        kafkaProducerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonSerializer");

        // Create 2 partitions
        AdminClientUtil.createTopic(topicName, 2, kafkaProducerProperties);

        Producer<String, Order> producer = new KafkaProducer<>(kafkaProducerProperties);

        int orderBaseId = 1;
        // Keep on producing messages on regular interval
        while (true) {
            String orderType = ApplicationConstant.ORDER_TYPES[new Random().nextInt(2)];
            Order currentOrder = new Order();
            currentOrder.setOrderNumber("ORD-" + orderBaseId);
            currentOrder.setTotalPrice(new Random().nextInt(1000) + 2000);
            currentOrder.setOrderType(orderType);
            log.info("Producing record: {} {}", orderType, currentOrder);
            producer.send(new ProducerRecord<String, Order>(topicName, orderType, currentOrder), (message, exception) -> {
                if (exception != null) {
                    log.error("Exception occurred while producing messages", exception);
                } else {
                    log.info("Produced record to topic {}, partition {}, offset {}", message.topic(),
                            message.partition(), message.offset());
                }
            });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException interruptedException) {
                log.error("Exception occurred while waiting to produce messages", interruptedException);
                System.exit(3);
            }
            orderBaseId += 1;
        }
    }
}
