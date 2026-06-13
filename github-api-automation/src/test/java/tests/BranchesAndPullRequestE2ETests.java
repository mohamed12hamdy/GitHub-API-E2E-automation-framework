package tests;

import datareader.JsonReader;
import io.restassured.response.Response;
import models.File;
import models.MergePullRequest;
import models.PullRequest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import services.BranchesAndPullRequestService;

import static org.hamcrest.CoreMatchers.equalTo;

public class BranchesAndPullRequestE2ETests extends RepositoryApiBase {

    private BranchesAndPullRequestService branchesAndPullRequestService;

    private String sha;

    private final String ref = "refs/heads/feature-branch";

    private final String filepath = "test.md";

    @BeforeClass(dependsOnMethods = "setUp")
    public void setupBranchesAndPullRequest() {
        repositoryService.createRepo(repository);
        branchesAndPullRequestService = new BranchesAndPullRequestService();
        sha = branchesAndPullRequestService.getDefaultSHA(username, repository.getName()).jsonPath().
                getString("object.sha");
    }

    @Test
    public void testBranchesAndPullRequestE2ETests() {

     branchesAndPullRequestService
                .createNewBranch(username, repository.getName(), ref, sha)
                .then()
                .statusCode(201)
                .body("ref", equalTo("refs/heads/feature-branch"))
                .body("object.sha", equalTo(sha));

        branchesAndPullRequestService.createFileOnNewBranch(username, repository.getName(), filepath,
                        JsonReader.getJson("File", File.class))
                .then()
                .statusCode(201);

        Integer pullNumber = branchesAndPullRequestService.createPullRequest(
                        username,
                        repository.getName(),
                        JsonReader.getJson("PullRequest", PullRequest.class))
                .then()
                .statusCode(201)
                .extract()
                .path("number");



        branchesAndPullRequestService.getPullRequest(username, repository.getName(), pullNumber)
                .then()
                .statusCode(200)
                .body("state", equalTo("open"));

        branchesAndPullRequestService.mergePullRequest(username, repository.getName(), pullNumber,
                        JsonReader.getJson("MergePullRequest", MergePullRequest.class))
                .then()
                .statusCode(200)
                .body("merged", equalTo(true));

    }

    @AfterClass
    public void teardown() {
        repositoryService.deleteRepo(username, repository.getName());
    }

}

