package org.example.amt.rest;

import org.example.amt.exceptions.AccountException;
import org.example.amt.exceptions.AccountExistsException;
import org.example.amt.exceptions.BalanceNegativeException;
import org.example.amt.rest.dto.AccountDto;
import org.example.amt.rest.dto.DtoMapper;
import org.example.amt.service.AccountService;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.util.Objects.isNull;
import static javax.ws.rs.core.Response.Status.*;

@Path("accounts")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private AccountService accountService;
    private DtoMapper dtoMapper = Mappers.getMapper(DtoMapper.class);

    @Inject
    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAccount(AccountDto account) {
        if (isNull(account) || isNull(account.getAccountId()) || isNull(account.getBalance())) {
            return Response.status(BAD_REQUEST).build();
        }
        try {
            accountService.createAccount(account.getAccountId(), account.getBalance());
            return Response.status(CREATED).build();
        } catch (BalanceNegativeException | AccountExistsException e) {
            log.error("", e);
            return Response.status(CONFLICT).build();
        } catch (AccountException e) {
            log.error("Server error", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@PathParam("id") long id) {
        try {
            return accountService.getAccount(id)
                    .map(dtoMapper::toAccountDto)
                    .map(Response::ok)
                    .map(Response.ResponseBuilder::build)
                    .orElseGet(() -> Response.status(NOT_FOUND).build());
        } catch (AccountException e) {
            log.error("Server error", e);
            return Response.serverError().build();
        }
    }
}
