package com.mjc.school.repository.impl;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.configuration.RepositoryConfiguration;
import com.mjc.school.repository.entity.TagEntity;
import com.mjc.school.repository.query.TagRepositorySearchParams;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {RepositoryConfiguration.class, TagRepositoryImpl.class, NewsRepositoryImpl.class})
public class TagRepositoryTest {

    TagRepository tagRepository;
    NewsRepository newsRepository;

    @Autowired
    public TagRepositoryTest(TagRepository tagRepository, NewsRepository newsRepository) {
        this.tagRepository = tagRepository;
        this.newsRepository = newsRepository;
    }

    @AfterAll
    public static void clean(@Autowired Flyway flyway) {
        flyway.clean();
    }

    @Test
    public void createTest() {
        final String expectedName = "new tag name";
        TagEntity toCreate = TagEntity.builder().name(expectedName).build();
        TagEntity actual = tagRepository.create(toCreate);

        assertEquals(actual.getName(), expectedName);
        assertEquals(3L, actual.getId());
    }

    @Test
    public void getAllTest() {
        final String firstExpectedName = "weather";
        final String secondExpectedName = "games";

        Pageable pageable = PageRequest.of(0, 5);
        TagRepositorySearchParams emptyParams = new TagRepositorySearchParams(null);

        List<TagEntity> actual = tagRepository.getAll(pageable, emptyParams).getContent();

        assertEquals(2, actual.size());
        assertThat(actual)
                .extracting("name")
                .contains(firstExpectedName, secondExpectedName);
    }

    @Test
    public void getByIdTest() {
        final long id = 1;

        TagEntity actual = tagRepository.getById(id).get();
        assertEquals(id, actual.getId());
    }

    @Test
    public void deleteByIdTest() {
        final long id = 1;
        assertTrue(tagRepository.deleteById(id));
        ;
    }

    @Test
    public void updateTest() {
        final long id = 1;
        final String updatedName = "new tag name";
        TagEntity toUpdate = tagRepository.getById(id).get();

        toUpdate.setName(updatedName);
        TagEntity actual = tagRepository.update(toUpdate);

        assertEquals(updatedName, actual.getName());
    }

    @Test
    public void getByNewsIdTest() {
        final long newsId = 2;
        final String expectedName = "games";

        List<TagEntity> actual = tagRepository.getByNewsId(newsId);

        assertEquals(1, actual.size());
        assertEquals(expectedName, actual.get(0).getName());
    }

}
