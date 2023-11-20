package com.mjc.school.repository.entity;

public interface BaseEntity<K> {

    K getId();

    void setId(K id);
}
