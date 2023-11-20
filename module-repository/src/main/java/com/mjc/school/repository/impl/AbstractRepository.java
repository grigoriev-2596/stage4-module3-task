package com.mjc.school.repository.impl;

import com.mjc.school.repository.BaseRepository;
import com.mjc.school.repository.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unchecked")
public abstract class AbstractRepository<T extends BaseEntity<K>, K, P> implements BaseRepository<T, K, P> {

    private final Class<T> entityClass;

    @PersistenceContext
    protected EntityManager entityManager;

    protected AbstractRepository() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    protected abstract void setFields(T toUpdate, T updateBy);

    public abstract Page<T> getAll(Pageable pageable, P params);

    @Override
    public Optional<T> getById(K id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    @Override
    public T create(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public T update(T entity) {
        T entityToUpdate = entityManager.find(entityClass, entity.getId());
        setFields(entityToUpdate, entity);
        entityManager.flush();
        return entityToUpdate;
    }

    @Override
    public boolean deleteById(K id) {
        return getById(id).map(entity -> {
            entityManager.remove(entity);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean existById(K id) {
        return getById(id).isPresent();
    }

    protected Page<T> getFilteredEntity(final CriteriaBuilder criteriaBuilder, final CriteriaQuery<T> criteriaQuery,
                                        final Root<T> root, Pageable pageable) {

        criteriaQuery.select(root);
        criteriaQuery.orderBy(QueryUtils.toOrders(pageable.getSort(), root, criteriaBuilder));
        criteriaQuery.distinct(true);

        List<T> pageEntities = entityManager.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = count(criteriaBuilder, criteriaQuery, root);

        return new PageImpl<>(pageEntities, pageable, total);
    }


    private long count(final CriteriaBuilder builder, final CriteriaQuery<T> selectQuery,
                       Root<T> root) {
        CriteriaQuery<Long> query = createCountQuery(builder, selectQuery, root);
        return this.entityManager.createQuery(query).getSingleResult();
    }

    private CriteriaQuery<Long> createCountQuery(final CriteriaBuilder criteriaBuilder,
                                                 final CriteriaQuery<T> criteriaQuery, final Root<T> root) {

        final CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        final Root<T> countRoot = countQuery.from(entityClass);

        doJoins(root.getJoins(), countRoot);
        doJoinsOnFetches(root.getFetches(), countRoot);

        countQuery.select(criteriaBuilder.count(countRoot));
        if (criteriaQuery.getRestriction() != null) {
            countQuery.where(criteriaQuery.getRestriction());
        }
        countRoot.alias(root.getAlias());

        return countQuery.distinct(true);
    }

    @SuppressWarnings("unchecked")
    private void doJoinsOnFetches(Set<? extends Fetch<?, ?>> joins, Root<?> root) {
        doJoins((Set<? extends Join<?, ?>>) joins, root);
    }

    private void doJoins(Set<? extends Join<?, ?>> joins, Root<?> root) {
        for (Join<?, ?> join : joins) {
            Join<?, ?> joined = root.join(join.getAttribute().getName(), join.getJoinType());
            joined.alias(join.getAlias());
            doJoins(join.getJoins(), joined);
        }
    }

    private void doJoins(Set<? extends Join<?, ?>> joins, Join<?, ?> root) {
        for (Join<?, ?> join : joins) {
            Join<?, ?> joined = root.join(join.getAttribute().getName(), join.getJoinType());
            joined.alias(join.getAlias());
            doJoins(join.getJoins(), joined);
        }
    }
}
