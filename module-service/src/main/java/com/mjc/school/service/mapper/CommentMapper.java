package com.mjc.school.service.mapper;

import com.mjc.school.repository.entity.CommentEntity;
import com.mjc.school.repository.impl.NewsRepositoryImpl;
import com.mjc.school.repository.query.CommentRepositorySearchParams;
import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;
import com.mjc.school.service.query.CommentServiceSearchParams;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.mapstruct.Mapper(componentModel = "spring")
public abstract class CommentMapper {

    @Autowired
    protected NewsRepositoryImpl newsRepository;

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "news", expression = "java( newsRepository.getById(request.newsId()).get() )")
    public abstract CommentEntity dtoRequestToEntity(CommentDtoRequest request);

    @Mapping(target = "newsId", source = "news.id")
    public abstract CommentDtoResponse entityToDtoResponse(CommentEntity entity);

    public abstract List<CommentDtoResponse> listOfEntitiesToListOfResponses(List<CommentEntity> entities);

    public abstract CommentRepositorySearchParams serviceParamsToRepositoryParams(CommentServiceSearchParams serviceParams);

    @Mapping(target = "newsId", source = "news.id")
    public abstract CommentDtoRequest entityToRequest(CommentEntity entity);
}
