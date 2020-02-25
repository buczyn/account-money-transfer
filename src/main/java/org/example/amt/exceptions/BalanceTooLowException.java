package org.example.amt.exceptions;

import lombok.Getter;

import java.math.BigDecimal;
import java.text.MessageFormat;

public class BalanceTooLowException extends AccountException {

    public BalanceTooLowException(long id, BigDecimal amount) {
        super(MessageFormat.format("Balance on account {0} is too low to transfer {1}.", id, amount));
    }
}
