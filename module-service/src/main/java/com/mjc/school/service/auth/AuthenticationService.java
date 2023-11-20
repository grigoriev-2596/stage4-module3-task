package com.mjc.school.service.auth;

import com.mjc.school.repository.UserRepository;
import com.mjc.school.repository.entity.Role;
import com.mjc.school.repository.entity.UserEntity;
import com.mjc.school.service.dto.AuthenticationDtoRequest;
import com.mjc.school.service.dto.AuthenticationDtoResponse;
import com.mjc.school.service.dto.RegisterDtoRequest;
import com.mjc.school.service.exception.AlreadyExistException;
import com.mjc.school.service.exception.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.mjc.school.service.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationDtoResponse register(RegisterDtoRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new AlreadyExistException(USER_ALREADY_EXIST.getId(), USER_ALREADY_EXIST.getMessage());
        }
        UserEntity user = UserEntity.builder()
                .firstName(request.firstname())
                .lastname(request.lastname())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ADMIN)
                .build();
         repository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthenticationDtoResponse(token);
    }

    public AuthenticationDtoResponse authenticate(AuthenticationDtoRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (AuthenticationException ex) {
            throw new JwtAuthenticationException(AUTHENTICATION_PROBLEM.getId(), AUTHENTICATION_PROBLEM.getMessage());
        }

        UserEntity user = repository.findByEmail(request.email()).get();
        String token = jwtService.generateToken(user);
        return new AuthenticationDtoResponse(token);
    }
}
