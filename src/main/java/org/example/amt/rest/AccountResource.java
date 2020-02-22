package org.example.amt.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("accounts")
public class AccountResource {

    @GET
    @Path("hello")
    @Produces("plain/text")
    public String getHello() {
        return "Hello World";
    }
}
