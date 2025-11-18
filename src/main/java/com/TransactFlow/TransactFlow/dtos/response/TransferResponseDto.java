package com.TransactFlow.TransactFlow.dtos.response;

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
public class TransferResponseDto {
    private String transactionId;
    private String referenceNumber;
    private String senderEmail;
    private String receiverEmail;
    private BigDecimal amount;
    private String description;
    private TransactionStatus status;
    private BigDecimal newBalance;
    private LocalDateTime timestamp;
    private String message;

}
