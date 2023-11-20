package com.mjc.school.repository.impl;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.entity.AuthorEntity;
import com.mjc.school.repository.entity.NewsEntity;
import com.mjc.school.repository.entity.TagEntity;
import com.mjc.school.repository.query.NewsRepositorySearchParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

@Repository
public class NewsRepositoryImpl extends AbstractRepository<NewsEntity, Long, NewsRepositorySearchParams> implements NewsRepository {

    @Override
    protected void setFields(NewsEntity toUpdate, NewsEntity updateBy) {
        toUpdate.setTitle(updateBy.getTitle());
        toUpdate.setTitle(updateBy.getTitle());
        toUpdate.setContent(updateBy.getContent());
        toUpdate.setAuthor(updateBy.getAuthor());
        toUpdate.setTags(updateBy.getTags());
    }

    @Override
    public Page<NewsEntity> getAll(Pageable pageable, NewsRepositorySearchParams params) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<NewsEntity> query = builder.createQuery(NewsEntity.class);
        Root<NewsEntity> root = query.from(NewsEntity.class);

        if (params.tagIds() != null || params.tagNames() != null) {
            Join<NewsEntity, TagEntity> tagJoin = root.join("tags");
            if (params.tagIds() != null) {
                query.where(tagJoin.get("id").in(params.tagIds()));
            }
            if (params.tagNames() != null) {
                query.where(builder.lower(tagJoin.get("name")).in(params.tagNames().stream().map(String::toLowerCase).toList()));
            }
        }

        if (params.authorName() != null) {
            Join<NewsEntity, AuthorEntity> authorJoin = root.join("author");
            query.where(builder.like(builder.lower(authorJoin.get("name")), "%" + params.authorName().toLowerCase() + "%"));
        }
        if (params.title() != null) {
            query.where(builder.like(builder.lower(root.get("title")), "%" + params.title().toLowerCase() + "%"));
        }
        if (params.content() != null) {
            query.where(builder.like(builder.lower(root.get("content")), "%" + params.content().toLowerCase() + "%"));
        }

        return getFilteredEntity(builder, query, root, pageable);
    }

    @Override
    public boolean existByTitle(String title) {
        return (Boolean) entityManager
                .createQuery("SELECT CASE WHEN count(n)>0 THEN true ELSE false END FROM NewsEntity n WHERE lower(title)=:title")
                .setParameter("title", title.toLowerCase()).getSingleResult();
    }
}
