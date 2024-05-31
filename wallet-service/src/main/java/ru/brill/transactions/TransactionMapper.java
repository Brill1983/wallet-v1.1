package ru.brill.transactions;

import lombok.experimental.UtilityClass;
import ru.brill.transactions.dto.TransactionDtoOut;
import ru.brill.transactions.dto.TransactionForWallet;
import ru.brill.transactions.model.WalletTransaction;
import ru.brill.wallet.model.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@UtilityClass
public class TransactionMapper {

    public WalletTransaction toNewWalletTransaction(Wallet senderWallet, Wallet receiverWallet, BigDecimal amount) {
        return WalletTransaction.builder()
                .senderWallet(senderWallet)
                .receiverWallet(receiverWallet)
                .created(LocalDateTime.now())
                .amount(amount)
                .build();
    }

    public TransactionDtoOut toTransactionDtoOut(WalletTransaction transaction) {
        return TransactionDtoOut.builder()
                .id(transaction.getId())
                .created(transaction.getCreated())
                .senderWalletId(transaction.getSenderWallet() != null ? transaction.getSenderWallet().getId() : null)
                .receiverWalletId(transaction.getReceiverWallet().getId())
                .amount(transaction.getAmount())
                .build();
    }

    public TransactionForWallet toTransactionForWallet(WalletTransaction transaction, Long walletId) {
        return TransactionForWallet.builder()
                .id(transaction.getId())
                .created(transaction.getCreated())
                .amount(transaction.getAmount())
                .senderWalletId(transaction.getSenderWallet() != null ? transaction.getSenderWallet().getId() : null)
                .receiverWalletId(transaction.getReceiverWallet().getId())
                .isIncoming(transaction.getReceiverWallet().getId().equals(walletId))
                .build();
    }
}
