package tests;

import datareader.JsonReader;
import io.qameta.allure.*;
import models.Comment;
import models.Issue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import services.issuesService;

import static org.hamcrest.Matchers.equalTo;

@Epic("GitHub API")
@Feature("Issues")
public class IssuesApiE2ETests extends RepositoryApiBase {

   private issuesService IssuesService;
   private Issue issue;

   @BeforeClass(dependsOnMethods = "setUp")
   public void init() {
      repositoryService.createRepo(repository);

      IssuesService = new issuesService();
      JsonReader issueData = new JsonReader("Issue");
      issue = new Issue(
              issueData.getJsonData("title"),
              issueData.getJsonData("body"),
              issueData.getJsonList("labels")
      );
   }

    @Story("Issue E2E Flow")
    @Severity(SeverityLevel.CRITICAL)
    @Test
   public void testIssueFullE2ETests() {

      int issueNumber = createIssue(username,repository.getName(),issue);

      getIssue(issueNumber);

      addComment(issueNumber);

      updateIssueTitle(issueNumber);

      closeIssue(issueNumber);
   }


    @Step("Create issue")
    private int createIssue(String username, String repo, Issue issue) {
        return IssuesService.createIssue(username, repo, issue)
                .then()
                .statusCode(201)
                .body("state", equalTo("open"))
                .extract()
                .path("number");
    }


    @Step("Get issue")
    private void getIssue(int issueNumber) {
        IssuesService.getIssue(username, repository.getName(), issueNumber)
                .then()
                .statusCode(200);
    }

    @Step("Add comment to issue")
    private void addComment(int issueNumber) {
        IssuesService.addCommentToIssue(username, repository.getName(), issueNumber,
                        JsonReader.getJson("Comment", Comment.class))
                .then()
                .statusCode(201);
    }

    @Step("Update issue title")
    private void updateIssueTitle(int issueNumber) {
        IssuesService.updateIssueTitle(username, repository.getName(), issueNumber,
                        "Updated Issue Title")
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated Issue Title"));
    }

    @Step("Close issue")
    private void closeIssue(int issueNumber) {
        IssuesService.closeIssue(username, repository.getName(), issueNumber)
                .then()
                .statusCode(200)
                .body("state", equalTo("closed"));
    }

   @AfterClass
    public void teardown() {
       repositoryService.deleteRepo(username, repository.getName());
   }
}
