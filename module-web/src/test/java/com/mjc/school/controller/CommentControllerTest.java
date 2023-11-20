package com.mjc.school.controller;

import com.mjc.school.service.dto.CommentDtoRequest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CommentControllerTest extends BaseControllerTest {

    @Test
    public void successfulCreateTest() {
        final String content = "It's not so warm today";
        final CommentDtoRequest request = new CommentDtoRequest(content, 1L);
        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/v1/comments")
                .then().log().all()
                .statusCode(201)
                .body("id", equalTo(3))
                .body("content", equalTo(content))
                .body("creationDate", notNullValue())
                .body("lastUpdateDate", notNullValue());
    }

    @Test
    public void unsuccessfulCreateNotValidCommentTest() {
        final CommentDtoRequest invalidRequest = new CommentDtoRequest("ab", 1L);
        given()
                .contentType("application/json")
                .body(invalidRequest)
                .when()
                .post("/api/v1/comments")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    public void successfulGetByIdTest() {
        final int id = 1;
        final String expectedContent = "It's actually warm today";
        given()
                .contentType("application/json")
                .when()
                .get("/api/v1/comments/" + id)
                .then().log().all()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("content", equalTo(expectedContent))
                .body("creationDate", notNullValue())
                .body("lastUpdateDate", notNullValue());
    }

    @Test
    public void unsuccessfulGetNonExistingCommentByIdTest() {
        final int id = 3;
        given()
                .contentType("application/json")
                .when()
                .get("/api/v1/comments/" + id)
                .then().log().all()
                .statusCode(404);
    }

    @Test
    public void successfulGetAllTest() {
        given()
                .contentType("application/json")
                .when()
                .get("/api/v1/comments")
                .then().log().all()
                .statusCode(200)
                .body("_embedded.commentDtoResponseList.id", hasItems(1, 2))
                .body("_embedded.commentDtoResponseList.content", hasItems("It's actually warm today", "we've been waiting there for a long time"));
    }

    @Test
    public void successfulUpdateTest() {
        final long id = 1;
        final String updatedContent = "Updated comment content";
        given()
                .contentType("application/json-patch+json")
                .body("[{\"op\":\"replace\", \"path\" : \"/content\", \"value\" : \"" + updatedContent + "\"}]")
                .when()
                .patch("/api/v1/comments/" + id)
                .then().log().all()
                .statusCode(200)
                .body("content", equalTo(updatedContent));
    }

    @Test
    public void unsuccessfulUpdateWithNonValidValueTest() {
        final long id = 1;
        final String invalidContent = "ic";
        given()
                .contentType("application/json-patch+json")
                .body("[{\"op\":\"replace\", \"path\" : \"/content\", \"value\" : \"" + invalidContent + "\"}]")
                .when()
                .patch("/api/v1/comments/" + id)
                .then().log().all()
                .statusCode(400);
    }

    @Test
    public void unsuccessfulUpdateNonExistingCommentTest() {
        final long id = 3;
        given()
                .contentType("application/json-patch+json")
                .body("[{\"op\":\"replace\", \"path\" : \"/content\", \"value\" : \"content value\"}]")
                .when()
                .patch("/api/v1/comments/" + id)
                .then().log().all()
                .statusCode(404);
    }
}

