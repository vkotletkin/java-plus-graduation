package ru.practicum.stats.collector.service.impl.kafka;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.collector.config.KafkaSettingsConfig;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class UserActionProducer {

    private final KafkaSettingsConfig kafkaSettingsConfig;
    private final Producer<String, SpecificRecordBase> producer;

    public void send(UserActionAvro action) {
        producer.send(new ProducerRecord<>(kafkaSettingsConfig.getTopic(), action));
    }
}
