import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {

    //INSTANCE VARIABLE
    RequestSpecification requestSpec;

    @BeforeEach
    public void setUp(){

         requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://jsonplaceholder.typicode.com")
                .build();

    }



}
