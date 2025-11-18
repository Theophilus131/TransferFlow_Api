package com.TransactFlow.TransactFlow.dtos;


import com.TransactFlow.TransactFlow.data.model.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionHistoryDto {
    private String transactionId;
    private String referenceNumber;
    private String otherParty;
    private BigDecimal amount;
    private String description;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private TransactionStatus status;
    private LocalDateTime timestamp;
}
