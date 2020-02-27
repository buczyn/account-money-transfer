package org.example.amt.service;

import lombok.extern.slf4j.Slf4j;
import org.example.amt.model.Account;
import org.example.amt.model.TransferCompleted;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.unmodifiableList;

@Slf4j
class H2AccountsDao implements AccountsDao {

    @Override
    public boolean accountExists(long id, Connection conn) throws SQLException {
        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT count(*) FROM accounts WHERE account_id = " + id)) {
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        }
        return false;
    }

    @Override
    public Optional<Account> findAccount(long id, Connection conn) throws SQLException {
        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT amount FROM accounts WHERE account_id = " + id)) {
            if (resultSet.next()) {
                BigDecimal amount = resultSet.getBigDecimal("amount");
                return Optional.of(Account.builder()
                        .id(id)
                        .balance(amount)
                        .build());
            }
        }
        return Optional.empty();
    }

    @Override
    public void createAccount(long id, BigDecimal balance, Connection conn) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO accounts (account_id, amount) VALUES (?, ?)")) {
            statement.setLong(1, id);
            statement.setBigDecimal(2, balance);
            statement.executeUpdate();
        }
    }

    @Override
    public void increaseBalance(long accountId, BigDecimal amount, Connection conn) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "UPDATE accounts SET amount = amount + ? WHERE account_id = ?")) {
            statement.setBigDecimal(1, amount);
            statement.setLong(2, accountId);
            statement.executeUpdate();
        }
    }

    @Override
    public void decreaseBalance(long accountId, BigDecimal amount, Connection conn) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "UPDATE accounts SET amount = amount - ? WHERE account_id = ?")) {
            statement.setBigDecimal(1, amount);
            statement.setLong(2, accountId);
            statement.executeUpdate();
        }
    }

    @Override
    public boolean transactionExists(String transactionId, Connection conn) throws SQLException {
        try (Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(
                "SELECT count(*) FROM transfers WHERE transaction_id = '" + transactionId + "'")) {
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        }
        return false;
    }

    @Override
    public List<TransferCompleted> findAccountTransfers(long id, Connection conn) throws SQLException {
        final List<TransferCompleted> result = new ArrayList<>();
        try (Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(
                "SELECT transaction_id, receiver_id, amount, done_at FROM transfers WHERE account_id = " + id + " ORDER BY done_at")) {
            while (resultSet.next()) {
                String transactionId = resultSet.getString("transaction_id");
                long receiverId = resultSet.getLong("receiver_id");
                BigDecimal amount = resultSet.getBigDecimal("amount");
                Timestamp timestamp = resultSet.getTimestamp("done_at");
                result.add(TransferCompleted.builder()
                        .transactionId(transactionId)
                        .receiverAccountId(receiverId)
                        .amount(amount)
                        .timestamp(timestamp.toInstant())
                        .build());
            }
        }
        return unmodifiableList(result);
    }

    @Override
    public void storeTransfer(long id, TransferCompleted transfer, Connection conn) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO transfers (transaction_id, account_id, receiver_id, amount, done_at) VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, transfer.getTransactionId());
            statement.setLong(2, id);
            statement.setLong(3, transfer.getReceiverAccountId());
            statement.setBigDecimal(4, transfer.getAmount());
            statement.setTimestamp(5, Timestamp.from(transfer.getTimestamp()));
            statement.executeUpdate();
        }
    }
}
