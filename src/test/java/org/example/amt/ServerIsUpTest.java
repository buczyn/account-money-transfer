package org.example.amt;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;

class ServerIsUpTest extends BaseTest {

    private Application application;

    @Test
    void testServerIsUp() {
        when().
                get("/accounts/hello").
        then().
                statusCode(200).
                body(Matchers.equalTo("Hello World"));

    }


}
