package ru.brill.transactions;

import lombok.experimental.UtilityClass;
import ru.brill.transactions.dto.TransactionDto;
import ru.brill.transactions.model.WalletTransaction;
import ru.brill.wallet.model.Wallet;

import java.time.LocalDateTime;

@UtilityClass
public class TransactionMapper {

    public WalletTransaction toWalletTransaction(Wallet senderWallet, Wallet receiverWallet, TransactionDto transactionDto) {
        return WalletTransaction.builder()
                .senderWallet(senderWallet)
                .receiverWallet(receiverWallet)
                .created(LocalDateTime.now())
                .amount(transactionDto.getAmount())
                .build();
    }
}
