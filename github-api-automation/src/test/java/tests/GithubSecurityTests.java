package tests;

import config.ConfigReader;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import services.GitHubSecurityService;

@Epic("GitHub API")
@Feature("Security")
public class GithubSecurityTests extends BaseTest{

    private GitHubSecurityService gitHubSecurityService;

    @BeforeClass
    public void setUp() {
        gitHubSecurityService = new GitHubSecurityService();
    }

    @Story("Authenticated user")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void testGetAuthenticatedUser() {
        Response response = gitHubSecurityService.getAuthenticatedUser();

        Assert.assertEquals(response.getStatusCode(), 200,
                "Expected status code 200 for authenticated user");

        Assert.assertEquals(response.jsonPath().getString("login"), ConfigReader.get("username"),
                "Expected login to match the configured username" );
    }

    @Story("Unauthenticated user")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void testGenUnAuthenticatedUserWithoutToken() {
        Response response = gitHubSecurityService.getUnauthenticatedUser();

        Assert.assertEquals(response.getStatusCode(), 401,
                "Expected status code 401 for unauthenticated user");
    }

    @Story("Rate limit")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void testCheckRateLimit() {
        Response response = gitHubSecurityService.checkRateLimit();

        Assert.assertEquals(response.getStatusCode(), 200,
                "Expected status code 200 for rate limit endpoint");

        int remaining = response.jsonPath().getInt("rate.remaining");
        Assert.assertTrue(remaining > 0, "Expected remaining rate limit to be greater than 0");
    }
}
