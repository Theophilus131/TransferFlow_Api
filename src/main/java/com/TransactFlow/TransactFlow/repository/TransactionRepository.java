package com.TransactFlow.TransactFlow.repository;

import com.TransactFlow.TransactFlow.data.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findBySenderEmailOrderByCreatedAtDesc(String senderEmail);
    List<Transaction> findByReceiverEmailOrderByCreatedAtDesc(String senderEmail);

    List<Transaction> findBySenderEmailOrReceiverEmailOrderByCreatedAtDesc(String senderEmail, String receiverEmail);

    boolean existsByReferenceNumber(String referenceNumber);


}
