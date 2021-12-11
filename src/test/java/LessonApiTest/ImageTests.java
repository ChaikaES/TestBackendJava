package LessonApiTest;

import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.responseSpecification;
import static org.example.Endpoints.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class ImageTests extends BaseTest {
    static String encodedFile;
    static RequestSpecification requestSpecificationWithAuthAndMultipartImage;
    static RequestSpecification requestSpecificationWithAuthWithBase64;
    private final String PATH_TO_IMAGE = "src/test/resources/884422.jpg";
    String uploadedImageId;
    MultiPartSpecification base64MultiPartSpec;
    MultiPartSpecification multiPartSpecWithFile;

    @BeforeEach
    void beforeTest() {

        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
        base64MultiPartSpec = new MultiPartSpecBuilder(encodedFile)
                .controlName("image")
                .build();

        multiPartSpecWithFile = new MultiPartSpecBuilder(new File(PATH_TO_IMAGE))
                .controlName("image")
                .build();

        requestSpecificationWithAuthAndMultipartImage = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addFormParam("title", "Dino")
                .addFormParam("type", "jpeg")
                .addMultiPart(multiPartSpecWithFile)
                .build();

        requestSpecificationWithAuthWithBase64 = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addFormParam("title", "Dino")
                .addFormParam("type", "jpeg")
                .addMultiPart(base64MultiPartSpec)
                .build();

        positiveResponse = new ResponseSpecBuilder()
                .expectBody("status", Matchers.equalTo(200))
                .expectBody("success", Matchers.is(true))
                .expectBody("data.id", is(notNullValue()))
                .expectBody("data.title", equalTo("Dino"))
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .build();

    }

    private byte[] getFileContent() {
        byte[] byteArray = new byte[0];
        try {
            byteArray = FileUtils.readFileToByteArray(new File(PATH_TO_IMAGE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    @Test
    void uploadFileTest() {
        byte[] byteArray = getFileContent();
        String encodedFile = Base64.getEncoder().encodeToString(byteArray);
        uploadedImageId = given(requestSpecificationWithAuthWithBase64, positiveResponse)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void uploadFileImageTest() {
        uploadedImageId = given(requestSpecificationWithAuthAndMultipartImage, positiveResponse)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @AfterEach
    void tearDown() {
        given(requestWithAuth, responseSpecification)
                .delete(DELETE_IMAGE, username, uploadedImageId)
                .prettyPeek();
    }
}
