package com.mjc.school.service;

import com.github.fge.jsonpatch.JsonPatch;
import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;
import com.mjc.school.service.query.CommentServiceSearchParams;

import java.util.List;

public interface CommentService extends BaseService<CommentDtoRequest, CommentDtoResponse, Long, JsonPatch, CommentServiceSearchParams> {

    List<CommentDtoResponse> getByNewsId(Long id);
}
