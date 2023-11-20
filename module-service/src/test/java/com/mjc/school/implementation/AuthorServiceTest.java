package com.mjc.school.implementation;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.entity.AuthorEntity;
import com.mjc.school.repository.query.AuthorRepositorySearchParams;
import com.mjc.school.service.AuthorService;
import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;
import com.mjc.school.service.impl.AuthorServiceImpl;
import com.mjc.school.service.mapper.AuthorMapper;
import com.mjc.school.service.query.AuthorServiceSearchParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {
    @Mock
    AuthorRepository authorRepository;

    AuthorService authorService;

    private AuthorDtoRequest request;
    private AuthorEntity entity;
    private AuthorDtoResponse expectedResponse;

    @BeforeEach
    public void setup() {
        authorService = new AuthorServiceImpl(authorRepository, Mappers.getMapper(AuthorMapper.class));

        LocalDateTime now = LocalDateTime.now();
        String name = "Grigoriev Egor";
        long id = 1L;

        request = new AuthorDtoRequest(name);
        entity = new AuthorEntity(id, name, now, now);
        expectedResponse = new AuthorDtoResponse(id, name, now, now);
    }

    @Test
    public void successfulGetByIdTest() {
        Long id = entity.getId();
        given(authorRepository.getById(id)).willReturn(Optional.of(entity));
        AuthorDtoResponse actual = authorService.getById(id);

        assertEquals(expectedResponse, actual);
        verify(authorRepository, times(1)).getById(id);
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    public void successfulCreateTest() {
        given(authorRepository.create(any(AuthorEntity.class))).willReturn(entity);
        AuthorDtoResponse actual = authorService.create(request);

        assertEquals(expectedResponse, actual);
        verify(authorRepository, times(1)).create(any(AuthorEntity.class));
        verify(authorRepository, times(1)).existByName(request.name());
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    public void validationFailedWhenCreatingTest() {
        AuthorDtoRequest invalidRequest = new AuthorDtoRequest("Invalid author name value");
        assertThrows(ConstraintViolationException.class, () -> authorService.create(invalidRequest));
    }

    @Test
    public void successfulGetAllTest() {
        Pageable pageable = PageRequest.of(0, 1);
        given(authorRepository.getAll(any(Pageable.class), any(AuthorRepositorySearchParams.class)))
                .willReturn(new PageImpl<>(List.of(entity), pageable, 1));

        List<AuthorDtoResponse> actual = authorService.getAll(pageable, new AuthorServiceSearchParams(null)).getContent();

        assertEquals(List.of(expectedResponse), actual);
        verify(authorRepository, times(1)).getAll(any(Pageable.class), any(AuthorRepositorySearchParams.class));
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    public void validationAuthorNameFailedWhenGettingAllTest() {
        Pageable pageable = PageRequest.of(0, 1);
        AuthorServiceSearchParams invalidParam = new AuthorServiceSearchParams("Ab");

        assertThrows(ConstraintViolationException.class, () -> authorService.getAll(pageable, invalidParam));
    }

    @Test
    public void successfulDeleteTest() {
        Long id = entity.getId();
        given(authorRepository.existById(id)).willReturn(true);
        given(authorRepository.deleteById(id)).willReturn(true);

        assertTrue(authorService.deleteById(id));

        verify(authorRepository, times(1)).deleteById(id);
        verify(authorRepository, times(1)).existById(id);
        verifyNoMoreInteractions(authorRepository);
    }
}