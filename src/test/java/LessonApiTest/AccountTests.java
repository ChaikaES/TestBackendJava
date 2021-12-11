package LessonApiTest;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

import static org.example.Endpoints.ACCOUNT_DETAIL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class AccountTests extends BaseTest{

    @Test
    void getAccountInfoTest() {
        given(requestWithAuth)
                .get(ACCOUNT_DETAIL, username)
                .prettyPeek();
    }

    @Test
    void getAccountInfoWithAssertionsInGivenTest() {
        positiveResponse = new ResponseSpecBuilder()
                .expectBody("data.url", equalTo(username))
                .expectBody("status", Matchers.equalTo(200))
                .expectBody("success", is(true))
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .build();

        given(requestWithAuth, positiveResponse)
                .get(ACCOUNT_DETAIL, username)
                .prettyPeek();
    }

    @Test
    void getAccountInfoWithAssertionsAfterTest() {
        Response response = given(requestWithAuth)
                .get(ACCOUNT_DETAIL, username)
                .prettyPeek();

        assertThat(response.jsonPath().get("data.url"), equalTo(username));
    }
}
