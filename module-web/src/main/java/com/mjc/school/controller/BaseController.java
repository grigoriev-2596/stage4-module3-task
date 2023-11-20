package com.mjc.school.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

public interface BaseController<T, R, K, U, P> {

    PagedModel<EntityModel<R>> getAll(Pageable pageable, P searchParameters);

    EntityModel<R> getById(K id);

    EntityModel<R> create(T createRequest);

    EntityModel<R> update(K id, U updateRequest);

    void deleteById(K id);
}
