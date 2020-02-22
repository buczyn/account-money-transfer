package org.example.amt.rest;

import org.glassfish.jersey.server.ResourceConfig;

class BankApplication extends ResourceConfig {
    BankApplication() {
        packages(BankApplication.class.getPackageName());
    }

}
