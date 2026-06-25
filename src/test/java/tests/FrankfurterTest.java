package tests;

import base.BaseFinancialTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private ArrayList<Float> getMXNRatesList(){

        // Helper method - extracts all MXN exchange rate values from the 5-year historical data
        // "2020-01-02"  :  { "MXN" → 18.9 }  --> Current Json format from the API
        Map<String, Map<String, Float>> ratesMap = getHistoricalRates5Yrs();

        //Create the ArrayList where you want to store all the values coming from the Map
        ArrayList<Float> ratesList = new ArrayList<>();

        //Retrieve and add all the values to the list
        for(Map<String, Float> dailyRate : ratesMap.values()){ // { "MXN" → 18.9 }  --> Current retrieved Json format from the API
            ratesList.add(dailyRate.get("MXN")); //populate the list with the values from the object "MXN"
        }
        // Returns an ArrayList of Float values representing daily USD to MXN rates from 2020 to 2025
        return ratesList;
    }
    @Test
    public void validateNoDuplicateDates(){

        Map<String, Map<String, Float>> ratesMap = getHistoricalRates5Yrs();

        Set<String> dates = ratesMap.keySet(); //ratesMap.keySet() returns a Set<String> con todas esas fechas:
        assertTrue(dates.size() == ratesMap.size());

        System.out.println("Total dates in Map: " + ratesMap.size());
        System.out.println("Unique dates in Set: " + dates.size());

        ArrayList<Float> ratesList = getMXNRatesList();

        Set<Float> uniqueRates = new HashSet<>(ratesList);
        assertTrue(uniqueRates.size() < ratesList.size());

        System.out.println("\nTotal rates in Map: " + ratesMap.size());
        System.out.println("Unique rates in Set: " + uniqueRates.size());
    }

    // HELPER METHOD - returns the minimum and maximum USD to MXN exchange rates from 5 years of historical data
    // Returns a Map with two keys: "minRate" and "maxRate"
    private Map<String, Float> getMinMaxHistoricalRates(){

        // GET 5 years of historical rates from Frankfurter API
        // Format returned: "2020-01-02" : { "MXN" → 18.9 }
        Map<String, Map<String, Float>> ratesMap = getHistoricalRates5Yrs();

        // Start with extreme values so any real rate will replace them on the first comparison
        float min = Float.MAX_VALUE; // Start with the largest possible float value so any real rate will be smaller
        float max = Float.MIN_VALUE; // Start with the smallest possible float value so any real rate will be larger

        //ratesMap contains this: "2020-01-02"  :  { "MXN" → 18.9 }"
        //using ratesMap.values() we ignored the date and get only the "MXN:18.9" part through JsonPath
        for (Map<String, Float> dailyRate : ratesMap.values()) { // dataType var : collection

            Float rate = dailyRate.get("MXN"); //Gets the VALUE associated with the "MXN" KEY from the map (e.g., 18.9) not the entire key-value pair (MXN: 18.9)

            if (rate < min) //update min if current rate is higher than the current minimum
                min = rate;
            if (rate > max)// update max if current rate is higher than the current maximum
                max = rate;
        }
        //Build the result Map with 2 entries: minRate y maxRate()
        Map<String, Float> result = new HashMap<>();
        result.put("min", min); // example: "minRate" -> 16.31
        result.put("max", max); // example: "maxRate" -> 25.104

        //System.out.println(result);
        return result;
    }

    private float calculateCarPriceInMXN(float carPriceUSD, float exchangeRate){

        return carPriceUSD * exchangeRate;
    }

    private void validatePriceWithinHistoricalRange(float carPriceUSD, float carPriceMXN,float minRate, float maxRate){

        // STEP 4 - Calculate min and max possible car prices based on historical exchange rates
        float minPriceMXN = carPriceUSD * minRate; // cheapest the car could have been: 25000 * 16.3 = ~407,812
        float maxPriceMXN = carPriceUSD * maxRate; // most expensive: 25000 * 25.1 = ~627,600

// Validate current price falls within the historical range
        assertTrue(carPriceMXN > minPriceMXN && carPriceMXN < maxPriceMXN);
        System.out.println("Valid price range: " + minPriceMXN + " - " + maxPriceMXN);

    }



    @Test
    public void endToEndCarPriceQuote() {

     float carPriceUSD = 25000;
// STEP 1 - GET current USD to MXN exchange rate from Frankfurter API
// Extract only the MXN rate value from the response JSON: { "rates": { "MXN": 19.5 } }
        float exchangeRate = given()
                    .spec(requestSpec) // base URI: https://api.frankfurter.dev
                     .queryParam("from", "USD") // convert FROM USD
                    .queryParam("to", "MXN") // convert TO MXN
                .when()
                    .get("/v1/latest") // GET latest exchange rate
                .then()
                    .statusCode(200) // validate successful response
                .extract()
                    .path("rates.MXN"); // extract only the MXN value as float

// STEP 2 - Calculate the price of a $25,000 USD car in Mexican Pesos
       // float carPriceUSD = 25000;
      //  float carPriceMXN = carPriceUSD * exchangeRate;

        float carPriceMXN = calculateCarPriceInMXN(carPriceUSD, exchangeRate); // example: 25000 * 19.5 = 487,500 MXN

// STEP 3 - Get 5-year historical rates to validate current price is within a realistic range
// "2020-01-02" : { "MXN" → 18.9 } --> JSON format returned by the API
        Map<String, Float> minMax = getMinMaxHistoricalRates();
        float minRate = minMax.get("min");
        float maxRate = minMax.get("max");

        System.out.println("Min: " + minMax.get("min"));
        System.out.println("Max: " + minMax.get("max"));


// STEP 4 - Calculate min and max possible car prices based on historical exchange rates

        validatePriceWithinHistoricalRange(carPriceUSD, carPriceMXN, minMax.get("min"), minMax.get("max"));

// STEP 5 - POST the car quote to JSONPlaceholder simulating saving the quote to a system
// Build the request body dynamically using HashMap
        Map<String, Object> quoteBody = new HashMap<>();
        quoteBody.put("title", "Car Quote"); // quote title
        quoteBody.put("body", "Car price in MXN: " + carPriceMXN);  // calculated price in MXN
        quoteBody.put("userId", 1); // user creating the quote

// POST the quote and validate the response
        given()
                .baseUri("https://jsonplaceholder.typicode.com") // different base URI than Frankfurter
                .contentType("application/json") // tell the API we are sending JSON
                .body(quoteBody) // attach the HashMap as the request body
                .when()
                .post("/posts")// POST to /posts endpoint
                .then()
                .statusCode(201) // 201 = Created successfully
                .body("title", equalTo("Car Quote"));// validate the title in the response matches
        //
    }
}


