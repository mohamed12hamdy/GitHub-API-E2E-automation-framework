package tests;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("GitHub API")
@Feature("Repository full cycle")
public class RepositoryApiE2ETests extends RepositoryApiBase {

    @Test
    public void repositoryFullE2ETests() {
        createRepo();
        getRepo();
        String newDescription = "Updated description for " + repository.getName();
        updateRepoDescription(newDescription);
        deleteRepo();
        getRepoAfterDeletion();
    }

    @Step("Create repo")
    @Severity(SeverityLevel.CRITICAL)
    private void createRepo() {
        repositoryService.createRepo(repository).then().statusCode(201);
    }

    @Step("Get repo")
    @Severity(SeverityLevel.CRITICAL)
    private void getRepo() {
        repositoryService.getRepo(username, repository.getName()).then().statusCode(200);
    }

    @Step("Update repo description")
    @Severity(SeverityLevel.CRITICAL)
    private void updateRepoDescription(String newDescription ) {
        repositoryService.updateRepoDescription(username, repository.getName(), newDescription)
                .then().statusCode(200);
    }

    @Step("Delete repo")
    @Severity(SeverityLevel.CRITICAL)
    private void deleteRepo() {
        repositoryService.deleteRepo(username, repository.getName()).then().statusCode(204);
    }

    @Step("Get repo after deletion")
    @Severity(SeverityLevel.CRITICAL)
    private void getRepoAfterDeletion() {
        repositoryService.getRepo(username, repository.getName()).then().statusCode(404);
    }

}
