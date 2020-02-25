package org.example.amt.exceptions;

import java.math.BigDecimal;
import java.text.MessageFormat;

public class AmountNotCurrencyValueException extends InvalidAmountException {

    public AmountNotCurrencyValueException(BigDecimal amount) {
        super(MessageFormat.format("Amount {0} is not currency value.", amount));
    }
}
