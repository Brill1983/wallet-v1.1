package ru.brill.transactions;

import org.springframework.http.ResponseEntity;
import ru.brill.transactions.dto.TransactionDto;

public interface TransactionsService {
    ResponseEntity<Object> getTransactionsByWalletId(Long userId, Long walletId);

    ResponseEntity<Object> getTransactionsByUserId(Long userId);

    ResponseEntity<Object> postTransaction(Long userId, TransactionDto transactionDto);
}
