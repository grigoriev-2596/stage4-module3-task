package com.mjc.school.service.query;

import javax.validation.constraints.Size;

import static com.mjc.school.service.validation.ValidationConstants.TAG_NAME_MAX_SIZE;
import static com.mjc.school.service.validation.ValidationConstants.TAG_NAME_MIN_SIZE;

public record TagServiceSearchParams(
        @Size(min = TAG_NAME_MIN_SIZE, max = TAG_NAME_MAX_SIZE)
        String name) {
}
