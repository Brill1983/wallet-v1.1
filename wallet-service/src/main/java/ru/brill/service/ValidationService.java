package ru.brill.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.brill.exceptions.BadParameterException;
import ru.brill.exceptions.ElementNotFoundException;
import ru.brill.exceptions.RestrictedOperationException;
import ru.brill.user.dao.UserRepository;
import ru.brill.wallet.dao.WalletRepository;
import ru.brill.wallet.model.Wallet;

import java.math.BigDecimal;

import static ru.brill.service.Constants.MAX_WALLET_LIMIT;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public void validWalletBelongsToUser(Long userId, Wallet wallet) {
        if (!wallet.getUser().getId().equals(userId)) {
            throw new BadParameterException("Кошелек с ID " + wallet.getId() + " не принадлежит пользователю с ID " + userId);
        }
    }

    public void validUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ElementNotFoundException("Пользователь с ID " + userId + " не зарегистрирован");
        }
    }

    public void validWalletExist(Long walletId) {
        if (!walletRepository.existsById(walletId)) {
            throw new ElementNotFoundException("Кошелек с ID " + walletId + " не зарегистрирован");
        }
    }

    public void validBalanceLimit(Wallet wallet, BigDecimal amount) {
        BigDecimal newAmount = wallet.getBalance().add(amount);

        if (newAmount.compareTo(MAX_WALLET_LIMIT) > 0) {
            throw new RestrictedOperationException("При пополнении на сумму " + amount +
                    " будет превышен допустимый максимальный лимит кошелька, попробуйте перевод на другой свой кошелек");
        }
    }
}
