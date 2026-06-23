package tests;

import base.BaseFinancialTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
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
    public void validateFiveYearMXNRangeOLD() {

        //The following ratesMap variable will extract AND store the values from 2020-01-01 to 2025-12-31 using a Map
        // "2020-01-02"  :  { "MXN" → 18.9 }  --> Current Json format
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

    private Map<String, Map<String, Float>> getHistoricalRates5Yrs(){ // "2020-01-02"  :  { "MXN" → 18.9 }  --> Current Json format

        //The following ratesMap variable will extract AND store the values from 2020-01-01 to 2025-12-31 using a Map
        // "2020-01-02"  :  { "MXN" → 18.9 }  --> Current Json format
        return given()
                    .spec(requestSpec)
                    .queryParam("from", "USD")
                    .queryParam("to", "MXN")
                .when()
                    .get("/v1/2020-01-01..2025-12-31")
                .then()
                    //.log().all()
                    .statusCode(200)
                .extract()
                    .path("rates"); //Extract the whole "rates" object from the JSON
    }
    @Test
    public void validateFiveYearMXNRange() {

        Map<String, Map<String, Float>> ratesMap = getHistoricalRates5Yrs();

        float min = Float.MAX_VALUE; // Start with the largest possible float value so any real rate will be smaller
        float max = Float.MIN_VALUE; // Start with the smallest possible float value so any real rate will be larger

        //ratesMap contains this: "2020-01-02"  :  { "MXN" → 18.9 }"
        //using ratesMap.values() we ignored the date and get only the "MXN:18.9" part
        for (Map<String, Float> dailyRate : ratesMap.values()) { // dataType var : collection

            Float rate = dailyRate.get("MXN"); //Gets the VALUE associated with the "MXN" KEY from the map (e.g., 18.9) not the entire key-value pair (MXN: 18.9)

            if (rate < min)
                min = rate;
            if (rate > max)
                max = rate;
        }
        assertTrue(min > 15.0f && max < 26.0f);

        System.out.println("actualMin: " + min);
        System.out.println("actualMax: " + max);
        System.out.println("");
        System.out.println("Total entries: " + ratesMap.size());
        System.out.println("First entry value: " + ratesMap.values().iterator().next());
        System.out.println("All values: " + ratesMap.values());
    }

    @Test
    public void validateSortedMXNRates(){

        //GET HISTORICAL RATES
        // "2020-01-02"  :  { "MXN" → 18.9 }  --> Current Json format
        Map<String, Map<String, Float>> ratesMap = getHistoricalRates5Yrs();

        //Step 2 - Create ArrayList and populate it with MXN values
        ArrayList<Float> ratesList = new ArrayList<>();

        for(Map<String, Float> dailyRate : ratesMap.values()){

            Float rate = dailyRate.get("MXN");
            ratesList.add(rate);
        }
        System.out.println("Before sorting: " + ratesList);

        //ratesList
        for(int i = 0;  i < ratesList.size() - 1; i++){
            for(int j = 0; j < ratesList.size() - 1 - i; j++){
                if(ratesList.get(j) > ratesList.get(j + 1)){ // example: j:25.1 > j+1:19.5? (25.1, 19.5)
                    Float temp = ratesList.get(j); //store the value from j (25.1) in a temp variable
                    ratesList.set(j,ratesList.get(j+1));// set the value from j+1 in j --> (19.5, 19.5)
                    ratesList.set(j+1, temp);//set the value of j+1 with the temp variable: 19.5, 25.1
                }
            }
        }
        System.out.println("\nAfter sorting: " + ratesList);
        assertTrue(ratesList.get(0) < ratesList.get(ratesList.size()-1));

    }


     private void bubbleSortDemo(){
        float [] numbers = {25.1f, 19.5f, 18.8f, 16.3f}; //4

        System.out.print("Before Sorting: " + Arrays.toString(numbers));

        //BUBBLE SORT
        for(int i = 0; i < numbers.length - 1; i++){
            for (int j = 0; j < numbers.length - 1 - i; j++){ // 1 < 4 -1 -1 : 1 < 2

                if(numbers[j] > numbers[j + 1]){ //19.5 > 25.3? NO
                    float temp = numbers[j]; //save the value from the left: 25.1
                    numbers[j] = numbers[j + 1];  //swap the right one to the left: 18.8
                    numbers [j + 1] = temp; //set the temp as the value on the right side: 25.1
                }
            }
        }
        //print after sorting
        System.out.println("\nAfter Sorting: " + Arrays.toString(numbers));
    }


}
