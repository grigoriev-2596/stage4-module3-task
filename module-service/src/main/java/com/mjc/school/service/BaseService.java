package com.mjc.school.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseService<T, R, K, U, P> {

    Page<R> getAll(Pageable pageable, P searchParam);

    R getById(K id);

    R create(T createRequest);

    R update(K id, U updateRequest);

    boolean deleteById(K id);
}
