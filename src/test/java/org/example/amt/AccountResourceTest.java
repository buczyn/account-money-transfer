package org.example.amt;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.equalTo;

class AccountResourceTest extends BaseTest {

    private Application application;

    @Test
    void getAccountShouldReturnExistingAccount() {
        when().
                get("/accounts/1").
        then().
                statusCode(200).
                body(
                        "accountId", equalTo(1),
                        "balance", equalTo(100)
                );
    }

    @Test
    void getAccountShouldRespondNotFoundForNotExistingAccount() {
        when().
                get("/accounts/999}").
        then().
                statusCode(404);
    }

    @Test
    void getAccountShouldRespondNotFoundForInvalidId() {
        when().
                get("/accounts/abcd}").
        then().
                statusCode(404);
    }

    @Test
    void createAccountShouldSucceedWithPositiveBalance() {
        given().
                contentType(ContentType.JSON).
                body("{\"accountId\": 100, \"balance\": 1000.23}").
        when().
                post("/accounts").
        then().
                statusCode(201);
    }

    @Test
    void createAccountShouldSucceedWithZeroBalance() {
        given().
                contentType(ContentType.JSON).
                body("{\"accountId\": 101, \"balance\": 0}").
        when().
                post("/accounts").
        then().
                statusCode(201);
    }

    @Test
    void createAccountShouldFailWhenBalanceIsNegative() {
        given().
                contentType(ContentType.JSON).
                body("{\"accountId\": 100, \"balance\": -1.02}").
                when().
                post("/accounts").
                then().
                statusCode(409);
    }

    @Test
    void createAccountShouldFailWhenAccountIdAlreadyExists() {
        given().
                contentType(ContentType.JSON).
                body("{\"accountId\": 1, \"balance\": 1000}").
        when().
                post("/accounts").
        then().
                statusCode(409);
    }

    @Test
    void createAccountShouldRespondBadRequestForMissingBody() {
        given().
                contentType(ContentType.JSON).
                body("").
        when().
                post("/accounts").
        then().
                statusCode(400);
    }

    @Test
    void createAccountShouldRespondBadRequestForMissingAccountId() {
        given().
                contentType(ContentType.JSON).
                body("{\"balance\": 100}").
        when().
                post("/accounts").
        then().
                statusCode(400);
    }

    @Test
    void createAccountShouldRespondBadRequestForMissingBalance() {
        given().
                contentType(ContentType.JSON).
                body("{\"accountId\": 1}").
        when().
                post("/accounts").
        then().
                statusCode(400);
    }

    @Test
    void createdAccountCanBeRetrieved() {
        given().
                contentType(ContentType.JSON).
                body("{\"accountId\": 102, \"balance\": 123.45}").
        when().
                post("/accounts");

        expect().
                statusCode(200).
                body(
                        "accountId", equalTo(102),
                        "balance", equalTo(123.45f)
                ).
        when().
                get("/accounts/102");
    }


}
