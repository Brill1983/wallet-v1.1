package ru.brill.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.brill.exceptions.BadParameterException;
import ru.brill.exceptions.ElementNotFoundException;
import ru.brill.user.dao.UserRepository;
import ru.brill.wallet.dao.WalletRepository;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public void validWalletBelongsToUser(Long userId, Long walletId) {
        if (!walletId.equals(userId)) {
            throw new BadParameterException("Кошелек с ID " + walletId + " не принадлежит пользователю с ID " + userId);
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
}
