package com.mjc.school.implementation;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.entity.AuthorEntity;
import com.mjc.school.repository.entity.NewsEntity;
import com.mjc.school.repository.entity.TagEntity;
import com.mjc.school.repository.query.NewsRepositorySearchParams;
import com.mjc.school.service.dto.NewsDtoRequest;
import com.mjc.school.service.dto.NewsDtoResponse;
import com.mjc.school.service.impl.NewsServiceImpl;
import com.mjc.school.service.mapper.NewsMapper;
import com.mjc.school.service.query.NewsServiceSearchParams;
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
class NewsServiceTest {
    @Mock
    private NewsRepository newsRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private NewsMapper newsMapper;

    @InjectMocks
    private NewsServiceImpl newsService;

    private NewsDtoRequest newsDtoRequest;
    private NewsEntity newsEntity;
    private NewsEntity nullIdNewsEntity;
    private NewsDtoResponse expectedResponse;
    private NewsRepositorySearchParams repositoryEmptyParams;
    private NewsServiceSearchParams serviceEmptyParams;

    @BeforeEach
    public void setup() {
        String tagName = "climate";
        String authorName = "Grigoriev Egor";
        String newsTitle = "title";
        String newsContent = "content";
        LocalDateTime now = LocalDateTime.now();
        long newsId = 1L;

        serviceEmptyParams = new NewsServiceSearchParams(null, null, null, null, null);
        repositoryEmptyParams = new NewsRepositorySearchParams(null, null, null, null, null);

        TagEntity tagEntity = new TagEntity(1L, tagName);
        AuthorEntity authorEntity = new AuthorEntity(1L, authorName, now, now);

        newsDtoRequest = new NewsDtoRequest(newsTitle, newsContent, authorEntity.getName(), List.of(tagEntity.getName()));

        newsEntity = NewsEntity.newBuilder().setId(newsId).setTitle(newsTitle).setContent(newsContent).setCreationDate(now)
                .setLastUpdateDate(now).setAuthor(authorEntity).setTags(List.of(tagEntity)).build();

        nullIdNewsEntity = NewsEntity.newBuilder().setTitle(newsTitle).setContent(newsContent).setCreationDate(now)
                .setLastUpdateDate(now).setAuthor(authorEntity).setTags(List.of(tagEntity)).build();

        expectedResponse = new NewsDtoResponse(newsId, newsTitle, newsContent, now, now);
    }

    @Test
    public void successfulGetByIdTest() {
        given(newsMapper.entityToDtoResponse(newsEntity)).willReturn(expectedResponse);
        given(newsRepository.getById(newsEntity.getId())).willReturn(Optional.of(newsEntity));
        NewsDtoResponse actual = newsService.getById(newsEntity.getId());

        assertEquals(expectedResponse, actual);
        verify(newsRepository, times(1)).getById(newsEntity.getId());
        verifyNoMoreInteractions(newsRepository);
    }

    @Test
    public void successfulCreateTest() {
        given(newsMapper.dtoRequestToEntity(newsDtoRequest)).willReturn(nullIdNewsEntity);
        given(newsRepository.create(nullIdNewsEntity)).willReturn(newsEntity);
        given(newsMapper.entityToDtoResponse(newsEntity)).willReturn(expectedResponse);
        NewsDtoResponse actual = newsService.create(newsDtoRequest);

        assertEquals(expectedResponse, actual);
        verify(newsRepository, times(1)).create(nullIdNewsEntity);
        verify(newsRepository, times(1)).existByTitle(newsDtoRequest.title());
        verify(authorRepository, times(1)).existByName(newsDtoRequest.authorName());
        verify(tagRepository, times(1)).existByName(newsDtoRequest.tagNames().get(0));
        verifyNoMoreInteractions(newsRepository);
    }

    @Test
    public void validationFailedWhenCreatingTest() {
        String invalidTitle = "Titl";
        NewsDtoRequest invalidRequest = new NewsDtoRequest(invalidTitle, "random content", null, null);
        assertThrows(ConstraintViolationException.class, () -> newsService.create(invalidRequest));
    }

    @Test
    public void successfulGetAllTest() {
        Pageable pageable = PageRequest.of(0, 1);
        List<NewsEntity> entityList = List.of(this.newsEntity);
        List<NewsDtoResponse> expectedResponseList = List.of(expectedResponse);

        given(newsMapper.serviceParamsToRepositoryParams(serviceEmptyParams)).willReturn(repositoryEmptyParams);
        given(newsMapper.listOfEntitiesToListOfResponses(entityList)).willReturn(expectedResponseList);
        given(newsRepository.getAll(pageable, repositoryEmptyParams))
                .willReturn(new PageImpl<>(entityList, pageable, 1));

        List<NewsDtoResponse> actual = newsService.getAll(pageable, serviceEmptyParams).getContent();

        assertEquals(expectedResponseList, actual);
        verify(newsRepository, times(1)).getAll(any(Pageable.class), any(NewsRepositorySearchParams.class));
        verifyNoMoreInteractions(newsRepository);
    }

    @Test
    public void validationFailedWhenGettingAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        String invalidTitle = "Invalid news title value for validation failure";
        NewsServiceSearchParams invalidParam = new NewsServiceSearchParams(invalidTitle, null, null, null, null);

        assertThrows(ConstraintViolationException.class, () -> newsService.getAll(pageable, invalidParam));
    }

    @Test
    void successfulDeleteTest() {
        Long id = newsEntity.getId();
        given(newsRepository.existById(id)).willReturn(true);
        given(newsRepository.deleteById(id)).willReturn(true);

        assertTrue(newsService.deleteById(id));
        verify(newsRepository, times(1)).deleteById(id);
        verify(newsRepository, times(1)).existById(id);
        verifyNoMoreInteractions(newsRepository);
    }
}