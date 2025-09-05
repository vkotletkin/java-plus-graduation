package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.comment.AdminCommentApi;
import ru.practicum.api.comment.PublicCommentApi;
import ru.practicum.api.comment.UserCommentApi;

@FeignClient(name = "comment-service")
public interface CommentFeignClient extends UserCommentApi, AdminCommentApi, PublicCommentApi {
}

