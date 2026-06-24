package tests;

import datareader.JsonReader;
import io.qameta.allure.*;
import models.File;
import models.MergePullRequest;
import models.PullRequest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import services.BranchesAndPullRequestService;

import static org.hamcrest.CoreMatchers.equalTo;

@Epic("GitHub API")
@Feature("Branches and Pull Requests")
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

    @Story("Branches and Pull Request E2E Flow")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void testBranchesAndPullRequestE2ETests() {

        createNewBranch();

        createFileOnNewBranch();

        int pullNumber = createPullRequest();

       getPullRequest(pullNumber);

       mergePullRequest(pullNumber);

    }

    @Step("Create new branch")
    private void createNewBranch() {
        branchesAndPullRequestService
                .createNewBranch(username, repository.getName(), ref, sha)
                .then()
                .statusCode(201)
                .body("ref", equalTo("refs/heads/feature-branch"))
                .body("object.sha", equalTo(sha));
    }

    @Step("Create file on new branch")
    private void createFileOnNewBranch() {
        branchesAndPullRequestService.createFileOnNewBranch(username, repository.getName(), filepath,
                        JsonReader.getJson("File", File.class))
                .then()
                .statusCode(201);
    }

    @Step("Create pull request")
    private Integer createPullRequest() {
       return  branchesAndPullRequestService.createPullRequest(
                        username,
                        repository.getName(),
                        JsonReader.getJson("PullRequest", PullRequest.class))
                .then()
                .statusCode(201)
                .extract()
                .path("number");
    }

    @Step("Get pull request and verify state is open")
    private void getPullRequest(Integer pullNumber) {
        branchesAndPullRequestService.getPullRequest(username, repository.getName(), pullNumber)
                .then()
                .statusCode(200)
                .body("state", equalTo("open"));
    }

    @Step("Merge pull request")
    private void mergePullRequest(Integer pullNumber) {
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

