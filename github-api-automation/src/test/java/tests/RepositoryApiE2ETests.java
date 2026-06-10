package tests;

import config.ConfigReader;
import datareader.JsonReader;
import models.Repository;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import services.RepositoryService;
public class RepositoryApiE2ETests extends BaseTest {

    protected Repository repository;

    private JsonReader repoData;

    protected RepositoryService repositoryService;

    protected String username;


    @BeforeClass
    public void setUp() {
        repositoryService = new RepositoryService();
        repoData = new JsonReader("Repository");
        repository = new Repository(repoData.getJsonData("name"),
                repoData.getJsonData("description"),
                Boolean.parseBoolean(repoData.getJsonData("private")),
                Boolean.parseBoolean(repoData.getJsonData("auto_init")));

         username = ConfigReader.get("username");
    }

    @Test
    public void RepositoryFullE2ETests() {

            repositoryService.createRepo(repository)
                    .then()
                    .statusCode(201);


            repositoryService.getRepo(username, repository.getName())
                    .then()
                    .statusCode(200);


            String newDescription = "Updated description for " + repository.getName();
            repositoryService.updateRepoDescription(username, repository.getName(), newDescription)
                    .then()
                    .statusCode(200);


            repositoryService.deleteRepo(username, repository.getName())
                    .then()
                    .statusCode(204);


            repositoryService.getRepo(username, repository.getName())
                    .then()
                    .statusCode(404);
    }
}
