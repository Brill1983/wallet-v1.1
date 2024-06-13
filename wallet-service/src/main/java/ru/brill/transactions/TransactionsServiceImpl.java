package ru.brill.transactions;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionsServiceImpl implements TransactionsService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final ValidationService validator;

    @Override
    @Transactional(readOnly = true)
    public List<TransactionForWallet> getTransactionsByWalletId(Long userId, Long walletId, Integer from, Integer size) {
        validator.validUserId(userId);
        Pageable page = PageRequest.of(from / size, size);
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ElementNotFoundException("Кошелек с ID " + walletId + " не зарегистрирован"));
        validator.validWalletBelongsToUser(userId, wallet);

        return transactionRepository.findAllBySenderWalletIdOrReceiverWalletIdOrderByCreatedDesc(walletId, walletId, page)
                .stream()
                .map(tr -> TransactionMapper.toTransactionForWallet(tr, walletId))
                .toList();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TransactionDtoOut postTransaction(Long userId, TransactionDto transactionDto) {
        TransactionDtoOut transactionBacked;
        try {
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

            transactionBacked = TransactionMapper.toTransactionDtoOut(transactionRepository.save(transaction));
        } catch(Exception ex) { // В случае гонки данных выбросится исключение. Его надо отловить и рекурсивно повторить транзакцию.
            // Такое решение может быть не оптимальным.
            if (ex instanceof SQLException) {
                transactionBacked = postTransaction(userId, transactionDto);
                return transactionBacked;
            }
            throw new RuntimeException(ex);
        }
        return transactionBacked;
    }
}
