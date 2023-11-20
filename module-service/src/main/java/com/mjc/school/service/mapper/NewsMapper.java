package com.mjc.school.service.mapper;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.entity.NewsEntity;
import com.mjc.school.repository.query.NewsRepositorySearchParams;
import com.mjc.school.service.dto.NewsDtoRequest;
import com.mjc.school.service.dto.NewsDtoResponse;
import com.mjc.school.service.query.NewsServiceSearchParams;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.mapstruct.Mapper(componentModel = "spring", uses = {AuthorMapper.class, TagMapper.class})
public abstract class NewsMapper {

    @Autowired
    protected TagRepository tagRepository;

    @Autowired
    protected AuthorRepository authorRepository;

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "author", expression = "java(authorRepository.getByName(request.authorName()).get())")
    @Mapping(target = "tags", expression = "java(request.tagNames().stream().map(name -> tagRepository.getByName(name).get()).toList())")
    @Mapping(target = "id", ignore = true)
    public abstract NewsEntity dtoRequestToEntity(NewsDtoRequest request);

    public abstract NewsDtoResponse entityToDtoResponse(NewsEntity entity);

    public abstract List<NewsDtoResponse> listOfEntitiesToListOfResponses(List<NewsEntity> entities);

    public abstract NewsRepositorySearchParams serviceParamsToRepositoryParams(NewsServiceSearchParams serviceParams);

    @Mapping(target = "authorName", source = "author.name")
    @Mapping(target = "tagNames", expression = "java(entity.getTags().stream().map(t -> t.getName()).toList())")
    public abstract NewsDtoRequest entityToRequest(NewsEntity entity);
}
