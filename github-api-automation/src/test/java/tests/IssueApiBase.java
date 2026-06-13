package tests;

import datareader.JsonReader;
import models.Issue;
import org.testng.annotations.BeforeClass;
import services.issuesService;

public class IssueApiBase extends RepositoryApiBase {

    protected issuesService IssuesService;
    protected int issueNumber;

    @BeforeClass(dependsOnMethods = "setUp")
    public void setUpIssue() {
        IssuesService = new issuesService();
        JsonReader issueData = new JsonReader("Issue");
        Issue issue = new Issue(
                issueData.getJsonData("title"),
                issueData.getJsonData("body"),
                issueData.getJsonList("labels")
        );

    }
}
