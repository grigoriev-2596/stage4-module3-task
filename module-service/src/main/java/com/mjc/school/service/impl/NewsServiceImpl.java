package com.mjc.school.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.entity.AuthorEntity;
import com.mjc.school.repository.entity.NewsEntity;
import com.mjc.school.repository.entity.TagEntity;
import com.mjc.school.repository.query.NewsRepositorySearchParams;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.dto.NewsDtoRequest;
import com.mjc.school.service.dto.NewsDtoResponse;
import com.mjc.school.service.exception.AlreadyExistException;
import com.mjc.school.service.exception.NotFoundException;
import com.mjc.school.service.exception.PatchApplyException;
import com.mjc.school.service.mapper.NewsMapper;
import com.mjc.school.service.query.NewsServiceSearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.mjc.school.service.exception.ErrorCode.*;
import static java.lang.String.format;

@Service
public class NewsServiceImpl implements BaseService<NewsDtoRequest, NewsDtoResponse, Long, JsonPatch, NewsServiceSearchParams> {

    private final NewsRepository newsRepository;
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;

    private final NewsMapper newsMapper;
    private final Validator springValidator;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .findAndRegisterModules();

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository, AuthorRepository authorRepository,
                           TagRepository tagRepository, NewsMapper newsMapper) {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            springValidator = validatorFactory.usingContext().getValidator();
        }
        this.newsMapper = newsMapper;
        this.newsRepository = newsRepository;
        this.authorRepository = authorRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<NewsDtoResponse> getAll(Pageable pageable, NewsServiceSearchParams serviceParams) {
        validateConstraintsOrThrowException(serviceParams);
        if (serviceParams.tagIds() != null) {
            tagsExistOrThrowException(serviceParams.tagIds());
        }

        NewsRepositorySearchParams repositoryParams = newsMapper.serviceParamsToRepositoryParams(serviceParams);

        Page<NewsEntity> newsEntityPage = newsRepository.getAll(pageable, repositoryParams);
        List<NewsDtoResponse> newsDtoResponses = newsMapper.listOfEntitiesToListOfResponses(newsEntityPage.getContent());
        return new PageImpl<>(newsDtoResponses, pageable, newsEntityPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    @Override
    public NewsDtoResponse getById(Long id) {
        NewsEntity entity = newsRepository.getById(id)
                .orElseThrow(() -> new NotFoundException(NEWS_DOES_NOT_EXIST.getId(), format(NEWS_DOES_NOT_EXIST.getMessage(), id)));

        return newsMapper.entityToDtoResponse(entity);
    }

    @Transactional
    @Override
    public NewsDtoResponse create(NewsDtoRequest createRequest) {
        if (newsRepository.existByTitle(createRequest.title())) {
            throw new AlreadyExistException(NEWS_ALREADY_EXIST.getId(), NEWS_ALREADY_EXIST.getMessage());
        }
        validateConstraintsOrThrowException(createRequest);
        createAuthorIfNotExists(createRequest.authorName());
        createTagsIfNotExist(createRequest.tagNames());

        NewsEntity createdNews = newsRepository.create(newsMapper.dtoRequestToEntity(createRequest));
        return newsMapper.entityToDtoResponse(createdNews);
    }

    @Transactional
    @Override
    public NewsDtoResponse update(Long id, JsonPatch patch) {
        Optional<NewsEntity> maybeNullEntity = newsRepository.getById(id);
        if (maybeNullEntity.isEmpty()) {
            throw new NotFoundException(NEWS_DOES_NOT_EXIST.getId(), format(NEWS_DOES_NOT_EXIST.getMessage(), id));
        }
        NewsDtoRequest request = newsMapper.entityToRequest(maybeNullEntity.get());

        try {
            JsonNode node = patch.apply(objectMapper.convertValue(request, JsonNode.class));
            NewsDtoRequest patchedNews = objectMapper.treeToValue(node, NewsDtoRequest.class);

            validateConstraintsOrThrowException(patchedNews);
            createAuthorIfNotExists(patchedNews.authorName());
            createTagsIfNotExist(patchedNews.tagNames());

            NewsEntity entity = newsMapper.dtoRequestToEntity(patchedNews);
            entity.setId(id);
            NewsEntity updateResult = newsRepository.update(entity);
            return newsMapper.entityToDtoResponse(updateResult);
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new PatchApplyException(e.getMessage(),
                    APPLYING_NEWS_PATCH_PROBLEM.getId(),
                    format(APPLYING_NEWS_PATCH_PROBLEM.getMessage(), id));
        }
    }

    @Transactional
    @Override
    public boolean deleteById(Long id) {
        if (!newsRepository.existById(id)) {
            throw new NotFoundException(NEWS_DOES_NOT_EXIST.getId(), format(NEWS_DOES_NOT_EXIST.getMessage(), id));
        }
        return newsRepository.deleteById(id);
    }

    private <T> void validateConstraintsOrThrowException(T object) {
        Set<ConstraintViolation<T>> constraintViolations = springValidator.validate(object);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    private void createAuthorIfNotExists(String name) {
        if (!authorRepository.existByName(name)) {
            AuthorEntity author = new AuthorEntity();
            author.setName(name);
            authorRepository.create(author);
        }
    }

    private void createTagsIfNotExist(List<String> tagNames) {
        tagNames.forEach(name -> {
            if (!tagRepository.existByName(name)) {
                tagRepository.create(TagEntity.builder().name(name).build());
            }
        });
    }

    private void tagsExistOrThrowException(List<Long> ids) {
        ids.forEach(id -> {
            if (!tagRepository.existById(id)) {
                throw new NotFoundException(TAG_DOES_NOT_EXIST.getId(), format(TAG_DOES_NOT_EXIST.getMessage(), id));
            }
        });
    }
}
