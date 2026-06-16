import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthTest {

    @BeforeEach
    public void setUp(){
        //RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
        RestAssured.baseURI = "https://petstore.swagger.io";
    }
/*
    @Test
    public void requestConBearerToken(){
        String token = "patitasdemilo";
        RestAssured
                .given()
                    .header("Authorization", "Bearer" + token)
                .when()
                    .get("/users")
                .then()
                    .statusCode(200);

    }

 */
    /*
    Escribe un nuevo test en AuthTest llamado requestConApiKey que haga un GET
    a /pet/findByStatus con el parámetro status=available y el header api_key
    con valor special-key contra https://petstore.swagger.io/v2.
     */

    @Test
    public void requestConApiKey(){
        RestAssured
                .given()
                    .header("api_key" , "special-key")
                    .queryParam("status", "available")
                    .log().all()
                .when()
                    .get("/v2/pet/findByStatus")
                .then()
                    .log().all()
                    .statusCode(200);
    }

}
