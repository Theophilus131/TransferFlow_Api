package com.TransactFlow.TransactFlow.data.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;
    private String senderEmail;
    private String receiverEmail;
    private BigDecimal amount;
    private String description;
    private TransactionStatus status;
    private String referenceNumber;
    private LocalDateTime createdAt;
    private BigDecimal senderBalanceBefore;
    private BigDecimal senderBalanceAfter;
    private BigDecimal receiverBalanceBefore;
    private BigDecimal receiverBalanceAfter;
}
