package com.mjc.school.service.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import static com.mjc.school.service.validation.ValidationConstants.TAG_NAME_MAX_SIZE;
import static com.mjc.school.service.validation.ValidationConstants.TAG_NAME_MIN_SIZE;

public record TagDtoRequest(
        @NotEmpty
        @Size(min = TAG_NAME_MIN_SIZE, max = TAG_NAME_MAX_SIZE)
        String name) {
}
