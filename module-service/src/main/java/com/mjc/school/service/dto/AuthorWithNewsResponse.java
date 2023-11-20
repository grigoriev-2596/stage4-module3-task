package com.mjc.school.service.dto;

public record AuthorWithNewsResponse(
        Long id,
        String name,
        int newsAmount
) {
}
