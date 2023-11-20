package stepDef;

import Utility.ConfigurationReader;
import Utility.DBUtil;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.internal.common.assertion.Assertion;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class US1_StepDef {
    public static String token;
    public static Response response;
    public static int expectedAllUsersNumber;
    public static int actualAllUsersNumber;


    @Given("User logged in as {string}")
    public void user_logged_in_as(String role) {
        response = RestAssured.given().contentType(ContentType.URLENC).formParam("email", ConfigurationReader.getProperty(role + "UserName")).formParam("password", ConfigurationReader.getProperty(role + "Password")).accept(ContentType.JSON)
                .when().post(
                        "https://library2.cydeo.com/rest/v1/login").then().extract().response();
        token = response.path("token");
   //     System.out.println(token);

    }

    @When("Sending request to API to retrieve all users bio")
    public void sending_request_to_api_to_retrieve_all_users_bio() {
        response = RestAssured.given().accept(ContentType.JSON)
                .header("x-library-token", token)
                .when().get(ConfigurationReader.getProperty("baseURI") + "/get_all_users")
               // .prettyPeek()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON).extract().response();

        JsonPath jsonPath = response.jsonPath();
        List<Integer> id = jsonPath.getList("id");
        actualAllUsersNumber = id.size();

    }

    @When("Sending query to DB to get expected users bio")
    public void sending_query_to_db_to_get_expected_users_bio(){

        DBUtil.createConnection(ConfigurationReader.getProperty("dbUrl"), ConfigurationReader.getProperty("dbUsername"), ConfigurationReader.getProperty("dbPassword"));

        DBUtil.runQuery("SELECT count(*) from users");

        String numberOfUsers = DBUtil.getFirstRowFirstColumn();
        System.out.println("numberOfUsers = " + numberOfUsers);

        expectedAllUsersNumber = Integer.parseInt(numberOfUsers);

        DBUtil.destroy();
    }

    @Then("Verifying match of expected and actual bios")
    public void verifying_match_of_expected_and_actual_bios() {

        Assert.assertEquals(expectedAllUsersNumber, actualAllUsersNumber);
    }

}
