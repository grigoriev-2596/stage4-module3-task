package com.mjc.school.repository.dto;

public record AuthorWithNews(
        Long id,
        String name,
        Long numberOfNews) {
}
