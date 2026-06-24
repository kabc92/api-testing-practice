# API Test Automation Framework

REST API test automation framework built from scratch using Java, RestAssured, and JUnit 5.

## Tech Stack
- Java 21
- RestAssured 6.0.0
- JUnit Jupiter 5.11.4
- Jackson Databind 2.21.4
- Maven 3.9.16
- Git / GitHub

## Project Structure
```
src/test/java/
    base/
        BaseTest.java           → Parent class with RequestSpecification setup for JSONPlaceholder
        BaseFinancialTest.java  → Extends BaseTest, overrides baseUri for Frankfurter API
    tests/
        PrimerApiTest.java      → GET, POST, PUT, PATCH, DELETE vs JSONPlaceholder
        AuthTest.java           → API Key authentication
        RequestSpecTest.java    → RequestSpecification with RequestSpecBuilder
        DynamicPayloadTest.java → Dynamic POST body using HashMap and Jackson
        FrankfurterTest.java    → Financial API tests vs Frankfurter exchange rate API
```

## Test Coverage

### JSONPlaceholder Tests
- GET, POST, PUT, PATCH, DELETE requests
- Response body validation with JsonPath
- Header validation
- Status code validation
- Negative test cases
- API Key authentication
- Dynamic payload construction using HashMap and Jackson Databind
- Request centralization using RequestSpecification and RequestSpecBuilder

### Frankfurter Financial API Tests
- Latest exchange rate validation (EUR base)
- Specific currency rate extraction with query parameters
- Historical rate validation for a specific date
- Historical MXN rate range validation (2020–2025) using `Map<String, Map<String, Float>>`
- USD to MXN currency conversion calculation
- 5-year MXN rate range validation with manual min/max loop
- Sorted MXN rates validation using `ArrayList<Float>` and manual Bubble Sort
- Duplicate date and rate validation using `Set<String>` and `HashSet<Float>`
- End-to-end car price quote: GET rate → calculate price → validate historical range → POST quote to JSONPlaceholder

## OOP Concepts Applied
- Inheritance — BaseTest → BaseFinancialTest → FrankfurterTest (3-level chain)
- Encapsulation — private helper methods (`getHistoricalRates5Yrs()`, `getMXNRatesList()`)
- Access modifiers — `protected` for shared fields across packages, `private` for internal helpers
- Interface vs implementation — `Map`/`HashMap`, `List`/`ArrayList`, `Set`/`HashSet`, `RequestSpecification`/`RequestSpecBuilder`

## Java Collections Used
| Collection | Implementation | Used For |
|------------|---------------|----------|
| `Map<String, Map<String, Float>>` | `HashMap` | Extract nested JSON (date → currency) |
| `Map<String, Object>` | `HashMap` | Build dynamic POST body |
| `ArrayList<Float>` | `ArrayList` | Store and sort MXN rate values |
| `Set<String>` | via `keySet()` | Validate unique dates |
| `Set<Float>` | `HashSet` | Detect duplicate rate values |

## APIs Tested
- JSONPlaceholder — https://jsonplaceholder.typicode.com
- Frankfurter v1 — https://api.frankfurter.dev

## How to Run
```bash
mvn test
```

## Author
Kevin Barragán — github.com/kabc92