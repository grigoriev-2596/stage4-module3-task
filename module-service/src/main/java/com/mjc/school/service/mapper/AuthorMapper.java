package com.mjc.school.service.mapper;

import com.mjc.school.repository.entity.AuthorEntity;
import com.mjc.school.repository.query.AuthorRepositorySearchParams;
import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;
import com.mjc.school.service.query.AuthorServiceSearchParams;
import org.mapstruct.Mapping;

import java.util.List;

@org.mapstruct.Mapper(componentModel = "spring")
public abstract class AuthorMapper {

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "news", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract AuthorEntity dtoRequestToEntity(AuthorDtoRequest request);

    public abstract AuthorDtoResponse entityToDtoResponse(AuthorEntity entity);

    public abstract List<AuthorDtoResponse> listOfEntitiesToListOfResponses(List<AuthorEntity> entities);

    public abstract AuthorRepositorySearchParams serviceParamsToRepositoryParams(AuthorServiceSearchParams serviceParams);

    public abstract AuthorDtoRequest entityToRequest(AuthorEntity entity);
}
