package com.mjc.school.repository;

import com.mjc.school.repository.entity.TagEntity;
import com.mjc.school.repository.query.TagRepositorySearchParams;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends BaseRepository<TagEntity, Long, TagRepositorySearchParams> {

    boolean existByName(String name);

    Optional<TagEntity> getByName(String name);

    List<TagEntity> getByNewsId(Long id);
}
