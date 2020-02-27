package org.example.amt;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

abstract class ServerRunningBaseTest {

    private static Application application;

    @BeforeAll
    static void startServer() {
        application = new Application();
        application.start();
    }

    @AfterAll
    static void stopServer() {
        application.stop();
    }
}
