package org.example.amt.exceptions;

import java.text.MessageFormat;

public class TransactionAlreadyDoneException extends AccountException {

    public TransactionAlreadyDoneException(String transactionId) {
        super(MessageFormat.format("Transaction {0} is already done.", transactionId));
    }
}
