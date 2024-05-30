package ru.brill.transactions;

import org.springframework.http.ResponseEntity;
import ru.brill.transactions.dto.TransactionDto;
import ru.brill.transactions.dto.TransactionDtoOut;
import ru.brill.transactions.dto.TransactionForWallet;

import java.util.List;

public interface TransactionsService {
    List<TransactionForWallet> getTransactionsByWalletId(Long userId, Long walletId);

    ResponseEntity<Object> getTransactionsByUserId(Long userId);

    TransactionDtoOut postTransaction(Long userId, TransactionDto transactionDto);
}
