package ru.practicum.api.comment;

import org.springframework.validation.annotation.Validated;

@Validated
public interface ConventialCommentApi extends AdminCommentApi, PublicCommentApi, UserCommentApi {
}
