package com.TransactFlow.TransactFlow.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequestDto {
    private BigDecimal amount;
    private String description;
    private String receiverEmail;
}
