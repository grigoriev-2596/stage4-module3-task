package com.mjc.school.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.dto.AuthorWithNews;
import com.mjc.school.repository.entity.AuthorEntity;
import com.mjc.school.repository.query.AuthorRepositorySearchParams;
import com.mjc.school.service.AuthorService;
import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;
import com.mjc.school.service.dto.AuthorWithNewsResponse;
import com.mjc.school.service.exception.AlreadyExistException;
import com.mjc.school.service.exception.NotFoundException;
import com.mjc.school.service.exception.PatchApplyException;
import com.mjc.school.service.mapper.AuthorMapper;
import com.mjc.school.service.query.AuthorServiceSearchParams;
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
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .findAndRegisterModules();

    private final Validator springValidator;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorMapper = authorMapper;
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            springValidator = validatorFactory.usingContext().getValidator();
        }
        this.authorRepository = authorRepository;

    }

    @Transactional(readOnly = true)
    public Page<AuthorDtoResponse> getAll(Pageable pageable, AuthorServiceSearchParams serviceParams) {
        validateConstraintsOrThrowException(serviceParams);
        AuthorRepositorySearchParams repositoryParams = authorMapper.serviceParamsToRepositoryParams(serviceParams);

        Page<AuthorEntity> authorEntityPage = authorRepository.getAll(pageable, repositoryParams);
        List<AuthorDtoResponse> authorDtoResponses = authorMapper.listOfEntitiesToListOfResponses(authorEntityPage.getContent());
        return new PageImpl<>(authorDtoResponses, pageable, authorEntityPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    @Override
    public AuthorDtoResponse getById(Long id) {
        AuthorEntity entity = authorRepository.getById(id)
                .orElseThrow(() -> new NotFoundException(AUTHOR_DOES_NOT_EXIST.getId(),
                        format(AUTHOR_DOES_NOT_EXIST.getMessage(), id)));

        return authorMapper.entityToDtoResponse(entity);
    }

    @Transactional
    @Override
    public AuthorDtoResponse create(AuthorDtoRequest createRequest) {
        if (authorRepository.existByName(createRequest.name())) {
            throw new AlreadyExistException(AUTHOR_ALREADY_EXIST.getId(), AUTHOR_ALREADY_EXIST.getMessage());
        }
        validateConstraintsOrThrowException(createRequest);

        AuthorEntity createdAuthor = authorRepository.create(authorMapper.dtoRequestToEntity(createRequest));
        return authorMapper.entityToDtoResponse(createdAuthor);
    }

    @Transactional
    @Override
    public AuthorDtoResponse update(Long id, JsonPatch patch) {
        Optional<AuthorEntity> maybeNullEntity = authorRepository.getById(id);
        if (maybeNullEntity.isEmpty()) {
            throw new NotFoundException(AUTHOR_DOES_NOT_EXIST.getId(), format(AUTHOR_DOES_NOT_EXIST.getMessage(), id));
        }

        AuthorDtoRequest request = authorMapper.entityToRequest(maybeNullEntity.get());

        try {
            JsonNode node = patch.apply(objectMapper.convertValue(request, JsonNode.class));
            AuthorDtoRequest patchedAuthor = objectMapper.treeToValue(node, AuthorDtoRequest.class);
            validateConstraintsOrThrowException(patchedAuthor);

            AuthorEntity entity = authorMapper.dtoRequestToEntity(patchedAuthor);
            entity.setId(id);
            AuthorEntity updateResult = authorRepository.update(entity);
            return authorMapper.entityToDtoResponse(updateResult);
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new PatchApplyException(e.getMessage(),
                    APPLYING_AUTHOR_PATCH_PROBLEM.getId(),
                    format(APPLYING_AUTHOR_PATCH_PROBLEM.getMessage(), id));
        }
    }

    @Transactional
    @Override
    public boolean deleteById(Long id) {
        if (!authorRepository.existById(id)) {
            throw new NotFoundException(AUTHOR_DOES_NOT_EXIST.getId(), format(AUTHOR_DOES_NOT_EXIST.getMessage(), id));
        }
        return authorRepository.deleteById(id);
    }

    @Override
    public AuthorDtoResponse getByNewsId(Long id) {
        return authorRepository.getByNewsId(id)
                .map(authorMapper::entityToDtoResponse)
                .orElseThrow(() -> new NotFoundException(NEWS_DOES_NOT_EXIST.getId(),
                        format(NEWS_DOES_NOT_EXIST.getMessage(), id)));
    }

    @Override
    public Page<AuthorWithNewsResponse> getWithNewsAmount(Pageable pageable) {
        Page<AuthorWithNews> authorPage = authorRepository.getWithNewsAmount(pageable);
        List<AuthorWithNewsResponse> content = authorPage.getContent().stream()
                .map(a -> new AuthorWithNewsResponse(a.id(), a.name(), a.numberOfNews().intValue())).toList();
        return new PageImpl<>(content, pageable, authorPage.getTotalElements());
    }

    private <T> void validateConstraintsOrThrowException(T object) {
        Set<ConstraintViolation<T>> constraintViolations = springValidator.validate(object);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }
}
