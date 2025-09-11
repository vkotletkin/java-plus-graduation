package ru.practicum.stats.analyzer.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.analyzer.starter.UserActionStarter;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaConfig {

    KafkaSettingsConfig kafkaSettingsConfig;

    @Bean
    public KafkaConsumer<String, EventSimilarityAvro> eventSimilarityAvroKafkaConsumer() {

        Properties config = new Properties();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaSettingsConfig.getUrl());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "ru.practicum.stats.analyzer.serialization.EventSimilarityAvroDeserializer");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "similarity-cg");

        return new KafkaConsumer<>(config);
    }

    @Bean
    public KafkaConsumer<String, UserActionAvro> userActionAvroKafkaConsumer() {

        Properties config = new Properties();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaSettingsConfig.getUrl());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "ru.practicum.stats.analyzer.serialization.UserActionAvroDeserializer");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "similarity-cg");

        return new KafkaConsumer<>(config);
    }
}