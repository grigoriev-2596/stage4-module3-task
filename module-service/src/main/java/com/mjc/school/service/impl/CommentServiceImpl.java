package com.mjc.school.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mjc.school.repository.CommentRepository;
import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.entity.CommentEntity;
import com.mjc.school.repository.query.CommentRepositorySearchParams;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;
import com.mjc.school.service.exception.NotFoundException;
import com.mjc.school.service.exception.PatchApplyException;
import com.mjc.school.service.mapper.CommentMapper;
import com.mjc.school.service.query.CommentServiceSearchParams;
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
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final NewsRepository newsRepository;

    private final CommentMapper commentMapper;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .findAndRegisterModules();

    private final Validator springValidator;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, NewsRepository newsRepository, CommentMapper commentMapper) {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            springValidator = validatorFactory.usingContext().getValidator();
        }
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
        this.newsRepository = newsRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CommentDtoResponse> getAll(Pageable pageable, CommentServiceSearchParams serviceParams) {
        validateConstraintsOrThrowException(serviceParams);
        CommentRepositorySearchParams repositoryParams = commentMapper.serviceParamsToRepositoryParams(serviceParams);

        Page<CommentEntity> commentEntityPage = commentRepository.getAll(pageable, repositoryParams);
        List<CommentDtoResponse> commentDtoResponses = commentMapper.listOfEntitiesToListOfResponses(commentEntityPage.getContent());
        return new PageImpl<>(commentDtoResponses, pageable, commentEntityPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    @Override
    public CommentDtoResponse getById(Long id) {
        CommentEntity entity = commentRepository.getById(id)
                .orElseThrow(() -> new NotFoundException(COMMENT_DOES_NOT_EXIST.getId(), format(COMMENT_DOES_NOT_EXIST.getMessage(), id)));

        return commentMapper.entityToDtoResponse(entity);
    }

    @Transactional
    @Override
    public CommentDtoResponse create(CommentDtoRequest createRequest) {
        newsExistOrThrowException(createRequest.newsId());
        validateConstraintsOrThrowException(createRequest);

        CommentEntity createdComment = commentRepository.create(commentMapper.dtoRequestToEntity(createRequest));
        return commentMapper.entityToDtoResponse(createdComment);
    }

    @Transactional
    @Override
    public CommentDtoResponse update(Long id, JsonPatch patch) {
        Optional<CommentEntity> maybeNullEntity = commentRepository.getById(id);
        if (maybeNullEntity.isEmpty()) {
            throw new NotFoundException(COMMENT_DOES_NOT_EXIST.getId(), format(COMMENT_DOES_NOT_EXIST.getMessage(), id));
        }
        CommentDtoRequest request = commentMapper.entityToRequest(maybeNullEntity.get());

        try {
            JsonNode node = patch.apply(objectMapper.convertValue(request, JsonNode.class));
            CommentDtoRequest patchedComment = objectMapper.treeToValue(node, CommentDtoRequest.class);
            validateConstraintsOrThrowException(patchedComment);

            CommentEntity entity = commentMapper.dtoRequestToEntity(patchedComment);
            entity.setId(id);
            CommentEntity updateResult = commentRepository.update(entity);
            return commentMapper.entityToDtoResponse(updateResult);
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new PatchApplyException(e.getMessage(),
                    APPLYING_COMMENT_PATCH_PROBLEM.getId(),
                    format(APPLYING_COMMENT_PATCH_PROBLEM.getMessage(), id));
        }
    }

    @Transactional
    @Override
    public boolean deleteById(Long id) {
        if (!commentRepository.existById(id)) {
            throw new NotFoundException(COMMENT_DOES_NOT_EXIST.getId(), format(COMMENT_DOES_NOT_EXIST.getMessage(), id));
        }
        return commentRepository.deleteById(id);
    }

    private <T> void validateConstraintsOrThrowException(T object) {
        Set<ConstraintViolation<T>> constraintViolations = springValidator.validate(object);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    private void newsExistOrThrowException(Long id) {
        if (!newsRepository.existById(id)) {
            throw new NotFoundException(NEWS_DOES_NOT_EXIST.getId(), format(NEWS_DOES_NOT_EXIST.getMessage(), id));
        }
    }

    @Override
    public List<CommentDtoResponse> getByNewsId(Long id) {
        if (!newsRepository.existById(id)) {
            throw new NotFoundException(NEWS_DOES_NOT_EXIST.getId(), format(NEWS_DOES_NOT_EXIST.getMessage(), id));
        }
        return commentMapper.listOfEntitiesToListOfResponses(commentRepository.getByNewsId(id));
    }
}
