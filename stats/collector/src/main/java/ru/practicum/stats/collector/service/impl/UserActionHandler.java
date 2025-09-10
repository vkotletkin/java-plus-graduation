package ru.practicum.stats.collector.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.practicum.stats.collector.mapper.UserActionMapper;
import ru.practicum.stats.collector.service.CollectorHandler;
import ru.practicum.stats.collector.service.impl.kafka.UserActionProducer;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class UserActionHandler implements CollectorHandler<UserActionProto> {

    private final UserActionProducer userActionProducer;

    public void handle(UserActionProto proto) {
        userActionProducer.send(UserActionMapper.mapToAvro(proto));
    }
}
