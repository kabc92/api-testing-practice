package base;

import io.restassured.builder.RequestSpecBuilder;
import org.junit.jupiter.api.BeforeEach;

public class BaseFinancialTest extends BaseTest{

    @Override
    @BeforeEach
    public void setUp(){

        requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://api.frankfurter.app")
                .build();

    }
}
