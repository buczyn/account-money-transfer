package org.example.amt.rest;

import org.example.amt.service.AccountService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("accounts")
public class AccountResource {

    private AccountService accountService;

    @Inject
    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    @Path("hello")
    @Produces("plain/text")
    public String getHello() {
        return accountService.hello();
    }
}
