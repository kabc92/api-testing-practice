package tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class RequestSpecTest {
    // I N S T A N C E  V A R I A B L E
    RequestSpecification requestSpec;

    // API = https://petstore.swagger.io/#/pet/findPetsByStatus

    @BeforeEach
    public void setUp(){
        requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io")
                .addHeader("api_key", "special-key")
                .addQueryParam("status", "available")
                .log(LogDetail.ALL)
                .build();
    }

    @Test
    public void getAvailablePets(){
                 given()
                   .spec(requestSpec)
                .when()
                   .get("/v2/pet/findByStatus")
                .then()
                    //.log().all()
                   .statusCode(200);

    }

    private long createPet(String name, String status) {

        Map<String, Object> petBody = new HashMap<>();
        petBody.put("name", name);
        petBody.put("status", status);


        // STEP 2 - POST to create a pet and get the generated ID
        return given()
                    .spec(requestSpec)
                    .contentType("application/json")
                    .body(petBody)
                .when()
                    .post("/v2/pet")
                .then()
                    .statusCode(200)
                .extract()
                    .path("id");
    }

    private void getPetAndValidate(long petId, String expectedName) {
        given()
                .spec(requestSpec)
                .pathParam("petId", petId)
        .when()
                .get("/v2/pet/{petId}")
        .then()
                //.log().all()
                .statusCode(200)
                .body("name", equalTo(expectedName));
    }

    @Test
    public void getPetById(){

        long petId = createPet("Milo", "available");
        getPetAndValidate(petId, "Milo");
    }


}

