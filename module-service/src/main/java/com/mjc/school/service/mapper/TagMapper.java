package com.mjc.school.service.mapper;

import com.mjc.school.repository.entity.TagEntity;
import com.mjc.school.repository.query.TagRepositorySearchParams;
import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.query.TagServiceSearchParams;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TagMapper {

    @Mapping(target = "news", ignore = true)
    public abstract TagEntity dtoRequestToEntity(TagDtoRequest dto);

    public abstract TagDtoResponse entityToDtoResponse(TagEntity entity);

    public abstract List<TagDtoResponse> listOfEntitiesToListOfResponses(List<TagEntity> entities);

    public abstract TagRepositorySearchParams serviceParamsToRepositoryParams(TagServiceSearchParams serviceParams);

    public abstract TagDtoRequest entityToRequest(TagEntity entity);
}
