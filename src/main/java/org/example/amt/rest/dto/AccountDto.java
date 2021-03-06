package org.example.amt.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class AccountDto {
    @NonNull
    private Long accountId;

    @NonNull
    private BigDecimal balance;
}
