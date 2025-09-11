package ru.practicum.client;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.proto.*;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnalyzerGrpcClient {

    @GrpcClient("analyzer")
    private final RecommendationsControllerGrpc.RecommendationsControllerBlockingStub client;

    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        return Lists.newArrayList(client.getInteractionsCount(request));
    }

    public List<RecommendedEventProto> getSimilarEvent(SimilarEventsRequestProto request) {
        return Lists.newArrayList(client.getSimilarEvents(request));
    }

    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        return Lists.newArrayList(client.getRecommendationsForUser(request));
    }
}