package tests;

import datareader.JsonReader;
import io.qameta.allure.*;
import io.restassured.response.Response;
import models.AssignLabelToIssue;
import models.Issue;
import models.Label;
import models.MileStone;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import services.LabelsAndMilestonesService;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@Epic("GitHub API")
@Feature("Labels and Milestones")
public class LabelsAndMilestonesE2ETests extends IssueApiBase {

    private LabelsAndMilestonesService labelsAndMilestonesService;

    private Response createdIssue;

    private Label label;

    private MileStone mileStone;

    @BeforeClass(dependsOnMethods = "setUpIssue")
    public void init() {
         repositoryService.createRepo(repository);
        createdIssue = IssuesService.
                       createIssue(username,repository.getName(),JsonReader.getJson("Issue",Issue.class));

        JsonReader labelData = new JsonReader("Label");

        JsonReader MilestoneData  = new JsonReader("MileStone");
        label = new Label(labelData.getJsonData("name"),labelData.getJsonData("color"),
                    labelData.getJsonData("description"));

        labelsAndMilestonesService = new LabelsAndMilestonesService();

        mileStone = new MileStone(MilestoneData.getJsonData("title"),
                  MilestoneData.getJsonData("due_on"));
    }

    @Story("Labels and Milestones E2E Flow")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void createLabelFullE2ETests() {
        //1.Create label like critical
        createLabel(username,repository.getName(),label);

       //2.Assign label to the bug
        assignLabelToIssue();
        //Create a milestone
        int milestoneNumber = createMilestone();

        //Assign a milestone for specific issue
        assignMilestoneToIssue(milestoneNumber);
    }

    @Step("Create label")
    private void createLabel(String username, String repo, Label label) {
        labelsAndMilestonesService.createLabel(username,repository.getName(),label)
                .then()
                .statusCode(201);
    }
    @Step("Assign label to issue")
    private void assignLabelToIssue() {
        labelsAndMilestonesService.assignLabelToIssue(
                        username,
                        repository.getName(),
                        JsonReader.getJson("AssignLabelToIssue", AssignLabelToIssue.class).getLabels(),
                        createdIssue.jsonPath().getInt("number"))
                .then()
                .statusCode(200)
                .body("name", hasItem("critical"));
    }

    @Step("Create milestone")
    private int createMilestone() {
        return labelsAndMilestonesService.createMileStone(
                        username,
                        repository.getName(),
                        mileStone)
                .then()
                .statusCode(201)
                .body("title", equalTo(mileStone.getTitle()))
                .extract()
                .path("number");
    }

    @Step("Assign milestone to issue")
    private void assignMilestoneToIssue(int milestoneNumber) {
        labelsAndMilestonesService.assignMilestoneToIssue(
                        username,
                        repository.getName(),
                        milestoneNumber,
                        createdIssue.jsonPath().getInt("number"))
                .then()
                .statusCode(200);
    }

   @AfterClass
   public void teardown() {
       repositoryService.deleteRepo(username,repository.getName());
    }
}
