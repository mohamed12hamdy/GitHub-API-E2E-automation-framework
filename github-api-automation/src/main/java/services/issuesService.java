package services;

import config.EnvironmentVariables;
import endpoints.IssuesEndpoints;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Comment;
import models.Issue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class issuesService {

    public Response createIssue(String username, String repo, Issue issue) {
        return given()
                .header("Authorization", "Bearer " + EnvironmentVariables.GITHUB_TOKEN)
                .contentType(ContentType.JSON)
                .pathParam("username", username)
                .pathParam("repo", repo)
                .body(issue)
                .when()
                .post(IssuesEndpoints.CREATE_ISSUE);
    }

    public Response getIssue(String username, String repo, int issueNumber) {
        return given()
                .header("Authorization", "Bearer " + EnvironmentVariables.GITHUB_TOKEN)
                .pathParam("username", username)
                .pathParam("repo", repo)
                .pathParam("issueNumber", issueNumber)
                .when()
                .get(IssuesEndpoints.GET_ISSUE);
    }

    public Response closeIssue(String username, String repo, int issueNumber) {
        return given()
                .header("Authorization", "Bearer " + EnvironmentVariables.GITHUB_TOKEN)
                .contentType(ContentType.JSON)
                .pathParam("username", username)
                .pathParam("repo", repo)
                .pathParam("issueNumber", issueNumber)
                .body("{\"state\": \"closed\"}")
                .when()
                .patch(IssuesEndpoints.CLOSE_ISSUE);
    }

    public Response addCommentToIssue(String username, String repo, int issueNumber, Comment comment) {
        return given()
                .header("Authorization", "Bearer " + EnvironmentVariables.GITHUB_TOKEN)
                .contentType(ContentType.JSON)
                .pathParam("username", username)
                .pathParam("repo", repo)
                .pathParam("issueNumber", issueNumber)
                .body(comment)
                .when()
                .post(IssuesEndpoints.ADD_COMMENT_TO_ISSUE);
    }

    public Response updateIssueTitle(String username, String repo, int issueNumber, String newTitle) {
        return given()
                .header("Authorization", "Bearer " + EnvironmentVariables.GITHUB_TOKEN)
                .contentType(ContentType.JSON)
                .pathParam("username", username)
                .pathParam("repo", repo)
                .pathParam("issueNumber", issueNumber)
                .body("{\"title\": \"" + newTitle + "\"}")
                .when()
                .patch(IssuesEndpoints.UPDATE_ISSUE);
    }

    public Response createIssueWithoutTitle(String username, String repo) {

        Map<String, Object> issue = new HashMap<>();
        issue.put("body", "Steps to reproduce: ...");
        issue.put("labels", List.of("bug"));

        return given()
                .header("Authorization", "Bearer " + EnvironmentVariables.GITHUB_TOKEN)
                .contentType(ContentType.JSON)
                .pathParam("username", username)
                .pathParam("repo", repo)
                .body(issue)
                .when()
                .post(IssuesEndpoints.CREATE_ISSUE);
    }
}
