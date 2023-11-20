package com.mjc.school.controller.impl;

import com.github.fge.jsonpatch.JsonPatch;
import com.mjc.school.controller.BaseController;
import com.mjc.school.hateoas.LinkHelper;
import com.mjc.school.service.AuthorService;
import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;
import com.mjc.school.service.dto.AuthorWithNewsResponse;
import com.mjc.school.service.query.AuthorServiceSearchParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.SortDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.mjc.school.constant.ApiConstant.AUTHORS_BASE_URI;

@RestController
@RequestMapping(value = "/api", produces = MediaTypes.HAL_JSON_VALUE)
public class AuthorRestController implements BaseController<AuthorDtoRequest, AuthorDtoResponse, Long, JsonPatch, AuthorServiceSearchParams> {

    private final AuthorService authorService;

    private final PagedResourcesAssembler<AuthorDtoResponse> pageAssembler;
    private final PagedResourcesAssembler<AuthorWithNewsResponse> authorWithNewsPageAssembler;

    @Autowired
    public AuthorRestController(AuthorService authorService,
                                PagedResourcesAssembler<AuthorDtoResponse> pageAssembler,
                                PagedResourcesAssembler<AuthorWithNewsResponse> authorWithNewsPageAssembler) {
        this.authorService = authorService;
        this.pageAssembler = pageAssembler;
        this.authorWithNewsPageAssembler = authorWithNewsPageAssembler;
    }

    @ApiOperation(value = "Get all authors")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully received all authors"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/v1" + AUTHORS_BASE_URI)
    @Override
    public PagedModel<EntityModel<AuthorDtoResponse>> getAll(
            @PageableDefault(size = 5)
            @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable,
            AuthorServiceSearchParams param) {

        PagedModel<EntityModel<AuthorDtoResponse>> modelPage = pageAssembler.toModel(authorService.getAll(pageable, param));
        modelPage.forEach(LinkHelper::addLinksToAuthor);
        return modelPage;
    }

    @ApiOperation(value = "Get author by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully received the author by its id"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/v1" + AUTHORS_BASE_URI + "/{id:\\d+}")
    @Override
    public EntityModel<AuthorDtoResponse> getById(@PathVariable Long id) {

        EntityModel<AuthorDtoResponse> model = EntityModel.of(authorService.getById(id));
        LinkHelper.addLinksToAuthor(model);
        return model;
    }

    @ApiOperation(value = "Create an author")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The author was created successfully"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/v1" + AUTHORS_BASE_URI, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public EntityModel<AuthorDtoResponse> create(@RequestBody AuthorDtoRequest createRequest) {

        EntityModel<AuthorDtoResponse> model = EntityModel.of(authorService.create(createRequest));
        LinkHelper.addLinksToAuthor(model);
        return model;
    }

    @ApiOperation(value = "Update an author")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The author was updated successfully"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/v1" + AUTHORS_BASE_URI + "/{id:\\d+}", consumes = "application/json-patch+json")
    @Override
    public EntityModel<AuthorDtoResponse> update(@PathVariable Long id, @RequestBody JsonPatch patch) {

        EntityModel<AuthorDtoResponse> model = EntityModel.of(authorService.update(id, patch));
        LinkHelper.addLinksToAuthor(model);
        return model;
    }

    @ApiOperation(value = "Delete author")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The author was deleted successfully"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/v1" + AUTHORS_BASE_URI + "/{id:\\d+}")
    @Override
    public void deleteById(@PathVariable Long id) {
        authorService.deleteById(id);
    }

    @ApiOperation(value = "Get authors with the amount of news")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved authors with the amount of news"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/v1" + AUTHORS_BASE_URI + "/with-news-amount")
    public PagedModel<EntityModel<AuthorWithNewsResponse>> getAuthorsWithNewsAmount(
            @PageableDefault(size = 5)
            Pageable pageable) {

        PagedModel<EntityModel<AuthorWithNewsResponse>> modelPage = authorWithNewsPageAssembler.toModel(authorService.getWithNewsAmount(pageable));
        modelPage.forEach(LinkHelper::addLinksToAuthorWithNewsAmount);
        return modelPage;
    }
}
