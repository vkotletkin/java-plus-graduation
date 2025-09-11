package ru.practicum.stats.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.comment.ConventionalCommentApi;

@FeignClient(name = "comment-service")
public interface CommentFeignClient extends ConventionalCommentApi {
}
