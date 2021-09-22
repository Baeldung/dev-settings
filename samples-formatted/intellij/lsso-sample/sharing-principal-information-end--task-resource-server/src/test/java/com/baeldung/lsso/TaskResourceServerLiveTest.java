package com.baeldung.lsso;

import static org.hamcrest.Matchers.greaterThan;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;

/**
 * This Live Test requires:
 * - the Task Resource Server to be running
 */
public class TaskResourceServerLiveTest {

    private static final String TASK_RESOURCE_SERVER_BASE_URL = "http://localhost:8085/lsso-task-resource-server";

    private static final String PROJECT_RESOURCE_URL = TASK_RESOURCE_SERVER_BASE_URL + "/api/tasks";

    @Test
    public void givenRequestWithPreAuthHeaders_whenRequestProjectsEndpoint_thenOk() throws Exception {
        RestAssured.given().header("BAEL-username", "customUsername").header("BAEL-authorities", "SCOPE_read").get(PROJECT_RESOURCE_URL + "?projectId=1").then().statusCode(HttpStatus.OK.value()).and().body("size()", greaterThan(0));
    }

    @Test
    public void givenRequestWithoutQueryParam_whenRequestProjectsEndpoint_thenBadRequest() throws Exception {
        RestAssured.given().header("BAEL-username", "customUsername").header("BAEL-authorities", "SCOPE_read").get(PROJECT_RESOURCE_URL).then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void givenJustUsernameHeaders_whenRequestProjectsEndpoint_thenForbidden() throws Exception {
        RestAssured.given().header("BAEL-username", "customUsername").get(PROJECT_RESOURCE_URL).then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void givenJustUsernameHeaders_whenRequestNonExistingEndpoint_thenNotFound() throws Exception {
        RestAssured.given().header("BAEL-username", "customUsername").get(TASK_RESOURCE_SERVER_BASE_URL + "/other").then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void givenNoHeaders_whenRequestProjectsEndpoint_thenPreAuthCredentialsNotFoundException() throws Exception {
        RestAssured.given().get(PROJECT_RESOURCE_URL).then().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
