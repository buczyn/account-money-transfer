package org.example.amt.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class AccountSettings {
    private long id;
    private BigDecimal maxTransferAmount;
}
