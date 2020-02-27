package org.example.amt;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.example.amt.exceptions.AccountException;
import org.example.amt.model.Account;
import org.example.amt.model.TransferCompleted;
import org.example.amt.model.TransferRequest;
import org.example.amt.service.AccountModule;
import org.example.amt.service.AccountService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountServiceTest {

    private static final int ACCOUNTS_COUNT = 10;
    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("1000000.00"); // 1_000_000.00
    private static final int TRANSFERS_COUNT = 100_000;
    private static final int MAX_AMOUNT = 100;
    private static final int CONCURRENT_TRANSFERS_COUNT = 20;

    private static AccountService service;
    private static Random random;

    @BeforeAll
    public static void initialize() {
        Injector injector = Guice.createInjector(new AccountModule());
        service = injector.getInstance(AccountService.class);

        random = new Random();
    }

    @Test
    public void concurrentTransfersAreHandledAndSumOnTheAccountsAreConstant() throws Exception {
        // 1. Create ACCOUNTS_COUNT accounts with fairly large balance of them, to not be worry about not enough money too much.
        final List<Long> accounts = new ArrayList<>(ACCOUNTS_COUNT);
        for (int i = 0; i < ACCOUNTS_COUNT; i++) {
            final int id = 300 + i;
            service.createAccount(id, INITIAL_BALANCE);
            accounts.add((long) id);
        }
        final BigDecimal initialBankBalance = countBankBalance(accounts);
        logAccounts(accounts, "Initially");

        // 2. Generate large number of transfers to be performed. Generate randomly accounts and amounts to transfer.
        final List<Callable<Boolean>> transfers = IntStream.rangeClosed(1, TRANSFERS_COUNT)
                .mapToObj(i -> generateTransfer(i, accounts))
                .collect(Collectors.toList());

        // 3. Run the transfers concurrently.
        ExecutorService threadPool = Executors.newFixedThreadPool(CONCURRENT_TRANSFERS_COUNT);
        List<Future<Boolean>> transferResults = threadPool.invokeAll(transfers, 30, TimeUnit.SECONDS);
        threadPool.shutdownNow();

        // 4. Check the overall amount of money in the bank is consistent.
        logAccounts(accounts, "Finally");
        final BigDecimal finalBankBalance = countBankBalance(accounts);
        assertEquals(initialBankBalance, finalBankBalance);

        // 5. Check there were transfers for each account.
        logTransfers(accounts);
        transfersStream(accounts)
                .map(Collection::size)
                .forEach(transfersCount ->
                        assertTrue(transfersCount > 0)
                );

        // 6. Check all transfers were successful.
        long allTransfersCount = transfersStream(accounts)
                .mapToLong(List::size)
                .sum();
        assertEquals(TRANSFERS_COUNT, allTransfersCount);
        for (Future<Boolean> transferResult : transferResults) {
            Boolean successful = transferResult.get();
            assertTrue(successful);
        }
    }

    private BigDecimal countBankBalance(List<Long> accounts) {
        return accountsStream(accounts)
                .map(Account::getBalance)
                .reduce(new BigDecimal("0.0"), BigDecimal::add);
    }

    private Stream<Account> accountsStream(List<Long> accounts) {
        return accounts.stream()
                .map(accountId -> {
                    try {
                        return service.getAccount(accountId).orElseThrow();
                    } catch (AccountException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private Stream<List<TransferCompleted>> transfersStream(List<Long> accounts) {
        return accounts.stream()
                .map(this::getTransfers);
    }

    private List<TransferCompleted> getTransfers(Long accountId) {
        try {
            return service.getTransfers(accountId);
        } catch (AccountException e) {
            throw new RuntimeException(e);
        }
    }

    private void logAccounts(List<Long> accounts, String header) {
        System.out.println(format(" -------- {0} --------", header));
        accountsStream(accounts)
                .forEach(account -> System.out.println(format("{0}", account)));
    }

    private void logTransfers(List<Long> accounts) {
        System.out.println(" -------- Transfers --------");
        accounts.forEach(accountId -> {
            int transfersCount = getTransfers(accountId).size();
            System.out.println(format("{0}: {1} transfers", accountId, transfersCount));
        });
    }

    Callable<Boolean> generateTransfer(int index, List<Long> accounts) {
        final long fromAccountId = accounts.get(random.nextInt(accounts.size()));
        long toAccountId;
        do {
            toAccountId = accounts.get(random.nextInt(accounts.size()));
        } while (fromAccountId == toAccountId);
        final BigDecimal amount = new BigDecimal(BigInteger.valueOf(new Random().nextInt(MAX_AMOUNT * 100) + 1), 2);
        final TransferRequest transferRequest = TransferRequest.builder()
                .receiverAccountId(toAccountId)
                .transactionId("tx" + index)
                .amount(amount)
                .build();
        return () -> {
            Exception thrown = null;
            try {
                service.transfer(fromAccountId, transferRequest);
            } catch (Exception e) {
                e.printStackTrace();
                thrown = e;
            }
//            System.out.println(format("{0}: transfer {1} from {2} to {3}.", transferRequest.getTransactionId(),
//                    transferRequest.getAmount(), accountId, transferRequest.getReceiverAccountId()));
            return isNull(thrown);
        };
    }

}
