package com.mjc.school.repository.impl;

import com.mjc.school.repository.CommentRepository;
import com.mjc.school.repository.configuration.RepositoryConfiguration;
import com.mjc.school.repository.entity.CommentEntity;
import com.mjc.school.repository.entity.NewsEntity;
import com.mjc.school.repository.query.CommentRepositorySearchParams;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {RepositoryConfiguration.class, CommentRepositoryImpl.class, NewsRepositoryImpl.class})
public class CommentRepositoryTest {

    CommentRepository commentRepository;

    @Autowired
    public CommentRepositoryTest(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @AfterAll
    public static void clean(@Autowired Flyway flyway) {
        flyway.clean();
    }

    @Test
    public void createTest() {
        final String content = "new comment";
        final long id = 1;
        NewsEntity news = NewsEntity.builder().id(1L).build();
        CommentEntity newComment = CommentEntity.builder().news(news).content(content).build();
        CommentEntity actual = commentRepository.create(newComment);

        assertEquals(id, actual.getNews().getId());
        assertEquals(content, actual.getContent());
        assertNotNull(actual.getCreationDate());
        assertNotNull(actual.getLastUpdateDate());
    }

    @Test
    public void getAllTest() {
        final String firstContent = "It's actually warm today";
        final String secondContent = "we've been waiting there for a long time";

        Pageable pageable = PageRequest.of(0, 2);
        CommentRepositorySearchParams emptyParams = new CommentRepositorySearchParams(null);

        List<CommentEntity> actual = commentRepository.getAll(pageable, emptyParams).getContent();

        assertEquals(2, actual.size());
        assertThat(actual)
                .extracting("content")
                .contains(firstContent, secondContent);
    }

    @Test
    public void getByIdTest() {
        final long id = 1;
        CommentEntity actual = commentRepository.getById(id).get();

        assertEquals(id, actual.getId());
    }

    @Test
    public void updateTest() {
        final long id = 2;
        String newContent = "updated content";

        CommentEntity toUpdate = commentRepository.getById(id).get();
        toUpdate.setContent(newContent);
        CommentEntity actual = commentRepository.update(toUpdate);

        assertEquals(newContent, actual.getContent());
        assertNotEquals(actual.getCreationDate(), actual.getLastUpdateDate());
    }

    @Test
    public void deleteTest() {
        final long id = 2;
        assertTrue(commentRepository.deleteById(id));
    }

    @Test
    public void getByNewsIdTest() {
        final long newsId = 2;
        final String expectedContent = "we've been waiting there for a long time";

        List<CommentEntity> actual = commentRepository.getByNewsId(newsId);
        assertEquals(1, actual.size());
        assertEquals(expectedContent, actual.get(0).getContent());
    }

}
