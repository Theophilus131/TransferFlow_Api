package com.TransactFlow.TransactFlow.controller;


import com.TransactFlow.TransactFlow.dtos.TransactionHistoryDto;
import com.TransactFlow.TransactFlow.dtos.WalletBalanceDto;
import com.TransactFlow.TransactFlow.dtos.request.TransferRequestDto;
import com.TransactFlow.TransactFlow.dtos.response.TransferResponseDto;
import com.TransactFlow.TransactFlow.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/wallet")
    public ResponseEntity<com.TransactFlow.TransactFlow.dtos.WalletBalanceDto> getWalletBalance(Authentication authentication){
        String email = authentication.getName();
        com.TransactFlow.TransactFlow.dtos.WalletBalanceDto balance = transactionService.getWalletBalance(email);
        return ResponseEntity.ok(balance);
    }

    @RequestMapping("/transfer")
    public ResponseEntity<TransferResponseDto> transferMoney(
            @RequestBody TransferRequestDto request,
            Authentication authentication
            ){

        String senderEmail = authentication.getName();

        TransferResponseDto response = transactionService.transferMoney(senderEmail, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionHistoryDto>> getTransactionHistory(Authentication authentication){
        String email = authentication.getName();
        List<TransactionHistoryDto> history = transactionService.getTransactionHistory(email);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/sent")
    public ResponseEntity<List<TransactionHistoryDto>> getSentTransactions(Authentication authentication){
        String email = authentication.getName();
        List<TransactionHistoryDto> sent = transactionService.getSentTransactions(email);
        return ResponseEntity.ok(sent);
    }

    @GetMapping("/received")
    public ResponseEntity<List<TransactionHistoryDto>> getReceivedTransactions(Authentication authentication){
        String email = authentication.getName();
        List<TransactionHistoryDto> received = transactionService.getReceivedTransactions(email);
        return ResponseEntity.ok(received);
    }

    @GetMapping("/balance")
    public ResponseEntity<WalletBalanceDto> getBalance(Authentication authentication){
        String email = authentication.getName();
         WalletBalanceDto balance = transactionService.getWalletBalance(email);
        return ResponseEntity.ok(balance);
    }



}
