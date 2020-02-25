package org.example.amt.exceptions;

import java.math.BigDecimal;
import java.text.MessageFormat;

public class AmountForbiddenToTransferException extends InvalidAmountException {

    public AmountForbiddenToTransferException(BigDecimal amount, BigDecimal min, BigDecimal max) {
        super(MessageFormat.format("Amount {0} must be within range {1}-{2}.", amount, min, max));
    }
}
