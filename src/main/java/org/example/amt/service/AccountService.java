package org.example.amt.service;

import org.example.amt.exceptions.AccountException;
import org.example.amt.model.Account;
import org.example.amt.model.TransferCompleted;
import org.example.amt.model.TransferRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {

    void createAccount(long id, BigDecimal balance) throws AccountException;

    Optional<Account> getAccount(long id) throws AccountException;

    void transfer(long fromId, TransferRequest transferRequest) throws AccountException;

    List<TransferCompleted> getTransfers(long id) throws AccountException;
}
