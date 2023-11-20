package com.mjc.school.service.query;

import javax.validation.constraints.Size;

import static com.mjc.school.service.validation.ValidationConstants.AUTHOR_NAME_MAX_SIZE;
import static com.mjc.school.service.validation.ValidationConstants.AUTHOR_NAME_MIN_SIZE;

public record AuthorServiceSearchParams(
        @Size(min = AUTHOR_NAME_MIN_SIZE, max = AUTHOR_NAME_MAX_SIZE)
        String name) {
}
