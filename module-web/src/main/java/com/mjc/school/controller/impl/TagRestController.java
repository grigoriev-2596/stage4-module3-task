package com.mjc.school.controller.impl;

import com.github.fge.jsonpatch.JsonPatch;
import com.mjc.school.controller.BaseController;
import com.mjc.school.hateoas.LinkHelper;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.query.TagServiceSearchParams;
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

import static com.mjc.school.constant.ApiConstant.TAGS_BASE_URI;

@RestController
@RequestMapping(value = "/api", produces = MediaTypes.HAL_JSON_VALUE)
public class TagRestController implements BaseController<TagDtoRequest, TagDtoResponse, Long, JsonPatch, TagServiceSearchParams> {

    private final BaseService<TagDtoRequest, TagDtoResponse, Long, JsonPatch, TagServiceSearchParams> tagService;

    private final PagedResourcesAssembler<TagDtoResponse> pageAssembler;

    @Autowired
    public TagRestController(BaseService<TagDtoRequest, TagDtoResponse, Long, JsonPatch, TagServiceSearchParams> tagService,
                             PagedResourcesAssembler<TagDtoResponse> pagedAssembler) {
        this.tagService = tagService;
        this.pageAssembler = pagedAssembler;
    }

    @ApiOperation(value = "Get all tags")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully received all tags"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/v1" + TAGS_BASE_URI)
    @Override
    public PagedModel<EntityModel<TagDtoResponse>> getAll(
            @PageableDefault(size = 2)
            @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable,
            TagServiceSearchParams params) {

        PagedModel<EntityModel<TagDtoResponse>> modelPage = pageAssembler.toModel(tagService.getAll(pageable, params));
        modelPage.forEach(LinkHelper::addLinksToTag);
        return modelPage;
    }

    @ApiOperation(value = "Get tag by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully received a tag by its id"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/v1" + TAGS_BASE_URI + "/{id:\\d+}")
    @Override
    public EntityModel<TagDtoResponse> getById(@PathVariable Long id) {

        EntityModel<TagDtoResponse> model = EntityModel.of(tagService.getById(id));
        LinkHelper.addLinksToTag(model);
        return model;
    }

    @ApiOperation(value = "Create a tag")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Tag was created successfully"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/v1" + TAGS_BASE_URI, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public EntityModel<TagDtoResponse> create(@RequestBody TagDtoRequest createRequest) {

        EntityModel<TagDtoResponse> model = EntityModel.of(tagService.create(createRequest));
        LinkHelper.addLinksToTag(model);
        return model;
    }

    @ApiOperation(value = "Update a tag")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tag was updated successfully"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/v1" + TAGS_BASE_URI + "/{id:\\d+}", consumes = "application/json-patch+json")
    @Override
    public EntityModel<TagDtoResponse> update(@PathVariable Long id, @RequestBody JsonPatch patch) {

        EntityModel<TagDtoResponse> model = EntityModel.of(tagService.update(id, patch));
        LinkHelper.addLinksToTag(model);
        return model;
    }

    @ApiOperation(value = "Delete tag")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Tag was deleted successfully"),
            @ApiResponse(code = 400, message = "Application cannot process the request due to a client error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/v1" + TAGS_BASE_URI + "/{id:\\d+}")
    @Override
    public void deleteById(@PathVariable Long id) {
        tagService.deleteById(id);
    }
}
