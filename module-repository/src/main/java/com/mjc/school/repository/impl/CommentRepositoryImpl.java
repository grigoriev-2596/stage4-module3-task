package com.mjc.school.repository.impl;

import com.mjc.school.repository.CommentRepository;
import com.mjc.school.repository.entity.CommentEntity;
import com.mjc.school.repository.query.CommentRepositorySearchParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class CommentRepositoryImpl extends AbstractRepository<CommentEntity, Long, CommentRepositorySearchParams> implements CommentRepository {

    @Override
    protected void setFields(CommentEntity toUpdate, CommentEntity updateBy) {
        toUpdate.setContent(updateBy.getContent());
    }

    @Override
    public Page<CommentEntity> getAll(Pageable pageable, CommentRepositorySearchParams params) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CommentEntity> criteriaQuery = builder.createQuery(CommentEntity.class);
        Root<CommentEntity> root = criteriaQuery.from(CommentEntity.class);

        if (params.content() != null) {
            criteriaQuery.where(builder.like(builder.lower(root.get("content")), "%" + params.content().toLowerCase() + "%"));
        }

        criteriaQuery.distinct(true);

        return getFilteredEntity(builder, criteriaQuery, root, pageable);
    }

    @Override
    public List<CommentEntity> getByNewsId(Long id) {
        return entityManager
                .createQuery("SELECT c FROM NewsEntity n INNER JOIN n.comments c where n.id=:id", CommentEntity.class)
                .setParameter("id", id)
                .getResultList();
    }
}
