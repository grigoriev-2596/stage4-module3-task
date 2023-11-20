package com.mjc.school.controller;


import com.mjc.school.constant.ApiConstant;
import com.mjc.school.service.dto.AuthenticationDtoRequest;
import com.mjc.school.service.dto.AuthenticationDtoResponse;
import com.mjc.school.service.dto.RegisterDtoRequest;
import com.mjc.school.service.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/v1" + ApiConstant.AUTH_BASE_URI + ApiConstant.REGISTER_BASE_URI)
    public AuthenticationDtoResponse register(@RequestBody RegisterDtoRequest request) {
        return service.register(request);
    }

    @PostMapping("/v1" + ApiConstant.AUTH_BASE_URI + ApiConstant.AUTHENTICATE_BASE_URI)
    public AuthenticationDtoResponse authenticate(@RequestBody AuthenticationDtoRequest request) {
        return service.authenticate(request);
    }
}
