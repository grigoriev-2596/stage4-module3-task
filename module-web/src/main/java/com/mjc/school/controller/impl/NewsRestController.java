package com.mjc.school.controller.impl;

import com.github.fge.jsonpatch.JsonPatch;
import com.mjc.school.controller.BaseController;
import com.mjc.school.hateoas.LinkHelper;
import com.mjc.school.service.AuthorService;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.TagService;
import com.mjc.school.service.dto.*;
import com.mjc.school.service.query.AuthorServiceSearchParams;
import com.mjc.school.service.query.NewsServiceSearchParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.SortDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

import static com.mjc.school.constant.ApiConstant.*;

@RestController
@RequestMapping(value = "/api", produces = MediaTypes.HAL_JSON_VALUE)
public class NewsRestController implements BaseController<NewsDtoRequest, NewsDtoResponse, Long, JsonPatch, NewsServiceSearchParams> {

    private final BaseService<NewsDtoRequest, NewsDtoResponse, Long, JsonPatch, NewsServiceSearchParams> newsService;

    private final TagService tagService;
    private final AuthorService authorService;
    private final CommentService commentService;

    private final PagedResourcesAssembler<NewsDtoResponse> pageAssembler;

    @Autowired
    public NewsRestController(BaseService<NewsDtoRequest, NewsDtoResponse, Long, JsonPatch, NewsServiceSearchParams> newsService,
                              TagService tagService, AuthorService authorService, CommentService commentService,
                              PagedResourcesAssembler<NewsDtoResponse> pageAssembler) {
        this.newsService = newsService;
        this.tagService = tagService;
        this.authorService = authorService;
        this.commentService = commentService;
        this.pageAssembler = pageAssembler;
    }

    @ApiOperation(value = "Get all news")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully received all news"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/v1" + NEWS_BASE_URI)
    @Override
    public PagedModel<EntityModel<NewsDtoResponse>> getAll(
            @PageableDefault(size = 5)
            @SortDefault(sort = "creationDate", direction = Sort.Direction.DESC)
            Pageable pageable,
            NewsServiceSearchParams params) {

        PagedModel<EntityModel<NewsDtoResponse>> modelPage = pageAssembler.toModel(newsService.getAll(pageable, params));
        modelPage.forEach(LinkHelper::addLinksToNews);
        return modelPage;
    }

    @ApiOperation(value = "Get news by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully received a news by its id"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/v1" + NEWS_BASE_URI + "/{id:\\d+}")
    @Override
    public EntityModel<NewsDtoResponse> getById(@PathVariable Long id) {

        EntityModel<NewsDtoResponse> model = EntityModel.of(newsService.getById(id));
        LinkHelper.addLinksToNews(model);
        return model;
    }

    @ApiOperation(value = "Create a news")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "News was created successfully"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping(value = "/v1" + NEWS_BASE_URI, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public EntityModel<NewsDtoResponse> create(@RequestBody NewsDtoRequest createRequest) {

        EntityModel<NewsDtoResponse> model = EntityModel.of(newsService.create(createRequest));
        LinkHelper.addLinksToNews(model);
        return model;
    }

    @ApiOperation(value = "Update a news")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "News was updated successfully"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/v1" + NEWS_BASE_URI + "/{id:\\d+}", consumes = "application/json-patch+json")
    @Override
    public EntityModel<NewsDtoResponse> update(@PathVariable Long id, @RequestBody JsonPatch patch) {

        EntityModel<NewsDtoResponse> model = EntityModel.of(newsService.update(id, patch));
        LinkHelper.addLinksToNews(model);
        return model;
    }

    @ApiOperation(value = "Delete news")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "News was deleted successfully"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/v1" + NEWS_BASE_URI + "/{id:\\d+}")
    @Override
    public void deleteById(@PathVariable Long id) {
        newsService.deleteById(id);
    }


    @ApiOperation(value = "Get tags by news id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully received tags by news id"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/v1" + NEWS_BASE_URI + "/{id:\\d+}" + TAGS_BASE_URI)
    public CollectionModel<EntityModel<TagDtoResponse>> getTagsByNewsId(@PathVariable Long id) {

        List<EntityModel<TagDtoResponse>> tagModels = tagService.getByNewsId(id).stream().map(EntityModel::of).toList();
        CollectionModel<EntityModel<TagDtoResponse>> modelCollection = CollectionModel.of(tagModels);
        modelCollection.forEach(LinkHelper::addLinksToTag);
        return modelCollection;
    }

    @ApiOperation(value = "Get comments by news id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully received comments by news id"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/v1" + NEWS_BASE_URI + "/{id:\\d+}" + COMMENTS_BASE_URI)
    public CollectionModel<EntityModel<CommentDtoResponse>> getCommentsByNewsId(@PathVariable Long id) {

        List<EntityModel<CommentDtoResponse>> commentModels = commentService.getByNewsId(id).stream().map(EntityModel::of).toList();
        CollectionModel<EntityModel<CommentDtoResponse>> modelCollection = CollectionModel.of(commentModels);
        modelCollection.forEach(LinkHelper::addLinksToComment);
        return modelCollection;
    }

    @ApiOperation(value = "Get author by news id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully received the author by news id"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/v1" + NEWS_BASE_URI + "/{id:\\d+}" + AUTHORS_BASE_URI)
    public EntityModel<AuthorDtoResponse> getAuthorByNewsId(@PathVariable Long id) {

        EntityModel<AuthorDtoResponse> model = EntityModel.of(authorService.getByNewsId(id));
        LinkHelper.addLinksToAuthor(model);
        return model;
    }
}
