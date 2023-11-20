package com.mjc.school.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "authors")
@EntityListeners(AuditingEntityListener.class)
public class AuthorEntity implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "creation_date", updatable = false)
    @CreatedDate
    private LocalDateTime creationDate;

    @Column(name = "last_update_date")
    @LastModifiedDate
    private LocalDateTime lastUpdateDate;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "author", fetch = FetchType.LAZY)
    private List<NewsEntity> news = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
