package com.mjc.school.repository.impl;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.configuration.RepositoryConfiguration;
import com.mjc.school.repository.entity.NewsEntity;
import com.mjc.school.repository.query.NewsRepositorySearchParams;
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
@ContextConfiguration(classes = {RepositoryConfiguration.class, NewsRepositoryImpl.class})
public class NewsRepositoryTest {

    NewsRepository newsRepository;

    @Autowired
    public NewsRepositoryTest(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @AfterAll
    public static void clean(@Autowired Flyway flyway) {
        flyway.clean();
    }

    @Test
    public void createTest() {
        final String title = "new title";
        final String content = "new content";
        NewsEntity newNews = NewsEntity.builder().title(title).content(content).build();
        NewsEntity actual = newsRepository.create(newNews);

        assertEquals(3, actual.getId());
        assertEquals(title, actual.getTitle());
        assertEquals(content, actual.getContent());
        assertNotNull(actual.getCreationDate());
        assertNotNull(actual.getLastUpdateDate());
    }

    @Test
    public void getAllTest() {
        final String firstTitle = "Weather in Minsk";
        final String secondTitle = "CS:GO 2";

        Pageable pageable = PageRequest.of(0, 2);
        NewsRepositorySearchParams emptyParams = new NewsRepositorySearchParams(null, null, null, null, null);

        List<NewsEntity> actual = newsRepository.getAll(pageable, emptyParams).getContent();

        assertEquals(2, actual.size());
        assertThat(actual)
                .extracting("title")
                .contains(firstTitle, secondTitle);
    }

    @Test
    public void getByIdTest() {
        final long id = 1;
        NewsEntity actual = newsRepository.getById(id).get();

        assertEquals(id, actual.getId());
    }

    @Test
    public void updateTest() {
        final long id = 1;
        String newTitle = "updated title";

        NewsEntity toUpdate = newsRepository.getById(id).get();
        toUpdate.setTitle(newTitle);
        NewsEntity actual = newsRepository.update(toUpdate);

        assertEquals(newTitle, actual.getTitle());
        assertNotEquals(actual.getCreationDate(), actual.getLastUpdateDate());
    }

    @Test
    public void deleteTest() {
        final long id = 2;
        assertTrue(newsRepository.deleteById(id));
    }
}
