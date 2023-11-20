package com.mjc.school.service.dto;

public record RegisterDtoRequest(
        String firstname,
        String lastname,
        String email,
        String password) {
}
