package org.example.amt.service;

import lombok.RequiredArgsConstructor;
import org.example.amt.exceptions.*;
import org.example.amt.model.Account;
import org.example.amt.model.AccountSettings;
import org.example.amt.model.TransferCompleted;
import org.example.amt.model.TransferRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
class DefaultAccountService implements AccountService {

    private final Logger log = LoggerFactory.getLogger(DefaultAccountService.class);
    private final DataSource dataSource;
    private final AccountSettingsService accountSettingsService;
    private final AccountsDao accountsDao;

    @Override
    public void createAccount(long id, BigDecimal balance) throws AccountException {
        checkNewAccountConditions(id, balance);

        try (Connection conn = dataSource.getConnection()) {
            accountsDao.createAccount(id, balance, conn);
        } catch (SQLException e) {
            throw new AccountException("Error creating account", e);
        }
    }

    private void checkNewAccountConditions(long id, BigDecimal balance) throws AccountException {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new AmountNegativeException(balance);
        }

        checkCorrectCurrencyValue(balance);

        if (getAccount(id).isPresent()) {
            throw new AccountExistsException(id);
        }
    }

    private void checkCorrectCurrencyValue(BigDecimal amount) throws AmountNotCurrencyValueException {
        if (amount.scale() > 2) {
            throw new AmountNotCurrencyValueException(amount);
        }
    }

    @Override
    public Optional<Account> getAccount(long id) throws AccountException {
        try (Connection conn = dataSource.getConnection()) {
            return accountsDao.findAccount(id, conn);
        } catch (SQLException e) {
            throw new AccountException("Cannot get account", e);
        }
    }

    @Override
    public void transfer(long fromId, TransferRequest transferRequest) throws AccountException {
        final long toId = transferRequest.getReceiverAccountId();
        final BigDecimal amount = transferRequest.getAmount();
        final String transactionId = transferRequest.getTransactionId();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            checkTransferConditions(fromId, transferRequest, conn);

            conn.setAutoCommit(false);
            accountsDao.decreaseBalance(fromId, amount, conn);
            accountsDao.increaseBalance(toId, amount, conn);
            TransferCompleted transfer = TransferCompleted.builder()
                    .transactionId(transactionId)
                    .receiverAccountId(toId)
                    .amount(amount)
                    .timestamp(Instant.now())
                    .build();
            accountsDao.storeTransfer(fromId, transfer, conn);
            conn.commit();
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                log.error("Error on transaction rollback.", e);
            }
            throw new AccountException("Error making transfer", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("Error closing connection.", e);
                }
            }
        }
    }

    private boolean isAmountOutOfLimits(BigDecimal amount, BigDecimal min, BigDecimal max) {
        return max.compareTo(amount) < 0 || min.compareTo(amount) >= 0;
    }

    private void checkTransferConditions(long fromId, TransferRequest transferRequest, Connection conn) throws AccountException, SQLException {
        long toId = transferRequest.getReceiverAccountId();
        BigDecimal amount = transferRequest.getAmount();
        String transactionId = transferRequest.getTransactionId();

        checkCorrectCurrencyValue(amount);

        AccountSettings settings = accountSettingsService.getSettings(fromId);
        if (isAmountOutOfLimits(amount, BigDecimal.ZERO, settings.getMaxTransferAmount())) {
            throw new AmountForbiddenToTransferException(amount, BigDecimal.ZERO, settings.getMaxTransferAmount());
        }
        if (!accountsDao.accountExists(toId, conn)) {
            throw new AccountNotFoundException(toId);
        }
        Optional<Account> account = accountsDao.findAccount(fromId, conn);
        if (account.isEmpty()) {
            throw new AccountNotFoundException(fromId);
        }
        if (account.get().getBalance().compareTo(amount) < 0) {
            throw new BalanceTooLowException(fromId, amount);
        }
        if (accountsDao.transactionExists(transactionId, conn)) {
            throw new TransactionAlreadyDoneException(transactionId);
        }
    }

    @Override
    public List<TransferCompleted> getTransfers(long id) throws AccountException {
        try (Connection conn = dataSource.getConnection()) {
            if (!accountsDao.accountExists(id, conn)) {
                throw new AccountNotFoundException(id);
            }
            return accountsDao.findAccountTransfers(id, conn);
        } catch (SQLException e) {
            throw new AccountException("Cannot get transfers", e);
        }
    }
}
