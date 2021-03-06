package org.example.amt.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class Account {
    @NonNull
    private Long id;

    @NonNull
    private BigDecimal balance;
}
