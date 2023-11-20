package com.mjc.school.service.dto;

public record AuthenticationDtoRequest(
        String email,
        String password) {
}
