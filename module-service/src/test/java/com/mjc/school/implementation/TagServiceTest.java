package com.mjc.school.implementation;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.entity.TagEntity;
import com.mjc.school.repository.query.TagRepositorySearchParams;
import com.mjc.school.service.TagService;
import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.impl.TagServiceImpl;
import com.mjc.school.service.mapper.TagMapper;
import com.mjc.school.service.query.TagServiceSearchParams;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {
    @Mock
    TagRepository tagRepository;

    @Mock
    NewsRepository newsRepository;

    TagService tagService;

    private TagDtoRequest request;
    private TagEntity entity;
    private TagDtoResponse expectedResponse;

    @BeforeEach
    public void setup() {
        tagService = new TagServiceImpl(tagRepository, newsRepository, Mappers.getMapper(TagMapper.class));

        String name = "climate";
        long id = 1L;

        request = new TagDtoRequest(name);
        entity = new TagEntity(id, name);
        expectedResponse = new TagDtoResponse(id, name);
    }

    @Test
    public void successfulGetByIdTest() {
        Long id = entity.getId();
        given(tagRepository.getById(id)).willReturn(Optional.of(entity));
        TagDtoResponse actual = tagService.getById(id);

        assertEquals(expectedResponse, actual);
        verify(tagRepository, times(1)).getById(id);
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    public void successfulCreateTest() {
        given(tagRepository.create(any(TagEntity.class))).willReturn(entity);
        TagDtoResponse actual = tagService.create(request);

        assertEquals(expectedResponse, actual);
        verify(tagRepository, times(1)).create(any(TagEntity.class));
        verify(tagRepository, times(1)).existByName(request.name());
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    public void validationFailedWhenCreatingTest() {
        TagDtoRequest invalidRequest = new TagDtoRequest("Invalid tag name value");
        assertThrows(ConstraintViolationException.class, () -> tagService.create(invalidRequest));
    }

    @Test
    public void successfulGetAllTest() {
        Pageable pageable = PageRequest.of(0, 1);
        given(tagRepository.getAll(any(Pageable.class), any(TagRepositorySearchParams.class)))
                .willReturn(new PageImpl<>(List.of(entity), pageable, 1));

        List<TagDtoResponse> actual = tagService.getAll(pageable, new TagServiceSearchParams(null)).getContent();

        assertEquals(List.of(expectedResponse), actual);
        verify(tagRepository, times(1)).getAll(any(Pageable.class), any(TagRepositorySearchParams.class));
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    public void validationTagNameFailedWhenGettingAllTest() {
        Pageable pageable = PageRequest.of(0, 1);
        TagServiceSearchParams invalidParam = new TagServiceSearchParams("Invalid tag name for search");

        assertThrows(ConstraintViolationException.class, () -> tagService.getAll(pageable, invalidParam));
    }

    @Test
    public void successfulDeleteTest() {
        Long id = entity.getId();
        given(tagRepository.existById(id)).willReturn(true);
        given(tagRepository.deleteById(id)).willReturn(true);

        assertTrue(tagService.deleteById(id));

        verify(tagRepository, times(1)).deleteById(id);
        verify(tagRepository, times(1)).existById(id);
        verifyNoMoreInteractions(tagRepository);
    }

}