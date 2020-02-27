package org.example.amt;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;

class AccountRestApiTest extends ServerRunningBaseTest {

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
                get("/accounts/999").
        then().
                statusCode(404);
    }

    @Test
    void getAccountShouldRespondNotFoundForInvalidId() {
        when().
                get("/accounts/abcd").
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
                statusCode(409).
                header("X-Fail-Reason", "INVALID_BALANCE");
    }

    @Test
    void createAccountShouldFailWhenBalanceHasTooManyFractionDigits() {
        given().
                contentType(ContentType.JSON).
                body("{\"accountId\": 100, \"balance\": 1000.234}").
        when().
                post("/accounts").
        then().
                statusCode(409).
                header("X-Fail-Reason", "INVALID_BALANCE");
    }

    @Test
    void createAccountShouldFailWhenAccountIdAlreadyExists() {
        given().
                contentType(ContentType.JSON).
                body("{\"accountId\": 1, \"balance\": 1000}").
        when().
                post("/accounts").
        then().
                statusCode(409).
                header("X-Fail-Reason", "ACCOUNT_EXISTS");
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

    @Test
    void makeTransferShouldRespondBadRequestForMissingBody() {
        given().
                contentType(ContentType.JSON).
                body("").
        when().
                post("/accounts/1/transfers").
        then().
                statusCode(400);
    }

    @Test
    void makeTransferShouldRespondBadRequestForMissingTransactionId() {
        given().
                contentType(ContentType.JSON).
                body("{\"accountTo\": 1, \"amount\": 10}").
        when().
                post("/accounts/1/transfers").
        then().
                statusCode(400);
    }

    @Test
    void makeTransferShouldRespondBadRequestForMissingAccountTo() {
        given().
                contentType(ContentType.JSON).
                body("{\"transactionId\": \"abc123\", \"amount\": 10}").
        when().
                post("/accounts/1/transfers").
        then().
                statusCode(400);
    }

    @Test
    void makeTransferShouldRespondBadRequestForMissingAmount() {
        given().
                contentType(ContentType.JSON).
                body("{\"transactionId\": \"abc123\", \"accountTo\": 1}").
        when().
                post("/accounts/1/transfers").
        then().
                statusCode(400);
    }

    @Test
    void makeTransferShouldRespondNotFoundForNotExistingAccount() {
        given().
                contentType(ContentType.JSON).
                body("{\"transactionId\": \"abc123\", \"accountTo\": 1, \"amount\": 10}").
        when().
                post("/accounts/999/transfers").
        then().
                statusCode(404);
    }

    @Test
    void makeTransferShouldRespondNotFoundForNotExistingAccountTo() {
        given().
                contentType(ContentType.JSON).
                body("{\"transactionId\": \"abc123\", \"accountTo\": 999, \"amount\": 10}").
        when().
                post("/accounts/1/transfers").
        then().
                statusCode(404);
    }

    @Test
    void makeTransferShouldFailWhenThereIsNotEnoughBalanceOnAccount() {
        given().
                contentType(ContentType.JSON).
                body("{\"transactionId\": \"abc123\", \"accountTo\": 1, \"amount\": 100.01}").
        when().
                post("/accounts/10/transfers").
        then().
                statusCode(409).
                header("X-Fail-Reason", "BALANCE_TOO_LOW");
    }

    @Test
    void makeTransferShouldFailWhenAmountIsNegative() {
        given().
                contentType(ContentType.JSON).
                body("{\"transactionId\": \"abc123\", \"accountTo\": 2, \"amount\": -0.01}").
        when().
                post("/accounts/1/transfers").
        then().
                statusCode(409).
                header("X-Fail-Reason", "INVALID_AMOUNT");
    }

    @Test
    void makeTransferShouldFailWhenAmountIsZero() {
        given().
                contentType(ContentType.JSON).
                body("{\"transactionId\": \"abc123\", \"accountTo\": 2, \"amount\": 0}").
        when().
                post("/accounts/1/transfers").
        then().
                statusCode(409).
                header("X-Fail-Reason", "INVALID_AMOUNT");
    }

    @Test
    void makeTransferShouldFailWhenAmountIsTooBig() {
        given().
                contentType(ContentType.JSON).
                body("{\"transactionId\": \"abc123\", \"accountTo\": 1, \"amount\": 1000.01}").
                header("X-Fail-Reason", "INVALID_AMOUNT").
        when().
                post("/accounts/14/transfers").
        then().
                statusCode(409);
    }

    @Test
    void makeTransferShouldFailWhenAmountHasTooManyFractionDigits() {
        given().
                contentType(ContentType.JSON).
                body("{\"transactionId\": \"abc123\", \"accountTo\": 1, \"amount\": 10.011}").
        when().
                post("/accounts/14/transfers").
        then().
                statusCode(409).
                header("X-Fail-Reason", "INVALID_AMOUNT");
    }

    @Test
    void makeTransferShouldFailWhenExistingTransactionIdIsUsed() {
        given().
                contentType(ContentType.JSON).
                body("{\"transactionId\": \"abc130\", \"accountTo\": 2, \"amount\": 10.01}").
        when().
                post("/accounts/14/transfers").
        then().
                statusCode(204);

        expect().
                statusCode(409).
                header("X-Fail-Reason", "TX_ALREADY_DONE").
        given().
                contentType(ContentType.JSON).
                body("{\"transactionId\": \"abc130\", \"accountTo\": 2, \"amount\": 10.01}").
        when().
                post("/accounts/15/transfers");
    }

    @Test
    void makeTransferShouldSucceedWhenAllConditionsAreMet() {
        given().
                contentType(ContentType.JSON).
                body("{\"transactionId\": \"abc123\", \"accountTo\": 11, \"amount\": 10}").
        when().
                post("/accounts/10/transfers").
        then().
                statusCode(204);
    }

    @Test
    void accountsBalancesAfterMakingTransferAreCorrect() {
        given().
                contentType(ContentType.JSON).
                body("{\"transactionId\": \"abc124\", \"accountTo\": 13, \"amount\": 10.01}").
        when().
                post("/accounts/12/transfers");

        expect().
                statusCode(200).
                body(
                        "accountId", equalTo(12),
                        "balance", equalTo(109.99f)
                ).
        when().
                get("/accounts/12");

        expect().
                statusCode(200).
                body(
                        "accountId", equalTo(13),
                        "balance", equalTo(140.01f)
                ).
        when().
                get("/accounts/13");
    }

    @Test
    void getTransfersShouldBeEmptyWhenThereAreNoTransfers() {
        when().
                get("/accounts/1/transfers").
        then().
                statusCode(200).
                body("transfers", hasSize(0));
    }

    @Test
    void getTransfersShouldRespondNotFoundForNotExistingAccount() {
        when().
                get("/accounts/999/transfers").
        then().
                statusCode(404);
    }

    @Test
    void getTransfersShouldRespondNotFoundForInvalidId() {
        when().
                get("/accounts/abcd/transfers").
        then().
                statusCode(404);
    }

    @Test
    void getTransfersShouldReturnTransfers() {
        given().
                contentType(ContentType.JSON).
                body("{\"transactionId\": \"abc125\", \"accountTo\": 15, \"amount\": 10.01}").
        when().
                post("/accounts/16/transfers").
        then().
                statusCode(204);
        given().
                contentType(ContentType.JSON).
                body("{\"transactionId\": \"abc126\", \"accountTo\": 17, \"amount\": 20.02}").
        when().
                post("/accounts/16/transfers").
        then().
                statusCode(204);

        expect().
                statusCode(200).
                body(
                        "transfers", hasSize(2),

                        "transfers[0].transactionId", equalTo("abc125"),
                        "transfers[0].accountTo", equalTo(15),
                        "transfers[0].amount", equalTo(10.01f),
                        "transfers[0].timestamp", notNullValue(),

                        "transfers[1].transactionId", equalTo("abc126"),
                        "transfers[1].accountTo", equalTo(17),
                        "transfers[1].amount", equalTo(20.02f),
                        "transfers[1].timestamp", notNullValue()
                ).
        when().
                get("/accounts/16/transfers").then().log();
    }

}
