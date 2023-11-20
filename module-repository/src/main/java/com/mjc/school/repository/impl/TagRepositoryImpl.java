package com.mjc.school.repository.impl;

import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.entity.TagEntity;
import com.mjc.school.repository.query.TagRepositorySearchParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class TagRepositoryImpl extends AbstractRepository<TagEntity, Long, TagRepositorySearchParams> implements TagRepository {

    @Override
    protected void setFields(TagEntity toUpdate, TagEntity updateBy) {
        toUpdate.setName(updateBy.getName());
    }

    @Override
    public Page<TagEntity> getAll(Pageable pageable, TagRepositorySearchParams params) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TagEntity> query = builder.createQuery(TagEntity.class);
        Root<TagEntity> root = query.from(TagEntity.class);

        if (params.name() != null) {
            query.where(builder.like(builder.lower(root.get("name")), "%" + params.name().toLowerCase() + "%"));
        }
        query.distinct(true);

        return getFilteredEntity(builder, query, root, pageable);
    }

    @Override
    public boolean existByName(String name) {
        return (Boolean) entityManager
                .createQuery("SELECT CASE WHEN count(t)>0 THEN true ELSE false END FROM TagEntity t WHERE lower(name)=:name")
                .setParameter("name", name.toLowerCase()).getSingleResult();
    }

    @Override
    public Optional<TagEntity> getByName(String name) {
        Query findByName = entityManager
                .createQuery("SELECT t FROM TagEntity t where lower(name)=:name", TagEntity.class)
                .setParameter("name", name.toLowerCase());
        try {
            return Optional.of((TagEntity) findByName.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<TagEntity> getByNewsId(Long id) {
        return entityManager
                .createQuery("SELECT t FROM NewsEntity n INNER JOIN n.tags t where n.id=:id", TagEntity.class)
                .setParameter("id", id)
                .getResultList();
    }
}
