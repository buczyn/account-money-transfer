package org.example.amt.exceptions;

import java.math.BigDecimal;
import java.text.MessageFormat;

public class AmountNegativeException extends InvalidAmountException {

    public AmountNegativeException(BigDecimal amount) {
        super(MessageFormat.format("Amount {0} cannot be negative.", amount));
    }
}
