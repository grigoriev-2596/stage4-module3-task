package com.mjc.school.service;

import com.github.fge.jsonpatch.JsonPatch;
import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;
import com.mjc.school.service.dto.AuthorWithNewsResponse;
import com.mjc.school.service.query.AuthorServiceSearchParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorService extends BaseService<AuthorDtoRequest, AuthorDtoResponse, Long, JsonPatch, AuthorServiceSearchParams> {

    AuthorDtoResponse getByNewsId(Long id);

    Page<AuthorWithNewsResponse> getWithNewsAmount(Pageable pageable);
}
