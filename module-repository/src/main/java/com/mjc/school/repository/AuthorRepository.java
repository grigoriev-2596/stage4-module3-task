package com.mjc.school.repository;

import com.mjc.school.repository.dto.AuthorWithNews;
import com.mjc.school.repository.entity.AuthorEntity;
import com.mjc.school.repository.query.AuthorRepositorySearchParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AuthorRepository extends BaseRepository<AuthorEntity, Long, AuthorRepositorySearchParams> {

    boolean existByName(String name);

    Optional<AuthorEntity> getByName(String name);

    Optional<AuthorEntity> getByNewsId(Long id);

    Page<AuthorWithNews> getWithNewsAmount(Pageable pageable);
}
