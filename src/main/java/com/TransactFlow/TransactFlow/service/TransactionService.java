package com.TransactFlow.TransactFlow.service;

import com.TransactFlow.TransactFlow.data.model.Transaction;
import com.TransactFlow.TransactFlow.data.model.TransactionStatus;
import com.TransactFlow.TransactFlow.data.model.User;
import com.TransactFlow.TransactFlow.dtos.TransactionHistoryDto;
import com.TransactFlow.TransactFlow.dtos.WalletBalanceDto;
import com.TransactFlow.TransactFlow.dtos.request.TransferRequestDto;
import com.TransactFlow.TransactFlow.dtos.response.TransferResponseDto;
import com.TransactFlow.TransactFlow.exceptions.DuplicateTransactionException;
import com.TransactFlow.TransactFlow.exceptions.InsufficientBalanceException;
import com.TransactFlow.TransactFlow.exceptions.InvalidTransactionException;
import com.TransactFlow.TransactFlow.exceptions.UserNotFoundException;
import com.TransactFlow.TransactFlow.repository.TransactionRepository;
import com.TransactFlow.TransactFlow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;


    @Transactional
    public TransferResponseDto transferMoney(String senderEmail, TransferRequestDto request) {
        log.info("Processing transfer from {} to {} ", senderEmail, request.getReceiverEmail());

        validateTransferRequest(senderEmail, request);

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(()-> new UserNotFoundException("Sender not found"));

        User receiver = userRepository.findByEmail(request.getReceiverEmail())
                .orElseThrow(()-> new UserNotFoundException("Receiver not found"));

        if(!receiver.isActive()){
            throw new InvalidTransactionException("Receiver account is inactive");
        }

        if(sender.getBalance().compareTo(request.getAmount()) < 0){
            throw new InsufficientBalanceException(
                    String.format("Insufficient balance. Sender balance is %s",
                            sender.getBalance(),
                            request.getAmount())
            );

        }


        String referenceNumber = generateReferenceNumber();

        if (transactionRepository.existsByReferenceNumber(referenceNumber)) {
            throw new DuplicateTransactionException("Duplicate transaction detected");
        }

        // Record balances before transaction
        BigDecimal senderBalanceBefore = sender.getBalance();
        BigDecimal receiverBalanceBefore = receiver.getBalance();

        // Perform the transfer
        BigDecimal newSenderBalance = sender.getBalance().subtract(request.getAmount());
        BigDecimal newReceiverBalance = receiver.getBalance().add(request.getAmount());

        sender.setBalance(newSenderBalance);
        sender.setUpdateDate(LocalDateTime.now());

        receiver.setBalance(newReceiverBalance);
        receiver.setUpdateDate(LocalDateTime.now());

        //  Save updated balances
        userRepository.save(sender);
        userRepository.save(receiver);

        //  Create transaction record
        Transaction transaction = Transaction.builder()
                .senderEmail(senderEmail)
                .receiverEmail(request.getReceiverEmail())
                .amount(request.getAmount())
                .description(request.getDescription())
                .status(TransactionStatus.SUCCESS)
                .referenceNumber(referenceNumber)
                .senderBalanceBefore(senderBalanceBefore)
                .senderBalanceAfter(newSenderBalance)
                .receiverBalanceBefore(receiverBalanceBefore)
                .receiverBalanceAfter(newReceiverBalance)
                .createdAt(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Transfer successful. Reference: {}", referenceNumber);

      
        return TransferResponseDto.builder()
                .transactionId(savedTransaction.getId())
                .referenceNumber(referenceNumber)
                .senderEmail(senderEmail)
                .receiverEmail(request.getReceiverEmail())
                .amount(request.getAmount())
                .description(request.getDescription())
                .status(TransactionStatus.SUCCESS)
                .newBalance(newSenderBalance)
                .timestamp(savedTransaction.getCreatedAt())
                .message("Transfer successful")
                .build();
    }


    public WalletBalanceDto getWalletBalance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return WalletBalanceDto.builder()
                .email(email)
                .balance(user.getBalance())
                .currency("USD")
                .build();
    }


    public List<TransactionHistoryDto> getTransactionHistory(String email) {
        // Get all transactions where user is sender or receiver
        List<Transaction> transactions = transactionRepository
                .findBySenderEmailOrReceiverEmailOrderByCreatedAtDesc(email, email);

        List<TransactionHistoryDto> history = new ArrayList<>();

        for (Transaction transaction : transactions) {
            boolean isSender = transaction.getSenderEmail().equals(email);

            TransactionHistoryDto dto = TransactionHistoryDto.builder()
                    .transactionId(transaction.getId())
                    .referenceNumber(transaction.getReferenceNumber())
//                    .type(isSender ? "SENT" : "RECEIVED")
                    .otherParty(isSender ? transaction.getReceiverEmail() : transaction.getSenderEmail())
                    .amount(transaction.getAmount())
                    .description(transaction.getDescription())
                    .status(transaction.getStatus())
                    .balanceBefore(isSender ? transaction.getSenderBalanceBefore() : transaction.getReceiverBalanceBefore())
                    .balanceAfter(isSender ? transaction.getSenderBalanceAfter() : transaction.getReceiverBalanceAfter())
                    .timestamp(transaction.getCreatedAt())
                    .build();

            history.add(dto);
        }

        return history;
    }


    public List<TransactionHistoryDto> getSentTransactions(String email) {
        List<Transaction> transactions = transactionRepository.findBySenderEmailOrderByCreatedAtDesc(email);
        return mapToHistoryDto(transactions, email, true);
    }


    public List<TransactionHistoryDto> getReceivedTransactions(String email) {
        List<Transaction> transactions = transactionRepository.findByReceiverEmailOrderByCreatedAtDesc(email);
        return mapToHistoryDto(transactions, email, false);
    }


    private void validateTransferRequest(String senderEmail, TransferRequestDto request) {
        // Check if receiver email is provided
        if (request.getReceiverEmail() == null || request.getReceiverEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Receiver email is required");
        }


        if (request.getAmount() == null) {
            throw new IllegalArgumentException("Amount is required");
        }

        // Check if amount is positive
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must be greater than zero");
        }

        // Check if amount has max 2 decimal places (for currency)
        if (request.getAmount().scale() > 2) {
            throw new InvalidTransactionException("Amount can have maximum 2 decimal places");
        }

        // Check if sender is trying to send money to themselves
        if (senderEmail.equals(request.getReceiverEmail())) {
            throw new InvalidTransactionException("Cannot transfer money to yourself");
        }

        // Check minimum transfer amount
        if (request.getAmount().compareTo(new BigDecimal("0.01")) < 0) {
            throw new InvalidTransactionException("Minimum transfer amount is 0.01");
        }

        // Check maximum transfer amount (optional security measure)
        if (request.getAmount().compareTo(new BigDecimal("100000.00")) > 0) {
            throw new InvalidTransactionException("Maximum transfer amount is 100,000.00");
        }
    }


    private String generateReferenceNumber() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }


    private List<TransactionHistoryDto> mapToHistoryDto(List<Transaction> transactions, String userEmail, boolean isSent) {
        List<TransactionHistoryDto> history = new ArrayList<>();

        for (Transaction transaction : transactions) {
            TransactionHistoryDto dto = TransactionHistoryDto.builder()
                    .transactionId(transaction.getId())
                    .referenceNumber(transaction.getReferenceNumber())
//                    .type(isSent ? "SENT" : "RECEIVED")
                    .otherParty(isSent ? transaction.getReceiverEmail() : transaction.getSenderEmail())
                    .amount(transaction.getAmount())
                    .description(transaction.getDescription())
                    .status(transaction.getStatus())
                    .balanceBefore(isSent ? transaction.getSenderBalanceBefore() : transaction.getReceiverBalanceBefore())
                    .balanceAfter(isSent ? transaction.getSenderBalanceAfter() : transaction.getReceiverBalanceAfter())
                    .timestamp(transaction.getCreatedAt())
                    .build();

            history.add(dto);
        }

        return history;
    }
}


