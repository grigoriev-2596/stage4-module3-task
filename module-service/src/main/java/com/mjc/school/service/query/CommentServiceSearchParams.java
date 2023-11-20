package com.mjc.school.service.query;

import javax.validation.constraints.Size;

import static com.mjc.school.service.validation.ValidationConstants.COMMENT_CONTENT_MAX_SIZE;
import static com.mjc.school.service.validation.ValidationConstants.COMMENT_CONTENT_MIN_SIZE;

public record CommentServiceSearchParams(
        @Size(min = COMMENT_CONTENT_MIN_SIZE, max = COMMENT_CONTENT_MAX_SIZE)
        String content) {
}
