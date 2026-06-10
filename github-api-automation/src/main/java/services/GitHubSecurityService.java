package services;

import config.EnvironmentVariables;
import endpoints.AuthEndpoints;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class GitHubSecurityService {

    public Response getAuthenticatedUser() {
        return given()
                .header("Authorization", "Bearer " + EnvironmentVariables.GITHUB_TOKEN)
                .when()
                .get(AuthEndpoints.GET_AUTH_USER);

    }

    public Response getUnauthenticatedUser() {
        return given()
                .when()
                .get(AuthEndpoints.GET_AUTH_USER);
    }

    public Response checkRateLimit() {
        return given()
                .header("Authorization", "Bearer " + EnvironmentVariables.GITHUB_TOKEN)
                .when()
                .get(AuthEndpoints.Check_RATE_LIMIT);
    }
}