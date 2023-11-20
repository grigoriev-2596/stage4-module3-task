package com.mjc.school.repository.impl;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.dto.AuthorWithNews;
import com.mjc.school.repository.entity.AuthorEntity;
import com.mjc.school.repository.query.AuthorRepositorySearchParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class AuthorRepositoryImpl extends AbstractRepository<AuthorEntity, Long, AuthorRepositorySearchParams> implements AuthorRepository {

    @Override
    protected void setFields(AuthorEntity toUpdate, AuthorEntity updateBy) {
        toUpdate.setName(updateBy.getName());
    }

    public Page<AuthorEntity> getAll(Pageable pageable, AuthorRepositorySearchParams params) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AuthorEntity> query = builder.createQuery(AuthorEntity.class);
        Root<AuthorEntity> root = query.from(AuthorEntity.class);

        if (params.name() != null) {
            query.where(builder.like(builder.lower(root.get("name")), "%" + params.name().toLowerCase() + "%"));
        }

        return getFilteredEntity(builder, query, root, pageable);
    }

    @Override
    public boolean existByName(String name) {
        return entityManager
                .createQuery("SELECT CASE WHEN count(a)>0 THEN true ELSE false END FROM AuthorEntity a WHERE lower(name)=:name", Boolean.class)
                .setParameter("name", name.toLowerCase()).getSingleResult();
    }

    @Override
    public Optional<AuthorEntity> getByName(String name) {
        TypedQuery<AuthorEntity> query = entityManager
                .createQuery("SELECT a FROM AuthorEntity a where lower(name)=:name", AuthorEntity.class)
                .setParameter("name", name.toLowerCase());
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Page<AuthorWithNews> getWithNewsAmount(Pageable pageable) {
        String sql = "SELECT a.id, a.name, COUNT(n.id) as numberOfNews " +
                "FROM AuthorEntity a " +
                "LEFT JOIN a.news n " +
                "GROUP BY a.id " +
                "ORDER BY numberOfNews DESC";
        List<Object[]> list = entityManager.createQuery(sql, Object[].class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        List<AuthorWithNews> content = list.stream().map(columns -> new AuthorWithNews((Long) columns[0], (String) columns[1], (Long) columns[2])).toList();
        long total = entityManager.createQuery("SELECT COUNT(*) FROM AuthorEntity", Long.class).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Optional<AuthorEntity> getByNewsId(Long id) {
        TypedQuery<AuthorEntity> query = entityManager
                .createQuery("SELECT a FROM NewsEntity n INNER JOIN n.author a where n.id=:id", AuthorEntity.class)
                .setParameter("id", id);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }

    }
}
