package tests;

import base.BaseFinancialTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FrankfurterTest extends BaseFinancialTest {
//API: https://frankfurter.dev

    @Test
    public void getLatestExchangeRates(){
        // GET latest exchange rates and validate base currency, date and USD rate
        given()
                .spec(requestSpec) //use this setup I already prepared it using setUp()
        .when()
                .get("/latest")
        .then()
                .log().all()
                .body("base",equalTo("EUR"))
                .body("date", notNullValue())
                .body("rates.USD", greaterThan(0.0f))
                .statusCode(200);
    }

    @Test
    public void getSpecificCurrencyRate(){
        // GET USD to MXN exchange rate and validate base currency and rate is positive
        given()
                .spec(requestSpec)
                .queryParam("from", "USD")
                .queryParam("to", "MXN")//"/latest?from=USD&to=MXN")
        .when()
                .get("/latest")
        .then()
                .body("base", equalTo("USD"))
                .body("rates.MXN", greaterThan(0.0f))
                .statusCode(200);
    }

    @Test
    public void getHistoricalRate(){

        given()
                .spec(requestSpec)
        .when()
                .get("/2024-01-15")
        .then()
                .log().all()
                .body("date", equalTo("2024-01-15"))
                .statusCode(200);
    }

    @Test
    public void validateHistoricalMXNRate(){
        // GET USD to MXN exchange rate and validate base currency and rate is positive
        float exchangeRate =        //I will store the exchangeRate value from a desired rate in a variable
                given()
                        .spec(requestSpec) //retrieve setUp() information for this method
                .when()
                        .get("/2020-01-15")// get whatever you have from this historical date
                .then()
                        .log().all() //for log purposes
                        .statusCode(200) //validate that it gives status code of 200
                .extract()//in order to extract and access the required values from java
                        .path("rates.MXN"); //using this path

        assertTrue(exchangeRate > 15.0f && exchangeRate < 25.0f); //validate that the exchangeRate is between 15 and 25 mexican pesos
    }

    @Test
    public void calculateUSDtoMXN(){
        // Extract current USD to MXN exchange rate
        float exchangeRate =
                given()
                    .spec(requestSpec)
                        .queryParam("from", "USD")
                        .queryParam("to", "MXN")
                .when()
                    .get("/latest")
                .then()
                        .log().all()
                        .statusCode(200)
                .extract()
                        .path("rates.MXN");
        //Calculate total MXN for $5,000 USD
        float totalMXN = 5000 * exchangeRate;

        //Validate that $5,000USD is always more than $50,000 MXN
        assertTrue(totalMXN > 50000);

    }


}
