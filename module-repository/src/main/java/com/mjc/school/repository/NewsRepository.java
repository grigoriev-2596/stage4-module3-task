package com.mjc.school.repository;

import com.mjc.school.repository.entity.NewsEntity;
import com.mjc.school.repository.query.NewsRepositorySearchParams;

public interface NewsRepository extends BaseRepository<NewsEntity, Long, NewsRepositorySearchParams> {

    boolean existByTitle(String title);
}