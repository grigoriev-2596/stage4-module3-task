package com.mjc.school.service.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.mjc.school.service.validation.ValidationConstants.COMMENT_CONTENT_MAX_SIZE;
import static com.mjc.school.service.validation.ValidationConstants.COMMENT_CONTENT_MIN_SIZE;

public record CommentDtoRequest(
        @NotBlank
        @Size(min = COMMENT_CONTENT_MIN_SIZE, max = COMMENT_CONTENT_MAX_SIZE)
        String content,

        @NotNull
        @Min(1)
        Long newsId) {
}
