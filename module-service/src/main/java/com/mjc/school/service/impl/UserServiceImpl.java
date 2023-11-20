package com.mjc.school.service.impl;

import com.mjc.school.repository.UserRepository;
import com.mjc.school.service.exception.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.mjc.school.service.exception.ErrorCode.USER_DOES_NOT_EXIST;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new JwtAuthenticationException(USER_DOES_NOT_EXIST.getId(),
                        String.format(USER_DOES_NOT_EXIST.getMessage(), username)));
    }
}
