package org.example.amt.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TransferDto {
    @NonNull
    private Long accountTo;

    @NonNull
    private BigDecimal amount;

    @NonNull
    private String transactionId;
}
