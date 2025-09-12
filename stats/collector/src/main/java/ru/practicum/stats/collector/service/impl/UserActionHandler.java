package ru.practicum.stats.collector.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.practicum.stats.collector.config.KafkaSettingsConfig;
import ru.practicum.stats.collector.mapper.UserActionMapper;
import ru.practicum.stats.collector.service.CollectorHandler;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class UserActionHandler implements CollectorHandler<UserActionProto> {

    private final KafkaSettingsConfig kafkaSettingsConfig;
    private final Producer<String, SpecificRecordBase> producer;

    public void handle(UserActionProto proto) {
        producer.send(new ProducerRecord<>(kafkaSettingsConfig.getTopic(), UserActionMapper.mapToAvro(proto)));
    }
}
