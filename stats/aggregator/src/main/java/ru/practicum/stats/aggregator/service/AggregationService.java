package ru.practicum.stats.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.aggregator.config.KafkaSettingsConfig;
import ru.practicum.stats.aggregator.service.domain.UserActionDomainService;

import java.time.Duration;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationService implements Runnable {

    // Domain services
    private final UserActionDomainService userActionDomainService;

    // Kafka
    private final Consumer<String, UserActionAvro> consumer;
    private final Producer<String, SpecificRecordBase> producer;
    private final KafkaSettingsConfig settingsConfig;

    @Override
    public void run() {
        try {

            log.info("Запуск процесса получения данных консьюмером");
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            consumer.subscribe(List.of(settingsConfig.getAction()));
            log.info("Осуществлена подписка на топик: {}", settingsConfig.getAction());

            log.info("Начало получения данных из топика");

            while (true) {
                ConsumerRecords<String, UserActionAvro> records = consumer.poll(Duration.ofMillis(500));
                log.info("Получено записей: {}", records.count());
                for (ConsumerRecord<String, UserActionAvro> datapart : records) {
                    for (EventSimilarityAvro eventSimilarityAvro : userActionDomainService.calculateSimilarityEvents(datapart.value())) {
                        producer.send(new ProducerRecord<>(settingsConfig.getSimilarity(), datapart.key(), eventSimilarityAvro));
                    }

                    consumer.commitAsync();
                }
            }
        } catch (WakeupException ignored) {
            // wakeupexception ignored by shutdown hook
        } catch (Exception e) {
            log.error("Сбой обработки: {}", e.getMessage());
        } finally {
            try {
                producer.flush();
                log.info("Буфер продюсера отправлен");
            } catch (Exception e) {
                log.error("Ошибка на окончательной отправке буфера продюсера", e);
            } finally {
                try {
                    producer.close();
                    log.info("Продюсер закрыт");
                    consumer.close();
                    log.info("Консюмер закрыт");
                } catch (Exception e) {
                    log.error("Ошибка на закрытии продюсера и консюмера: {}", e.getMessage());
                }
            }
        }
    }
}

