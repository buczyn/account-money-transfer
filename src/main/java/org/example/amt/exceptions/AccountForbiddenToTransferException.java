package org.example.amt.exceptions;

import java.math.BigDecimal;
import java.text.MessageFormat;

public class AccountForbiddenToTransferException extends InvalidAmountException {

    public AccountForbiddenToTransferException(long fromAccountId, long toAccountId) {
        super(MessageFormat.format("Transfer from {0} to {1} is not allowed.", fromAccountId, toAccountId));
    }
}
