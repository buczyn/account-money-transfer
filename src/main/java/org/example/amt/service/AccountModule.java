package org.example.amt.service;

import com.google.inject.AbstractModule;

public class AccountModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AccountService.class).to(DefaultAccountService.class);
    }
}
