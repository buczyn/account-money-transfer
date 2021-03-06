package org.example.amt.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value
@Builder
public class TransferRequest {
    @NonNull
    private String transactionId;

    @NonNull
    private Long receiverAccountId;

    @NonNull
    private BigDecimal amount;
}
