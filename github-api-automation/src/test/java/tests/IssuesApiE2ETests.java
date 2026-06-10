package tests;

import config.ConfigReader;
import datareader.JsonReader;

import io.restassured.response.Response;
import models.Comment;
import models.Issue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import services.issuesService;


import static org.hamcrest.CoreMatchers.equalTo;

public class IssuesApiE2ETests extends RepositoryApiE2ETests {

   private issuesService IssuesService;

   private Issue issue;

   private JsonReader issueData;

   private String repo;

   @BeforeClass
   public void init() {

      super.setUp();

      Response response = repositoryService.createRepo(repository);

      IssuesService = new issuesService();
      issueData = new JsonReader("Issue");
      issue = new Issue(issueData.getJsonData("title"),
                issueData.getJsonData("body"),
                issueData.getJsonList("labels"));

   }

   @Test
   public void testIssueFullE2ETests() {

      String username = ConfigReader.get("username");
      String repoName = repository.getName();

      Response response = IssuesService.createIssue(username, repoName, issue);

      response.then()
              .statusCode(201)
              .body("state", equalTo("open"));

      int issueNumber = response.jsonPath().getInt("number");

      IssuesService.getIssue(username, repoName, issueNumber)
              .then()
              .statusCode(200);

      IssuesService.addCommentToIssue(username, repoName, issueNumber,
                      JsonReader.getJson("Comment", Comment.class))
              .then()
              .statusCode(201);

      IssuesService.updateIssueTitle(username, repoName, issueNumber, "Updated Issue Title")
              .then()
              .statusCode(200)
              .body("title", equalTo("Updated Issue Title"));

      IssuesService.closeIssue(username, repoName, issueNumber)
              .then()
              .statusCode(200)
              .body("state", equalTo("closed"));

      repositoryService.deleteRepo(username, repoName)
              .then()
              .statusCode(204);
   }

}
