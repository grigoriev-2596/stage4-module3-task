package com.mjc.school.implementation;

import com.mjc.school.repository.CommentRepository;
import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.entity.CommentEntity;
import com.mjc.school.repository.entity.NewsEntity;
import com.mjc.school.repository.query.CommentRepositorySearchParams;
import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;
import com.mjc.school.service.impl.CommentServiceImpl;
import com.mjc.school.service.mapper.CommentMapper;
import com.mjc.school.service.query.CommentServiceSearchParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private CommentDtoRequest request;
    private CommentEntity entity;
    private CommentEntity nullIdEntity;
    private CommentDtoResponse expectedResponse;
    private CommentRepositorySearchParams repositoryEmptyParams;
    private CommentServiceSearchParams serviceEmptyParams;

    @BeforeEach
    public void setup() {

        String content = "Test comment content";
        Long newsId = 46L;
        Long id = 1L;
        LocalDateTime now = LocalDateTime.now();
        NewsEntity news = NewsEntity.newBuilder().setId(1L).build();

        serviceEmptyParams = new CommentServiceSearchParams(null);
        repositoryEmptyParams = new CommentRepositorySearchParams(null);
        request = new CommentDtoRequest(content, newsId);
        entity = new CommentEntity(id, content, news, now, now);
        nullIdEntity = new CommentEntity(null, content, news, now, now);
        expectedResponse = new CommentDtoResponse(id, content, now, now, news.getId());
    }

    @Test
    public void successfulGetByIdTest() {
        given(commentMapper.entityToDtoResponse(entity)).willReturn(expectedResponse);
        given(commentRepository.getById(entity.getId())).willReturn(Optional.of(entity));
        CommentDtoResponse actual = commentService.getById(entity.getId());

        assertEquals(expectedResponse, actual);
        verify(commentRepository, times(1)).getById(entity.getId());
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    public void successfulCreateTest() {
        given(newsRepository.existById(request.newsId())).willReturn(true);
        given(commentMapper.dtoRequestToEntity(request)).willReturn(nullIdEntity);
        given(commentRepository.create(nullIdEntity)).willReturn(entity);
        given(commentMapper.entityToDtoResponse(entity)).willReturn(expectedResponse);
        CommentDtoResponse actual = commentService.create(request);

        assertEquals(expectedResponse, actual);
        verify(commentRepository, times(1)).create(nullIdEntity);
        verify(newsRepository, times(1)).existById(request.newsId());
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    public void validationFailedWhenCreatingTest() {
        String invalidContent = "co";
        long newsId = 2L;
        given(newsRepository.existById(newsId)).willReturn(true);
        CommentDtoRequest invalidRequest = new CommentDtoRequest(invalidContent, newsId);

        assertThrows(ConstraintViolationException.class, () -> commentService.create(invalidRequest));
    }

    @Test
    public void successfulGetAllTest() {
        Pageable pageable = PageRequest.of(0, 1);
        List<CommentEntity> entityList = List.of(this.entity);
        List<CommentDtoResponse> expectedResponseList = List.of(expectedResponse);

        given(commentMapper.serviceParamsToRepositoryParams(serviceEmptyParams)).willReturn(repositoryEmptyParams);
        given(commentMapper.listOfEntitiesToListOfResponses(entityList)).willReturn(expectedResponseList);
        given(commentRepository.getAll(pageable, repositoryEmptyParams))
                .willReturn(new PageImpl<>(entityList, pageable, 1));

        List<CommentDtoResponse> actual = commentService.getAll(pageable, serviceEmptyParams).getContent();

        assertEquals(expectedResponseList, actual);
        verify(commentRepository, times(1)).getAll(any(Pageable.class), any(CommentRepositorySearchParams.class));
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    public void validationFailedWhenGettingAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        String invalidContent = "ic";
        CommentServiceSearchParams invalidParam = new CommentServiceSearchParams(invalidContent);

        assertThrows(ConstraintViolationException.class, () -> commentService.getAll(pageable, invalidParam));
    }

    @Test
    void successfulDeleteTest() {
        Long id = entity.getId();
        given(commentRepository.existById(id)).willReturn(true);
        given(commentRepository.deleteById(id)).willReturn(true);

        assertTrue(commentService.deleteById(id));
        verify(commentRepository, times(1)).deleteById(id);
        verify(commentRepository, times(1)).existById(id);
        verifyNoMoreInteractions(commentRepository);
    }
}