package com.mjc.school.service;

import com.github.fge.jsonpatch.JsonPatch;
import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.query.TagServiceSearchParams;

import java.util.List;

public interface TagService extends BaseService<TagDtoRequest, TagDtoResponse, Long, JsonPatch, TagServiceSearchParams> {

    List<TagDtoResponse> getByNewsId(Long id);
}
