package ru.practicum.stats.aggregator.config;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties("settings")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class SettingsConfig {
    private final String url;
    private final String action;
    private final String similarity;
}