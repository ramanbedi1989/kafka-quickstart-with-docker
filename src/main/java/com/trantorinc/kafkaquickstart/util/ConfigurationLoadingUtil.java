package com.trantorinc.kafkaquickstart.util;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Log4j2
public class ConfigurationLoadingUtil {
    public static Properties fetchKafkaProducerConfigs() throws IOException {
        return fetchConfigs("producer.properties");
    }

    public static Properties fetchKafkaConsumerConfigs() throws IOException {
        return fetchConfigs("consumer.properties");
    }

    public static Properties fetchConfigs(String configFileName) throws IOException {
        Properties producerConfig = new Properties();
        try (InputStream inputStream = ConfigurationLoadingUtil.class.getClassLoader()
                .getResourceAsStream(configFileName)) {
            producerConfig.load(inputStream);
        }
        return producerConfig;
    }
}
