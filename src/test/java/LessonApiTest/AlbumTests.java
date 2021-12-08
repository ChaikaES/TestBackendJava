package LessonApiTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static io.restassured.RestAssured.given;

public class AlbumTests extends BaseTest{
    private final String FIRST_IMAGE = "src/test/resources/884422.jpg";
    private List<String> imageDeleteHashes = new ArrayList<>();

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

    private String createAlbum(String title, String imageHash) {
        return given()
                .headers("Authorization", token)
                .formParams(new HashMap<String, String>() {{
                    put("ids", imageHash);
                    put("title", title);
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

    @AfterEach
    void removeImages(){
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
    void testCreateAlbum(){
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
                .body("data.title", equalTo("MyNewOwn"))
                .when()
                .post("https://api.imgur.com/3/album")
                .prettyPeek();
    }

    @Test
    void testGetAlbum(){
        String imageHash = uploadImage(FIRST_IMAGE);
        String albumHash = createAlbum("MiMi", imageHash);

        given()
                .headers("Authorization", token)
                .expect()
                .statusCode(200)
                .body("data.link", equalTo("https://imgur.com/a/" + albumHash))
                .body("data.title", equalTo("MiMi"))
                .body("data.id", equalTo(albumHash))
                .body("data.cover", equalTo(imageHash))
                .when()
                .get("https://api.imgur.com/3/album/{albumHash}", albumHash)
                .prettyPeek();
    }

    @Test
    void testDeleteAlbum(){
        String imageHash = uploadImage(FIRST_IMAGE);
        String albumHash = createAlbum("Album", imageHash);

        given()
                .headers("Authorization", token)
                .expect()
                .statusCode(200)
                .body("data.success", equalTo(true))
                .when()
                .delete("https://api.imgur.com/3/album/{albumHash}", albumHash)
                .prettyPeek();
    }
}
