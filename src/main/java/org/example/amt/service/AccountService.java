package org.example.amt.service;

import org.example.amt.exceptions.AccountException;
import org.example.amt.model.Account;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountService {

    void createAccount(long id, BigDecimal balance) throws AccountException;

    Optional<Account> getAccount(long id) throws AccountException;
}
