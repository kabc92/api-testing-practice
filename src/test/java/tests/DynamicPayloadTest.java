package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DynamicPayloadTest extends BaseTest {
    /*
        Ahora escribe el primer test. Se llama crearPostDinamico y debe:
    Declarar tres variables String — userId, titulo y contenido con cualquier valor
    Construir el body concatenando esas variables
    Hacer POST a /posts  Validar 201
     */

    int userId = 35079;
    String titulo = "Mr. ";
    String contenido = "Testing dynamic payloads";

    @Test
    public void createDynamicPost(){
    // En Java Object es la clase madre de absolutamente de todos los data type
        Map<String, Object> body = new HashMap<>(); // Entonces cuando dices Object como tipo estás diciendo "acepta cualquier cosa" — un int, un String, un boolean, un objeto personalizado, lo que sea.
        body.put("userId", userId);
        body.put("title", titulo);
        body.put("body", contenido);

        given()
                .spec(requestSpec)
                .contentType("application/json") //content type is a MUST when sending a post
                .body(body)
        .when()
                .post("/posts")
        .then()
                .log().all()
                .statusCode(201)
        .body("title", equalTo(titulo));

    }

}
