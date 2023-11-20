package com.mjc.school.controller;

import com.mjc.school.service.dto.AuthorDtoRequest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthorControllerTest extends BaseControllerTest {

    @Test
    public void successfulCreateTest() {
        final String name = "Sergey Sokolov";
        final AuthorDtoRequest request = new AuthorDtoRequest(name);

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/v1/authors")
                .then().log().all()
                .statusCode(201)
                .body("id", equalTo(3))
                .body("name", equalTo(name))
                .body("creationDate", notNullValue())
                .body("lastUpdateDate", notNullValue());
    }

    @Test
    public void unsuccessfulCreateNotValidAuthorTest() {
        final AuthorDtoRequest invalidRequest = new AuthorDtoRequest("ab");

        given()
                .contentType("application/json")
                .body(invalidRequest)
                .when()
                .post("/api/v1/authors")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    public void successfulGetByIdTest() {
        final int id = 1;
        final String expectedName = "Ivan Testov";

        given()
                .contentType("application/json")
                .when()
                .get("/api/v1/authors/" + id)
                .then().log().all()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo(expectedName))
                .body("creationDate", notNullValue())
                .body("lastUpdateDate", notNullValue());

    }

    @Test
    public void unsuccessfulGetNonExistingAuthorByIdTest() {
        final int id = 3;
        given()
                .contentType("application/json")
                .when()
                .get("/api/v1/authors/" + id)
                .then().log().all()
                .statusCode(404);
    }

    @Test
    public void successfulGetAllTest() {
        given()
                .contentType("application/json")
                .when()
                .get("/api/v1/authors")
                .then().log().all()
                .statusCode(200)
                .body("_embedded.authorDtoResponseList.id", hasItems(1, 2))
                .body("_embedded.authorDtoResponseList.name", hasItems("Ivan Testov", "Petya Fomin"));
    }

    @Test
    public void successfulUpdateTest() {
        final long id = 1;
        final String updatedName = "Oleg Petrov";
        given()
                .contentType("application/json-patch+json")
                .body("[{\"op\":\"replace\", \"path\" : \"/name\", \"value\" : \"" + updatedName + "\"}]")
                .when()
                .patch("/api/v1/authors/" + id)
                .then().log().all()
                .statusCode(200)
                .body("name", equalTo(updatedName));
    }

    @Test
    public void unsuccessfulUpdateWithNonValidValueTest() {
        final long id = 1;
        final String invalidName = "Invalid author name";
        given()
                .contentType("application/json-patch+json")
                .body("[{\"op\":\"replace\", \"path\" : \"/name\", \"value\" : \"" + invalidName + "\"}]")
                .when()
                .patch("/api/v1/authors/" + id)
                .then().log().all()
                .statusCode(400);
    }

    @Test
    public void unsuccessfulUpdateNonExistingAuthorTest() {
        final long id = 3;
        given()
                .contentType("application/json-patch+json")
                .body("[{\"op\":\"replace\", \"path\" : \"/name\", \"value\" : \"RandomName\"}]")
                .when()
                .patch("/api/v1/authors/" + id)
                .then().log().all()
                .statusCode(404);
    }
}

