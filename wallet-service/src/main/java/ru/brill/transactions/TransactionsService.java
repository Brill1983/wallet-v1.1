package ru.brill.transactions;

import ru.brill.transactions.dto.TransactionDto;
import ru.brill.transactions.dto.TransactionDtoOut;
import ru.brill.transactions.dto.TransactionForWallet;

import java.util.List;

public interface TransactionsService {
    List<TransactionForWallet> getTransactionsByWalletId(Long userId, Long walletId, Integer from, Integer size);

    TransactionDtoOut postTransaction(Long userId, TransactionDto transactionDto);
}
