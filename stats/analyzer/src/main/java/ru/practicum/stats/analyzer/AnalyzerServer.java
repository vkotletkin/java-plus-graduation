package ru.practicum.stats.analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AnalyzerServer {
    public static void main(String[] args) {
        SpringApplication.run(AnalyzerServer.class, args);
    }
}
