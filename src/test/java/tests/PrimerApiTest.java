package tests;

import base.BaseTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class PrimerApiTest extends BaseTest {

    /*
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }
     */

    @Test
    public void obtenerUsuarios() {
        RestAssured
                .given()
                    .spec(requestSpec)
                .when()
                    .get("/users")
                .then()
                    .statusCode(200);
    }

    @Test
    public void validarCuerpoRespuesta() {
        given()
                .spec(requestSpec)
                .when()
                    .get("/users")
                .then()
                    .statusCode(200)
                    .body("size()", equalTo(10))
                    .body("[0].name", equalTo("Leanne Graham"));
    }

    @Test
    public void validateHeaders() {
        given()
                .spec(requestSpec)
                .when()
                    .get("/users")
                .then()
                    .statusCode(200)
                    .header("Content-Type", containsString("application/json"));
    }

    @Test
    public void userDoesNotExist() {
        given()
                .spec(requestSpec)
                .when()
                    .get("/users/99999")
                .then()
                    .statusCode(404);
    }

    @Test
    public void createPost() {
        given()
                .spec(requestSpec)
                .contentType("application/json")
                .body("{ \"userId\": 1, \"title\": \"Mi titulo\", \"body\": \"Mi contenido\" }")
                .when()
                    .post("/posts")
                .then()
                    .statusCode(201)
                    .body("title", equalTo("Mi titulo"))
                    .body("userId", equalTo(1));
    }

    @Test
    public void updateTheWholePost() {
        given()
                .spec(requestSpec)
                .contentType("application/json")
                .body("{ \"id\": 1, \"userId\": 1, \"title\": \"Titulo actualizado\", \"body\": \"Contenido actualizado\" }")
                .when()
                    .put("/posts/1")
                .then()
                    .statusCode(200)
                    .body("title", equalTo("Titulo actualizado"));
    }

    @Test
    public void actualizarPostParcial() {
        given()
                .spec(requestSpec)
                .contentType("application/json")
                .body("{ \"title\": \"Solo actualizo el titulo\" }")
                .when()
                    .patch("/posts/1")
                .then()
                    .statusCode(200)
                    .body("title", equalTo("Solo actualizo el titulo"));
    }

    @Test
    public void deletePost() {
        given()
                .spec(requestSpec)
                .when()
                    .delete("/posts/1")
                .then()
                    .statusCode(200);
    }

}