package LessonApiTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import io.restassured.response.Response;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;

public class AlbumTests extends BaseTest{
    private final String FIRST_IMAGE = "src/test/resources/884422.jpg";
    static private List<String> imageDeleteHashes = new ArrayList<>();

    private String uploadImage(String imagePath) {
        Response response = given()
                .headers("Authorization", token)
                .multiPart("image", new File(imagePath))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response();

        imageDeleteHashes.add(response.jsonPath().getString("data.deletehash"));

        return response.jsonPath().getString("data.id");
    }

    @AfterAll
    static void removeImages(){
        for (String deleteHash: imageDeleteHashes) {
            given()
            .headers("Authorization", token)
            .when()
            .delete("https://api.imgur.com/3/account/{username}/image/{deleteHash}", "testprogmath", deleteHash)
            .prettyPeek()
            .then()
            .statusCode(200);
        }
    }

    @Test
    void createAlbum(){
        String imageHash = uploadImage(FIRST_IMAGE);

        given()
                .headers("Authorization", token)
                .formParams(new HashMap<String, String>() {{
                    put("ids", imageHash);
                    put("title", "MyNewOwn");
                    put("description", "Be ready");
                    put("cover", imageHash);
                }})
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/album")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
    }
}
