package ru.practicum.stats.aggregator.starter;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.aggregator.config.SettingsConfig;
import ru.practicum.stats.aggregator.service.UserActionHandler;

import java.time.Duration;
import java.util.List;


@RequiredArgsConstructor
@Component
@Slf4j
public class AggregatorStarter implements Runnable {


    private final Consumer<String, UserActionAvro> consumer;
    private final Producer<String, SpecificRecordBase> producer;
    private final UserActionHandler handler;
    private final SettingsConfig settingsConfig;

    @Override
    public void run() {
        try {
            log.info("Started consumers");
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            log.info("Подписка на топик: {}", settingsConfig.getAction());
            consumer.subscribe(List.of(settingsConfig.getAction()));

            log.info("Получение данных");
            while (true) {
                ConsumerRecords<String, UserActionAvro> records = consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, UserActionAvro> record : records) {
                    for (EventSimilarityAvro eventSimilarityAvro : handler.handle(record.value())) {
                        producer.send(new ProducerRecord<>(settingsConfig.getSimilarity(), record.key(), eventSimilarityAvro));
                    }

                    consumer.commitAsync();
                }
            }
        } catch (WakeupException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Сбой обработки ", e);
            log.error(e.getMessage());
        } finally {
            try {
                //  handler.flush();
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                // handler.close();
            }
        }
    }

    @PostConstruct
    public void init() {
        Thread thread = new Thread(this);
        thread.setName("aggregator");
        thread.start();
    }
}
