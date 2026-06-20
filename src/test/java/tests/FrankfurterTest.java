package tests;

import base.BaseFinancialTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Map;

public class FrankfurterTest extends BaseFinancialTest {
//API: https://frankfurter.dev / https://frankfurter.dev/v1/

    @Test
    public void getLatestExchangeRates(){
        // GET latest exchange rates and validate base currency, date and USD rate
        given()
                .spec(requestSpec) //use this setup I already prepared it using setUp()
        .when()
                .get("/v1/latest")
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
                .get("/v1/latest")
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
                .get("/v1/2024-01-15")
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
                        .get("/v1/2020-01-15")// get whatever you have from this historical date
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
                    .get("/v1/latest")
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

    @Test
    public void validateFiveYearMXNRange() {

        //The following rates variable will extract AND store the values from 2020-01-01 to 2025-12-31 using a LIST
        // "2020-01-02"  :  { "MXN" → 18.9 }
        Map<String, Map<String, Float>> ratesMap = given()
                .spec(requestSpec)
                .queryParam("from", "USD")
                .queryParam("to", "MXN")
                .when()
                .get("/v1/2020-01-01..2025-12-31")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .path("rates"); // extract the whole "rates" object

        float min = Float.MAX_VALUE; // Start with the largest possible float value so any real rate will be smaller
        float max = Float.MIN_VALUE; // Start with the smallest possible float value so any real rate will be larger

        //ratesMap contains this: "2020-01-02"  :  { "MXN" → 18.9 }"
        //using ratesMap.values() we ignored the date and get only the "MXN:18.9" part
        for (Map<String, Float> dailyRate : ratesMap.values()) { // dataType var : collection

            Float rate = dailyRate.get("MXN"); //dailyRAte {"MXN": 18.9}

            if (rate < min)
                min = rate;
            if (rate > max)
                max = rate;
        }
        assertTrue(min > 15.0f && max < 26.0f);

        System.out.println("actualMin: " + min);
        System.out.println("actualMax: " + max);
    }
}
