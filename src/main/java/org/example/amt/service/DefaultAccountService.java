package org.example.amt.service;

import org.example.amt.exceptions.AccountException;
import org.example.amt.exceptions.AccountExistsException;
import org.example.amt.exceptions.BalanceNegativeException;
import org.example.amt.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Optional;

class DefaultAccountService implements AccountService {

    private final Logger log = LoggerFactory.getLogger(DefaultAccountService.class);
    private final DataSource dataSource;

    @Inject
    DefaultAccountService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createAccount(long id, BigDecimal balance) throws AccountException {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BalanceNegativeException(id);
        }
        if (getAccount(id).isPresent()) {
            throw new AccountExistsException(id);
        }
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO accounts (account_id, amount) VALUES (?, ?)");
            statement.setLong(1, id);
            statement.setBigDecimal(2, balance);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new AccountException("Error creating account", e);
        }
    }

    @Override
    public Optional<Account> getAccount(long id) throws AccountException {
        try (Connection conn = dataSource.getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT amount FROM accounts WHERE account_id = " + id);
            if (resultSet.next()) {
                BigDecimal amount = resultSet.getBigDecimal("amount");
                return Optional.of(Account.builder()
                        .id(id)
                        .balance(amount)
                        .build());
            }
        } catch (SQLException e) {
            throw new AccountException("Cannot get account", e);
        }
        return Optional.empty();
    }
}
