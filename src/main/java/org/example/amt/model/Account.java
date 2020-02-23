package org.example.amt.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Account {
    private long id;
    private BigDecimal balance;
}
