# account-money-transfer

## Task
Design and implement a RESTful API (including data model and the backing implementation) for
money transfers between accounts.

Explicit requirements:
1. You can use Java or Kotlin.
2. Keep it simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on behalf of end users.
4. You can use frameworks/libraries if you like (except Spring), but don't forget about
requirement #2 and keep it simple and avoid heavy frameworks.
5. The datastore should run in-memory for the sake of this test.
6. The final result should be executable as a standalone program (should not require a
pre-installed container/server).
7. Demonstrate with tests that the API works as expected.

Implicit requirements:
1. The code produced by you is expected to be of high quality.
2. There are no detailed requirements, use common sense.

## Running
```bash
./gradlew build
java -jar build\libs\account-money-transfer-1.0-SNAPSHOT-all.jar
```

Alternatively
```bash
./gradlew runShadow
```

## REST API
For simplicity the account properties are:
* _accountId_ - used instead of account number for simplicity, the real correct identifier should be the account number
* _balance_ - the money (without currency, assuming the same currency for all accounts), it is never null and always max dwo fraction digits

Following operations are available:
* `POST /accounts` - creating account (above mentioned account sent in request body)
  
  Request body:
  ```
  {
    "accountId": 1,
    "balance": 1237.25
  }
  ```
  
  Response body: none
  
  Response statuses:
  * 201 CREATED - success
  * 409 CONFLICT - account cannot be created because of condition not satisfied, the exact reason mentioned in header _X-Transfer-Fail-Reason_
* `GET /accounts/{id}` - retrieving account

  Request body: none
  
  Response body:
  ```
  {
    "accountId": 1,
    "balance": 1237.25
  }
  ```
  * 200 OK - for success
  * 404 NOT_FOUND - account does not exist
* `POST /accounts/{id}/transfers` with body defining the transfer
  
  Request body:
  ```
    {
      "accountTo": 2,
      "amount": 25.32,
      "transactionId": "abc123"
    }
  ```
  Response body: none
  
  Response statuses:
  * 204 NO_CONTENT - for success
  * 400 BAD_REQUEST - when request body is wrong
  * 404 NOT_FOUND - any of _id_ or _accountTo_ does not exist
  * 409 CONFLICT - transfer cannot be done because of wrong conditions, the exact reason mentioned in header _X-Fail-Reason_
* `GET /accounts/{id}/transfers` - get all successful transfers (for simplicity there is no paging and filtering)
  
  Request body: none
  
  Response body:
  ```
  {
    "transfers": [
      {
        "accountTo": 1,
        "amount": 10.01,
        "timestamp": "2020-02-26T23:42:57.687983Z",
        "tansactionId": "abc123"
      },
      {
        "accountTo": 1,
        "amount": 20.02,
        "timestamp": "2020-02-26T23:43:12.566183Z",
        "tansactionId": "def567"
      }
    ]
  }
  ```
  or
  ```
  {
    "transfers": []
  }
  ```
  Response statuses:
  * 200 OK - for success
  * 404 NOT_FOUND - account does not exist
