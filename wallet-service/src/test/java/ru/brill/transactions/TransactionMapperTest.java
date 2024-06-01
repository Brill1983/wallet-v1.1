package ru.brill.transactions;

import org.junit.jupiter.api.Test;
import ru.brill.transactions.dto.TransactionDtoOut;
import ru.brill.transactions.dto.TransactionForWallet;
import ru.brill.transactions.model.WalletTransaction;
import ru.brill.user.model.User;
import ru.brill.wallet.model.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionMapperTest {

    private final User user1 = new User(1L, "Иван", "Иванович", "ii@mail.ru");
    private final User user2 = new User(2L, "Петр", "Петров", "pp@Mail.ru");
    private Wallet wallet1 = new Wallet(1L, user1, LocalDateTime.now(), BigDecimal.ONE);
    private Wallet wallet2 = new Wallet(2L, user2, LocalDateTime.now(), BigDecimal.ONE);
    private WalletTransaction walletTransaction = new WalletTransaction(1L, wallet1, wallet2, LocalDateTime.now(), BigDecimal.ZERO);

    @Test
    void toNewWalletTransactionTest() {
        WalletTransaction transaction = TransactionMapper.toNewWalletTransaction(wallet1, wallet2, BigDecimal.ONE);

        assertEquals(wallet1.getId(), transaction.getSenderWallet().getId());
        assertEquals(wallet2.getId(), transaction.getReceiverWallet().getId());
        assertEquals(BigDecimal.ONE, transaction.getAmount());
        assertNotNull(transaction.getCreated());
    }

    @Test
    void toTransactionDtoOutTest() {
        TransactionDtoOut transaction = TransactionMapper.toTransactionDtoOut(walletTransaction);
        assertEquals(walletTransaction.getId(), transaction.getId());
        assertEquals(walletTransaction.getSenderWallet().getId(), transaction.getSenderWalletId());
        assertEquals(walletTransaction.getReceiverWallet().getId(), transaction.getReceiverWalletId());
        assertEquals(walletTransaction.getCreated(), transaction.getCreated());
        assertEquals(walletTransaction.getAmount(), transaction.getAmount());
    }

    @Test
    void toTransactionDtoOutWithSenderNullTest() {
        walletTransaction.setSenderWallet(null);
        TransactionDtoOut transaction = TransactionMapper.toTransactionDtoOut(walletTransaction);

        assertEquals(walletTransaction.getId(), transaction.getId());
        assertNull(transaction.getSenderWalletId());
        assertEquals(walletTransaction.getReceiverWallet().getId(), transaction.getReceiverWalletId());
        assertEquals(walletTransaction.getCreated(), transaction.getCreated());
        assertEquals(walletTransaction.getAmount(), transaction.getAmount());
    }

    @Test
    void toTransactionForWalletTest() {
        TransactionForWallet transaction = TransactionMapper.toTransactionForWallet(walletTransaction, 1L);
        assertEquals(walletTransaction.getId(), transaction.getId());
        assertEquals(walletTransaction.getSenderWallet().getId(), transaction.getSenderWalletId());
        assertEquals(walletTransaction.getReceiverWallet().getId(), transaction.getReceiverWalletId());
        assertEquals(walletTransaction.getCreated(), transaction.getCreated());
        assertEquals(walletTransaction.getAmount(), transaction.getAmount());
        assertFalse(transaction.getIsIncoming());
    }

    @Test
    void toTransactionForWalletWithSenderNullTest() {
        walletTransaction.setSenderWallet(null);
        TransactionForWallet transaction = TransactionMapper.toTransactionForWallet(walletTransaction, 1L);
        assertEquals(walletTransaction.getId(), transaction.getId());
        assertNull(transaction.getSenderWalletId());
        assertEquals(walletTransaction.getReceiverWallet().getId(), transaction.getReceiverWalletId());
        assertEquals(walletTransaction.getCreated(), transaction.getCreated());
        assertEquals(walletTransaction.getAmount(), transaction.getAmount());
        assertFalse(transaction.getIsIncoming());
    }
}
