package com.mjc.school.service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.mjc.school.service.validation.ValidationConstants.AUTHOR_NAME_MAX_SIZE;
import static com.mjc.school.service.validation.ValidationConstants.AUTHOR_NAME_MIN_SIZE;

public record AuthorDtoRequest(
        @NotBlank
        @Size(min = AUTHOR_NAME_MIN_SIZE, max = AUTHOR_NAME_MAX_SIZE)
        String name) {
}
