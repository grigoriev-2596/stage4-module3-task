package com.mjc.school.repository;

import com.mjc.school.repository.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BaseRepository<T extends BaseEntity<K>, K, P> {

    Page<T> getAll(Pageable pageable, P searchParam);

    Optional<T> getById(K id);

    T create(T entity);

    T update(T entity);

    boolean deleteById(K id);

    boolean existById(K id);
}
