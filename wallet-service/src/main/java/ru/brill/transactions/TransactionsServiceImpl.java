package ru.brill.transactions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.brill.exceptions.ElementNotFoundException;
import ru.brill.exceptions.RestrictedOperationException;
import ru.brill.service.ValidationService;
import ru.brill.transactions.dao.TransactionRepository;
import ru.brill.transactions.dto.TransactionDto;
import ru.brill.transactions.dto.TransactionDtoOut;
import ru.brill.transactions.dto.TransactionForWallet;
import ru.brill.transactions.model.WalletTransaction;
import ru.brill.wallet.dao.WalletRepository;
import ru.brill.wallet.model.Wallet;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionsServiceImpl implements TransactionsService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final ValidationService validator;

    @Override
    public List<TransactionForWallet> getTransactionsByWalletId(Long userId, Long walletId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getTransactionsByUserId(Long userId) {
        return null;
    }

    @Override
    public TransactionDtoOut postTransaction(Long userId, TransactionDto transactionDto) {
        BigDecimal amount = transactionDto.getAmount();
        validator.validUserId(userId);

        Wallet senderWallet = walletRepository.findById(transactionDto.getSenderWalletId())
                .orElseThrow(() -> new ElementNotFoundException("Кошелек с ID " + transactionDto.getSenderWalletId() +
                        " не зарегистрирован"));
        validator.validWalletBelongsToUser(userId, senderWallet);

        if (amount.compareTo(senderWallet.getBalance()) > 0) {
            throw new RestrictedOperationException("Денег на кошельке с ID " + senderWallet.getId() +
                    " меньше чем запрошенная сумма перевода " + amount);
        }

        Wallet receiverWallet = walletRepository.findById(transactionDto.getReceiverWalletId())
                .orElseThrow(() -> new ElementNotFoundException("Кошелек с ID " + transactionDto.getReceiverWalletId() +
                        " не зарегистрирован"));

        validator.validBalanceLimit(receiverWallet, amount);

        WalletTransaction transaction = TransactionMapper.toNewWalletTransaction(senderWallet, receiverWallet, amount);

        BigDecimal newBalance = senderWallet.getBalance().subtract(amount);
        senderWallet.setBalance(newBalance);
        walletRepository.save(senderWallet);

        newBalance = receiverWallet.getBalance().add(amount);
        receiverWallet.setBalance(newBalance);
        walletRepository.save(receiverWallet);

        return TransactionMapper.toTransactionDtoOut(transactionRepository.save(transaction));
    }
}
