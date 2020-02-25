package org.example.amt.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.amt.exceptions.*;
import org.example.amt.model.TransferRequest;
import org.example.amt.rest.dto.AccountDto;
import org.example.amt.rest.dto.DtoMapper;
import org.example.amt.rest.dto.TransferDto;
import org.example.amt.rest.dto.TransfersDto;
import org.example.amt.rest.dto.TransfersDto.TransferDoneDto;
import org.example.amt.service.AccountService;
import org.mapstruct.factory.Mappers;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static javax.ws.rs.core.Response.Status.*;

@Path("accounts")
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Slf4j
public class AccountResource {

    public static final String FAIL_REASON_HEADER = "X-Fail-Reason";
    private final AccountService accountService;
    private final DtoMapper dtoMapper = Mappers.getMapper(DtoMapper.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAccount(AccountDto account) {
        if (isNull(account) || isNull(account.getAccountId()) || isNull(account.getBalance())) {
            return Response.status(BAD_REQUEST).build();
        }
        try {
            accountService.createAccount(account.getAccountId(), account.getBalance());
            return Response.status(CREATED).build();
        } catch (AccountExistsException e) {
            log.error("Account already exists", e);
            return Response.status(CONFLICT).header(FAIL_REASON_HEADER, "ACCOUNT_EXISTS").build();
        } catch (InvalidAmountException e) {
            log.error("Balance is improper", e);
            return Response.status(CONFLICT).header(FAIL_REASON_HEADER, "INVALID_BALANCE").build();
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

    @POST
    @Path("/{id}/transfers")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response makeTransfer(@PathParam("id") long id, TransferDto transfer) {
        if (isNull(transfer) || isNull(transfer.getAccountTo()) || isNull(transfer.getAmount())
                || isNull(transfer.getTransactionId())) {
            return Response.status(BAD_REQUEST).build();
        }
        try {
            final TransferRequest transferRequest = TransferRequest.builder()
                    .transactionId(transfer.getTransactionId())
                    .receiverAccountId(transfer.getAccountTo())
                    .amount(transfer.getAmount())
                    .build();
            accountService.transfer(id, transferRequest);
            return Response.noContent().build();
        } catch (AmountForbiddenToTransferException | AmountNotCurrencyValueException e) {
            log.warn("Invalid amount for transfer.", e);
            return Response.status(CONFLICT).header(FAIL_REASON_HEADER, "INVALID_AMOUNT").build();
        } catch (BalanceTooLowException e) {
            log.warn("Too low balance to make transfer", e);
            return Response.status(CONFLICT).header(FAIL_REASON_HEADER, "BALANCE_TOO_LOW").build();
        } catch (TransactionAlreadyDoneException e) {
            log.warn("Too low balance to make transfer", e);
            return Response.status(CONFLICT).header(FAIL_REASON_HEADER, "TX_ALREADY_DONE").build();
        } catch (AccountNotFoundException e) {
            log.error("Account not found", e);
            return Response.status(NOT_FOUND).build();
        } catch (AccountException e) {
            log.error("Server error", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/{id}/transfers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransfers(@PathParam("id") long id) {
        try {
            List<TransferDoneDto> transfers = accountService.getTransfers(id)
                    .stream()
                    .map(dtoMapper::toTransferDoneDto)
                    .collect(Collectors.toList());
            return Response.ok(new TransfersDto(transfers)).build();
        } catch (AccountNotFoundException e) {
            log.error("Account not found", e);
            return Response.status(NOT_FOUND).build();
        } catch (AccountException e) {
            log.error("Server error", e);
            return Response.serverError().build();
        }
    }
}
