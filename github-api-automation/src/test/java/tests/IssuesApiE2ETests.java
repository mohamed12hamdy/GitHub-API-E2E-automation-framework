package tests;

import datareader.JsonReader;
import io.restassured.response.Response;
import models.Comment;
import models.Issue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import services.issuesService;

import static org.hamcrest.Matchers.equalTo;

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

   @Test
   public void testIssueFullE2ETests() {

      Response response = IssuesService.createIssue(username, repository.getName(), issue);
      response.then().statusCode(201).body("state", equalTo("open"));

      int issueNumber = response.jsonPath().getInt("number");

     IssuesService.getIssue(username, repository.getName(), issueNumber).then().statusCode(200);

     IssuesService.addCommentToIssue(username, repository.getName(), issueNumber,
                      JsonReader.getJson("Comment", Comment.class))
              .then().statusCode(201);

     IssuesService.updateIssueTitle(username, repository.getName(), issueNumber, "Updated Issue Title")
              .then().statusCode(200)
              .body("title", equalTo("Updated Issue Title"));

      IssuesService.closeIssue(username, repository.getName(), issueNumber)
              .then().statusCode(200)
              .body("state", equalTo("closed"));

   }

   @AfterClass
    public void teardown() {
       repositoryService.deleteRepo(username, repository.getName());
   }





}
