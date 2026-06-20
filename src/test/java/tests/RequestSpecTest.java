package tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RequestSpecTest {
    // I N S T A N C E  V A R I A B L E
    RequestSpecification requestSpec; // esta variable viene de de la interface RequestSpecification

    // API = https://petstore.swagger.io/#/pet/findPetsByStatus

    /*
    @BeforeEach
    public void setUp(){
        requestSpec = given() // asigno mi variable proveniente de una interface a un metodo de RestAssured para poder crear mi fase de config del request
                        .baseUri("https://petstore.swagger.io") //es mi base de URL / a donde voy
                        .header("api_key" , "special-key") // custom header porque ocupas mandar un tipo de authentication
                        .queryParam("status", "available")
                        .log().all(); // loguea en consola toda la información del request: URL, headers, params, body. Es para debugging
    }
     */
    @BeforeEach
    public void setUp(){
        requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io")
                .addHeader("api_key", "special-key")
                .addQueryParam("status", "available")
                .log(LogDetail.ALL)
                .build();
    }


    /*
    El test debe:Anotarlo como test
    Usar el requestSpec que ya construiste en el given()
    Hacer un GET al endpoint de pets por status
    Validar que regrese 200
     */

    @Test
    public void obtenerPetsDisponibles(){
                 given()
                   .spec(requestSpec)
                .when()
                   .get("/v2/pet/findByStatus")
                .then()
                    .log().all()
                   .statusCode(200);

    }

    /*
    Bien, el id 102 y el nombre Tom.
Ahora escribe el test. Se llama obtenerPetPorId y debe:

Usar el requestSpec
Usar .pathParam("petId", 102) en el given()
Hacer GET a /v2/pet/{petId} — las llaves indican que ahí va el path param
Validar status 200
Validar que el campo name sea igual a Tom
     */
    @Test
    public void getPetById(){
        given()
                .spec(requestSpec)
                .pathParam("petId", 365782)
        .when()
                .get("/v2/pet/{petId}")// Las llaves indican que ahi va el pathParam!
        .then()
                //.log().all()
                .statusCode(200)
                .body("name", equalTo("doggie"));
    }


}

