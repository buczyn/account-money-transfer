package org.example.amt.exceptions;

import java.text.MessageFormat;

public class BalanceNegativeException extends AccountException {

    public BalanceNegativeException(long id) {
        super(MessageFormat.format("Balance for account {0} cannot be negative.", id));
    }
}
