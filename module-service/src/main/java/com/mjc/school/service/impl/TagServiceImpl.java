package com.mjc.school.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.entity.TagEntity;
import com.mjc.school.repository.query.TagRepositorySearchParams;
import com.mjc.school.service.TagService;
import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.exception.AlreadyExistException;
import com.mjc.school.service.exception.NotFoundException;
import com.mjc.school.service.exception.PatchApplyException;
import com.mjc.school.service.mapper.TagMapper;
import com.mjc.school.service.query.TagServiceSearchParams;
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
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final NewsRepository newsRepository;

    private final TagMapper tagMapper;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .findAndRegisterModules();

    private final Validator springValidator;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, NewsRepository newsRepository, TagMapper tagMapper) {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            springValidator = validatorFactory.usingContext().getValidator();
        }
        this.tagMapper = tagMapper;
        this.tagRepository = tagRepository;
        this.newsRepository = newsRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TagDtoResponse> getAll(Pageable pageable, TagServiceSearchParams serviceParams) {
        validateConstraintsOrThrowException(serviceParams);
        TagRepositorySearchParams repositoryParams = tagMapper.serviceParamsToRepositoryParams(serviceParams);

        Page<TagEntity> tagEntityPage = tagRepository.getAll(pageable, repositoryParams);
        List<TagDtoResponse> content = tagMapper.listOfEntitiesToListOfResponses(tagEntityPage.getContent());
        return new PageImpl<>(content, pageable, tagEntityPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    @Override
    public TagDtoResponse getById(Long id) {
        TagEntity entity = tagRepository.getById(id)
                .orElseThrow(() -> new NotFoundException(TAG_DOES_NOT_EXIST.getId(), format(TAG_DOES_NOT_EXIST.getMessage(), id)));

        return tagMapper.entityToDtoResponse(entity);
    }

    @Transactional
    @Override
    public TagDtoResponse create(TagDtoRequest createRequest) {
        if (tagRepository.existByName(createRequest.name())) {
            throw new AlreadyExistException(TAG_ALREADY_EXIST.getId(), TAG_ALREADY_EXIST.getMessage());
        }
        validateConstraintsOrThrowException(createRequest);

        TagEntity createdTag = tagRepository.create(tagMapper.dtoRequestToEntity(createRequest));
        return tagMapper.entityToDtoResponse(createdTag);
    }

    @Transactional
    @Override
    public TagDtoResponse update(Long id, JsonPatch patch) {
        Optional<TagEntity> maybeNullEntity = tagRepository.getById(id);
        if (maybeNullEntity.isEmpty()) {
            throw new NotFoundException(TAG_DOES_NOT_EXIST.getId(), format(TAG_DOES_NOT_EXIST.getMessage(), id));
        }
        TagDtoRequest request = tagMapper.entityToRequest(maybeNullEntity.get());

        try {
            JsonNode node = patch.apply(objectMapper.convertValue(request, JsonNode.class));
            TagDtoRequest patchedTag = objectMapper.treeToValue(node, TagDtoRequest.class);
            validateConstraintsOrThrowException(patchedTag);

            TagEntity entity = tagMapper.dtoRequestToEntity(patchedTag);
            entity.setId(id);
            TagEntity updateResult = tagRepository.update(entity);
            return tagMapper.entityToDtoResponse(updateResult);
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new PatchApplyException(e.getMessage(),
                    APPLYING_TAG_PATCH_PROBLEM.getId(),
                    format(APPLYING_TAG_PATCH_PROBLEM.getMessage(), id));
        }
    }

    @Transactional
    @Override
    public boolean deleteById(Long id) {
        if (!tagRepository.existById(id)) {
            throw new NotFoundException(TAG_DOES_NOT_EXIST.getId(), format(TAG_DOES_NOT_EXIST.getMessage(), id));
        }
        return tagRepository.deleteById(id);
    }

    private <T> void validateConstraintsOrThrowException(T object) {
        Set<ConstraintViolation<T>> constraintViolations = springValidator.validate(object);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    @Override
    public List<TagDtoResponse> getByNewsId(Long id) {
        if (!newsRepository.existById(id)) {
            throw new NotFoundException(NEWS_DOES_NOT_EXIST.getId(), format(NEWS_DOES_NOT_EXIST.getMessage(), id));
        }
        return tagMapper.listOfEntitiesToListOfResponses(tagRepository.getByNewsId(id));
    }
}
