package ru.practicum.stats.aggregator.service.domain;

public record PairEvent(Long first, Long second) {

    public static PairEvent create(Long a, Long b) {
        return a < b ? new PairEvent(a, b) : new PairEvent(b, a);
    }
}