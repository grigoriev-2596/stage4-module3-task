package com.mjc.school.service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

import static com.mjc.school.service.validation.ValidationConstants.*;

public record NewsDtoRequest(
        @NotBlank
        @Size(min = NEWS_TITLE_MIN_SIZE, max = NEWS_TITLE_MAX_SIZE)
        String title,

        @NotBlank
        @Size(min = NEWS_CONTENT_MIN_SIZE, max = NEWS_CONTENT_MAX_SIZE)
        String content,

        @NotBlank
        @Size(min = AUTHOR_NAME_MIN_SIZE, max = AUTHOR_NAME_MAX_SIZE)
        String authorName,

        List<@Size(min = TAG_NAME_MIN_SIZE, max = TAG_NAME_MAX_SIZE) String> tagNames) {
}
