package com.TransactFlow.TransactFlow.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalanceDto {
    private String email;
    private BigDecimal balance;
    private String currency;
}
