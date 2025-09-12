package ru.practicum.stats.collector.config;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ConfigurationProperties("kafka.settings")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class KafkaSettingsConfig {
    String url;
    String topic;
}
