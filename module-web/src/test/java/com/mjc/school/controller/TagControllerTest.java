package com.mjc.school.controller;

import com.mjc.school.service.dto.TagDtoRequest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

public class TagControllerTest extends BaseControllerTest {

    @Test
    public void successfulCreateTest() {
        final String name = "science";
        final TagDtoRequest request = new TagDtoRequest(name);
        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/v1/tags")
                .then().log().all()
                .statusCode(201)
                .body("id", equalTo(3))
                .body("name", equalTo(name));
    }

    @Test
    public void unsuccessfulCreateNotValidTagTest() {
        final TagDtoRequest invalidRequest = new TagDtoRequest("Invalid tag name value");
        given()
                .contentType("application/json")
                .body(invalidRequest)
                .when()
                .post("/api/v1/tags")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    public void successfulGetByIdTest() {
        final int id = 1;
        final String expectedName = "weather";
        given()
                .contentType("application/json")
                .when()
                .get("/api/v1/tags/" + id)
                .then().log().all()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo(expectedName));
    }

    @Test
    public void unsuccessfulGetNonExistingTagByIdTest() {
        final int id = 3;
        given()
                .contentType("application/json")
                .when()
                .get("/api/v1/tags/" + id)
                .then().log().all()
                .statusCode(404);
    }

    @Test
    public void successfulGetAllTest() {
        given()
                .contentType("application/json")
                .when()
                .get("/api/v1/tags")
                .then().log().all()
                .statusCode(200)
                .body("_embedded.tagDtoResponseList.id", hasItems(1, 2))
                .body("_embedded.tagDtoResponseList.name", hasItems("weather", "games"));
    }

    @Test
    public void successfulUpdateTest() {
        final long id = 1;
        final String updatedName = "Updated name";
        given()
                .contentType("application/json-patch+json")
                .body("[{\"op\":\"replace\", \"path\" : \"/name\", \"value\" : \"" + updatedName + "\"}]")
                .when()
                .patch("/api/v1/tags/" + id)
                .then().log().all()
                .statusCode(200)
                .body("name", equalTo(updatedName));
    }

    @Test
    public void unsuccessfulUpdateWithNonValidValueTest() {
        final long id = 1;
        final String invalidName = "iv";
        given()
                .contentType("application/json-patch+json")
                .body("[{\"op\":\"replace\", \"path\" : \"/name\", \"value\" : \"" + invalidName + "\"}]")
                .when()
                .patch("/api/v1/tags/" + id)
                .then().log().all()
                .statusCode(400);
    }

    @Test
    public void unsuccessfulUpdateNonExistingTagTest() {
        final long id = 3;
        given()
                .contentType("application/json-patch+json")
                .body("[{\"op\":\"replace\", \"path\" : \"/name\", \"value\" : \"new name\"}]")
                .when()
                .patch("/api/v1/tags/" + id)
                .then().log().all()
                .statusCode(404);
    }
}

