package ru.practicum.api.comment;

import org.springframework.validation.annotation.Validated;

@Validated
public interface ConventionalCommentApi extends AdminCommentApi, PublicCommentApi, UserCommentApi {
}
