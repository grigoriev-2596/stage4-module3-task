package com.mjc.school.controller;

import io.restassured.RestAssured;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private Flyway flyway;

    @AfterAll
    public static void clean(@Autowired Flyway flyway) {
        flyway.clean();
    }

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:" + port;
        flyway.clean();
        flyway.migrate();
    }
}
