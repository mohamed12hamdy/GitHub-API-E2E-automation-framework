package services;

import config.EnvironmentVariables;
import endpoints.BranchesEndpoints;
import io.restassured.response.Response;
import models.File;
import models.MergePullRequest;
import models.PullRequest;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class BranchesAndPullRequestService {

    public Response getDefaultSHA(String username, String repo) {
        return given()
                .header("Authorization", "Bearer " + EnvironmentVariables.GITHUB_TOKEN)
                .pathParam("username", username)
                .pathParam("repo", repo)
                .when()
                .get(BranchesEndpoints.GET_DEFAULT_BRANCH);
    }

    public Response createNewBranch(String username, String repo, String ref, String sha) {

        Map<String, Object> body = new HashMap<>();
        body.put("ref", ref);
        body.put("sha", sha);

        return given()
                .header("Authorization", "Bearer " + EnvironmentVariables.GITHUB_TOKEN)
                .pathParam("username", username)
                .pathParam("repo", repo)
                .body(body)
                .when()
                .post(BranchesEndpoints.CREATE_BRANCH);
    }

    public Response createFileOnNewBranch(String username, String repo, String filepath, File file) {
        return given()
                .header("Authorization", "Bearer " + EnvironmentVariables.GITHUB_TOKEN)
                .pathParam("username", username)
                .pathParam("repo", repo)
                .pathParam("filepath", filepath)
                .body(file)
                .when()
                .put(BranchesEndpoints.CREATE_FILE_IN_BRANCH);
    }

    public Response createPullRequest(String username, String repo, PullRequest pullRequest) {
        return given()
                .header("Authorization", "Bearer " + EnvironmentVariables.GITHUB_TOKEN)
                .pathParam("username", username)
                .pathParam("repo", repo)
                .body(pullRequest)
                .when()
                .post(BranchesEndpoints.CREATE_PULL_REQUEST);
    }

    public Response getPullRequest(String username, String repo, Integer pullNumber) {
        return given()
                .header("Authorization", "Bearer " + EnvironmentVariables.GITHUB_TOKEN)
                .pathParam("username", username)
                .pathParam("repo", repo)
                .pathParam("pullNumber", pullNumber)
                .when()
                .get(BranchesEndpoints.GET_PULL_REQUEST);
    }

    public Response mergePullRequest(String username , String repo , Integer pullNumber, MergePullRequest mergePullRequest) {
        return given()
                .header("Authorization", "Bearer " + EnvironmentVariables.GITHUB_TOKEN)
                .pathParam("username", username)
                .pathParam("repo", repo)
                .pathParam("pullNumber",pullNumber)
                .body(mergePullRequest)
                .when()
                .put(BranchesEndpoints.MERGE_PULL_REQUEST);
    }

}

