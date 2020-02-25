package org.example.amt.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class TransfersDto {

    @NonNull
    private List<TransferDoneDto> transfers;

    @Data
    @NoArgsConstructor
    public static class TransferDoneDto {
        @NonNull
        private Long accountTo;

        @NonNull
        private BigDecimal amount;

        @NonNull
        private Instant timestamp;

        @NonNull
        private String transactionId;
    }
}
