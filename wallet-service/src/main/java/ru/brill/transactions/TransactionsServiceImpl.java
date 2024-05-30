package ru.brill.transactions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.brill.exceptions.BadParameterException;
import ru.brill.exceptions.ElementNotFoundException;
import ru.brill.exceptions.RestrictedOperationException;
import ru.brill.service.ValidationService;
import ru.brill.transactions.dao.TransactionRepository;
import ru.brill.transactions.dto.TransactionDto;
import ru.brill.transactions.model.WalletTransaction;
import ru.brill.user.dao.UserRepository;
import ru.brill.user.model.User;
import ru.brill.wallet.dao.WalletRepository;
import ru.brill.wallet.model.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static ru.brill.service.Constants.MAX_WALLET_LIMIT;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionsServiceImpl implements TransactionsService{

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final ValidationService validator;

    @Override
    public ResponseEntity<Object> getTransactionsByWalletId(Long userId, Long walletId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getTransactionsByUserId(Long userId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> postTransaction(Long userId, TransactionDto transactionDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));

        Wallet senderWallet = null;
        Wallet receiverWallet = null;
        Long senderWalletId = transactionDto.getSenderWalletId();

        // Если senderWalletId = null - значит пользователь переводит себе деньги из иного сервиса
        if (senderWalletId == null) {
            receiverWallet = walletRepository.findById(transactionDto.getReceiverWalletId())
                    .orElseThrow(() -> new ElementNotFoundException("Кошелек с ID " + transactionDto.getReceiverWalletId() + " не зарегистрирован"));

            if (!userId.equals(receiverWallet.getUser().getId())) {
                throw new RestrictedOperationException("Пополнять из другого сервиса можно только свой кошелек");
            }

            BigDecimal newAmount = receiverWallet.getBalance().add(transactionDto.getAmount());

            if (newAmount.compareTo(MAX_WALLET_LIMIT) > 0) {
                throw new RestrictedOperationException("При пополнении на сумму " + transactionDto.getAmount() +
                        " будет превышен допустимый максимальный лимит кошелька, попробуйте перевод на другой свой кошелек");
            }

            WalletTransaction transaction = TransactionMapper.toWalletTransaction(null, receiverWallet, transactionDto);

            WalletTransaction walletTransaction = transactionRepository.save(transaction); // TODO - что возвращаем
            // записать транзакцию
            // записать изменение кошелька получателя

        } else {
            senderWallet = walletRepository.findById(senderWalletId)
                    .orElseThrow(() -> new ElementNotFoundException("Кошелек с ID " + transactionDto.getSenderWalletId() + " не зарегистрирован"));
            validator.validWalletBelongsToUser(userId, senderWalletId);

            if (senderWallet.getBalance().compareTo(transactionDto.getAmount()) < 0) {
                throw new RestrictedOperationException("Денег на кошельке с ID " + senderWalletId +
                        " меньше чем запрошенная сумма перевода " + transactionDto.getAmount());
            }
        }

        receiverWallet = walletRepository.findById(transactionDto.getReceiverWalletId())
                .orElseThrow(() -> new ElementNotFoundException("Кошелек с ID " + transactionDto.getReceiverWalletId() + " не зарегистрирован"));

        return null;
    }
}
