package com.mjc.school.repository.impl;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.configuration.RepositoryConfiguration;
import com.mjc.school.repository.entity.AuthorEntity;
import com.mjc.school.repository.query.AuthorRepositorySearchParams;
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
@ContextConfiguration(classes = {RepositoryConfiguration.class, AuthorRepositoryImpl.class})
public class AuthorRepositoryTest {

    AuthorRepository authorRepository;

    @Autowired
    public AuthorRepositoryTest(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @AfterAll
    public static void clean(@Autowired Flyway flyway) {
        flyway.clean();
    }

    @Test
    public void createTest() {
        final String expectedName = "Test author";
        AuthorEntity toCreate = AuthorEntity.builder().name(expectedName).build();
        AuthorEntity actual = authorRepository.create(toCreate);

        assertEquals(actual.getName(), expectedName);
        assertEquals(3L, actual.getId());
        assertNotNull(actual.getCreationDate());
        assertNotNull(actual.getLastUpdateDate());
    }

    @Test
    public void getAllTest() {
        final String firstExpectedName = "Ivan Testov";
        final String secondExpectedName = "Petya Fomin";

        Pageable pageable = PageRequest.of(0, 5);
        AuthorRepositorySearchParams emptyParams = new AuthorRepositorySearchParams(null);

        List<AuthorEntity> actual = authorRepository.getAll(pageable, emptyParams).getContent();

        assertEquals(2, actual.size());
        assertThat(actual)
                .extracting("name")
                .contains(firstExpectedName, secondExpectedName);
    }

    @Test
    public void getByIdTest() {
        final long id = 2;

        AuthorEntity actual = authorRepository.getById(id).get();
        assertEquals(id, actual.getId());
    }

    @Test
    public void deleteByIdTest() {
        final long id = 1;
        assertTrue(authorRepository.deleteById(id));
        ;
    }

    @Test
    public void updateTest() {
        final long id = 1;
        final String updatedName = "updated name";
        AuthorEntity toUpdate = authorRepository.getById(id).get();

        toUpdate.setName(updatedName);
        AuthorEntity actual = authorRepository.update(toUpdate);

        assertEquals(updatedName, actual.getName());
        assertNotEquals(actual.getCreationDate(), actual.getLastUpdateDate());
    }

    @Test
    public void getByNewsIdTest() {
        final long newsId = 1;
        final String expectedName = "Ivan Testov";

        AuthorEntity actual = authorRepository.getByNewsId(newsId).get();

        assertEquals(expectedName, actual.getName());
    }


}
