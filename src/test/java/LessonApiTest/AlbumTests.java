package LessonApiTest;

import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.HashPair;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;

import java.io.File;

import static org.example.Endpoints.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class AlbumTests extends BaseTest {
    private final String FIRST_IMAGE = "src/test/resources/884422.jpg";
    private String firstImageHash;
    private String firstImageDeleteHash;

    private String albumDeleteHash;


    @BeforeEach
    void beforeEach() {
        HashPair hashPair = uploadImage(FIRST_IMAGE);
        firstImageHash = hashPair.getHash();
        firstImageDeleteHash = hashPair.getDeleteHash();
    }

    @AfterEach
    void afterEach() {
        given(requestWithAuth, positiveResponse)
                .delete(DELETE_IMAGE, username, firstImageDeleteHash)
                .prettyPeek();

        given(requestWithAuth, positiveResponse)
                .delete(ALBUM_DETAIL, albumDeleteHash)
                .prettyPeek();
    }

    private HashPair uploadImage(String imagePath) {
        MultiPartSpecification multiPartSpecWithFile = new MultiPartSpecBuilder(new File(imagePath))
                .controlName("image")
                .build();

        RequestSpecification requestSpecificationWithAuthAndMultipartImage = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addMultiPart(multiPartSpecWithFile)
                .build();

        Response response = given(requestSpecificationWithAuthAndMultipartImage, positiveResponse)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response();

        return new HashPair(
                response.jsonPath().getString("data.id"),
                response.jsonPath().getString("data.deletehash")
        );
    }

    private HashPair createAlbum(String title, String imageHash) {
        RequestSpecification requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addFormParam("ids", imageHash)
                .addFormParam("title", title)
                .addFormParam("description", "Be ready")
                .addFormParam("cover", imageHash)
                .build();

        ResponseSpecification responseSpecification = new ResponseSpecBuilder()
                .expectBody("status", Matchers.equalTo(200))
                .expectBody("success", is(true))
                .expectBody("data.title", equalTo(title))
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .build();

        Response response = given(requestSpecification, responseSpecification)
                .post(ALBUM_LIST)
                .prettyPeek()
                .then()
                .extract()
                .response();

        return new HashPair(
                response.jsonPath().getString("data.id"),
                response.jsonPath().getString("data.deletehash")
        );
    }


    @Test
    void testCreateAlbum() {
        RequestSpecification requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addFormParam("ids", firstImageHash)
                .addFormParam("title", "MyNewOwn")
                .addFormParam("description", "Be ready")
                .addFormParam("cover", firstImageHash)
                .build();

        ResponseSpecification responseSpecification = new ResponseSpecBuilder()
                .expectBody("status", Matchers.equalTo(200))
                .expectBody("success", is(true))
                .expectBody("data.title", equalTo("MyNewOwn"))
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .build();

        given(requestSpecification, responseSpecification)
                .post(ALBUM_LIST)
                .prettyPeek();
    }

    @Test
    void testGetAlbum() {
        HashPair hashPair = createAlbum("MiMi", firstImageHash);
        String albumHash = hashPair.getHash();
        albumDeleteHash = hashPair.getDeleteHash();

        ResponseSpecification responseSpecification = new ResponseSpecBuilder()
                .expectBody("status", Matchers.equalTo(200))
                .expectBody("success", is(true))
                .expectBody("data.link", equalTo("https://imgur.com/a/" + albumHash))
                .expectBody("data.title", equalTo("MiMi"))
                .expectBody("data.id", equalTo(albumHash))
                .expectBody("data.cover", equalTo(firstImageHash))
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .build();

        given(requestWithAuth, responseSpecification)
                .get(ALBUM_DETAIL, albumHash)
                .prettyPeek();
    }

    @Test
    void testDeleteAlbum() {
        HashPair hashPair = createAlbum("Album", firstImageHash);
        String albumHash = hashPair.getHash();
        albumDeleteHash = hashPair.getDeleteHash();

        given(requestWithAuth, positiveResponse)
                .delete(ALBUM_DETAIL, albumHash)
                .prettyPeek();
    }
}
