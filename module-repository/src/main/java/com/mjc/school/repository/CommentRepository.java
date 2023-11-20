package com.mjc.school.repository;

import com.mjc.school.repository.entity.CommentEntity;
import com.mjc.school.repository.query.CommentRepositorySearchParams;

import java.util.List;

public interface CommentRepository extends BaseRepository<CommentEntity, Long, CommentRepositorySearchParams> {

    List<CommentEntity> getByNewsId(Long id);
}