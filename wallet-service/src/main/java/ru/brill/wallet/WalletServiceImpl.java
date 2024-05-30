package ru.brill.wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.brill.exceptions.BadParameterException;
import ru.brill.exceptions.ElementNotFoundException;
import ru.brill.exceptions.RestrictedOperationException;
import ru.brill.service.ValidationService;
import ru.brill.user.dao.UserRepository;
import ru.brill.user.model.User;
import ru.brill.wallet.dao.WalletRepository;
import ru.brill.wallet.dto.WalletOutDto;
import ru.brill.wallet.model.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final ValidationService validator;

    @Override
    public WalletOutDto createWallet(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        Wallet wallet = Wallet.builder()
                .user(user)
                .created(LocalDateTime.now())
                .balance(BigDecimal.ZERO)
                .build();
        return WalletMapper.toWalletOutDto(walletRepository.save(wallet));
    }

    @Override
    @Transactional(readOnly = true)
    public WalletOutDto getWalletWithBalanceById(Long userId, Long walletId) {
        validator.validUserId(userId);
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ElementNotFoundException("Кошелек с ID " + walletId + " не зарегистрирован"));
        validator.validWalletBelongsToUser(userId, wallet.getUser().getId());
        return WalletMapper.toWalletOutDto(wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletOutDto> getUserWallets(Long userId, Integer from, Integer size) {
        validator.validUserId(userId);
        Pageable page = PageRequest.of(from / size, size);
        return walletRepository.findAllByUserIdOrderByCreatedDesc(userId, page)
                .map(WalletMapper::toWalletOutDto)
                .toList();
    }

    @Override
    public void deleteWallet(Long userId, Long walletId) {
        validator.validUserId(userId);
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ElementNotFoundException("Кошелек с ID " + walletId + " не зарегистрирован"));
        validator.validWalletBelongsToUser(userId, wallet.getUser().getId());
        if (wallet.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new RestrictedOperationException("Удалить можно только кошелек с нулевым балансом. Баланc кошелька с ID " +
                    walletId + " составляет " + wallet.getBalance());
        }
        walletRepository.delete(wallet);
    }
}
