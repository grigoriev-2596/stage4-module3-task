package com.mjc.school.controller;

import com.mjc.school.service.dto.NewsDtoRequest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class NewsControllerTest extends BaseControllerTest {

    @Test
    public void successfulCreateTest() {
        final String title = "Atomic Heart";
        final String content = "Atomic Heart is 2023's most popular Xbox launch so far";
        final NewsDtoRequest request = new NewsDtoRequest(title, content, "Ivan Testov", List.of("games"));
        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/v1/news")
                .then().log().all()
                .statusCode(201)
                .body("id", equalTo(3))
                .body("title", equalTo(title))
                .body("content", equalTo(content))
                .body("creationDate", notNullValue())
                .body("lastUpdateDate", notNullValue());
    }

    @Test
    public void unsuccessfulCreateNotValidNewsTest() {
        final NewsDtoRequest invalidRequest = new NewsDtoRequest("it", "ic", "ia", new ArrayList<>());
        given()
                .contentType("application/json")
                .body(invalidRequest)
                .when()
                .post("/api/v1/news")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    public void successfulGetByIdTest() {
        final int id = 1;
        final String expectedTitle = "Weather in Minsk";
        final String expectedContent = "It's very sunny in Minsk today";
        given()
                .contentType("application/json")
                .when()
                .get("/api/v1/news/" + id)
                .then().log().all()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("title", equalTo(expectedTitle))
                .body("content", equalTo(expectedContent))
                .body("creationDate", notNullValue())
                .body("lastUpdateDate", notNullValue());
    }

    @Test
    public void unsuccessfulGetNonExistingNewsByIdTest() {
        final int id = 3;
        given()
                .contentType("application/json")
                .when()
                .get("/api/v1/news/" + id)
                .then().log().all()
                .statusCode(404);
    }

    @Test
    public void successfulGetAllTest() {
        given()
                .contentType("application/json")
                .when()
                .get("/api/v1/news")
                .then().log().all()
                .statusCode(200)
                .body("_embedded.newsDtoResponseList.id", hasItems(1, 2))
                .body("_embedded.newsDtoResponseList.title", hasItems("Weather in Minsk", "CS:GO 2"));
    }

    @Test
    public void successfulUpdateTest() {
        final long id = 1;
        final String updatedContent = "Updated comment content";
        given()
                .contentType("application/json-patch+json")
                .body("[{\"op\":\"replace\", \"path\" : \"/content\", \"value\" : \"" + updatedContent + "\"}]")
                .when()
                .patch("/api/v1/news/" + id)
                .then().log().all()
                .statusCode(200)
                .body("content", equalTo(updatedContent));
    }

    @Test
    public void unsuccessfulUpdateWithNonValidValueTest() {
        final long id = 1;
        final String invalidTitle = "inti";
        given()
                .contentType("application/json-patch+json")
                .body("[{\"op\":\"replace\", \"path\" : \"/title\", \"value\" : \"" + invalidTitle + "\"}]")
                .when()
                .patch("/api/v1/news/" + id)
                .then().log().all()
                .statusCode(400);
    }

    @Test
    public void unsuccessfulUpdateNonExistingNewsTest() {
        final long id = 3;
        given()
                .contentType("application/json-patch+json")
                .body("[{\"op\":\"replace\", \"path\" : \"/title\", \"value\" : \"new title\"}]")
                .when()
                .patch("/api/v1/news/" + id)
                .then().log().all()
                .statusCode(404);
    }
}

