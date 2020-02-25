package org.example.amt.service;

import org.example.amt.model.Account;
import org.example.amt.model.TransferCompleted;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

interface AccountsDao {

    boolean accountExists(long id, Connection conn) throws SQLException;

    Optional<Account> findAccount(long id, Connection conn) throws SQLException;

    void createAccount(long id, BigDecimal balance, Connection conn) throws SQLException;

    void increaseBalance(long accountId, BigDecimal amount, Connection conn) throws SQLException;

    void decreaseBalance(long accountId, BigDecimal amount, Connection conn) throws SQLException;

    boolean transactionExists(String transactionId, Connection conn) throws SQLException;

    List<TransferCompleted> findAccountTransfers(long id, Connection conn) throws SQLException;

    void storeTransfer(long fromId, TransferCompleted transfer, Connection conn) throws SQLException;
}
