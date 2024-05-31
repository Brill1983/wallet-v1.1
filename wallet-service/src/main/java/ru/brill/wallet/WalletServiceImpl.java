package ru.brill.wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.brill.exceptions.ElementNotFoundException;
import ru.brill.exceptions.RestrictedOperationException;
import ru.brill.service.ValidationService;
import ru.brill.transactions.TransactionMapper;
import ru.brill.transactions.dao.TransactionRepository;
import ru.brill.transactions.model.WalletTransaction;
import ru.brill.user.dao.UserRepository;
import ru.brill.user.model.User;
import ru.brill.wallet.dao.WalletRepository;
import ru.brill.wallet.dto.AmountDto;
import ru.brill.wallet.dto.WalletOutDto;
import ru.brill.wallet.model.Wallet;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final ValidationService validator;
    private final TransactionRepository transactionRepository;

    @Override
    public WalletOutDto createWallet(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        Wallet wallet = WalletMapper.toNewWallet(user);
        return WalletMapper.toWalletOutDto(walletRepository.save(wallet));
    }

    @Override
    public WalletOutDto sendMoneyToWallet(Long userId, Long walletId, AmountDto amountDto) {
        validator.validUserId(userId);
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ElementNotFoundException("Кошелек с ID " + walletId + " не зарегистрирован"));
        validator.validWalletBelongsToUser(userId, wallet);
        BigDecimal amount = amountDto.getAmount();
        validator.validBalanceLimit(wallet, amount);

        WalletTransaction transaction = TransactionMapper.toNewWalletTransaction(null, wallet, amount);
        transactionRepository.save(transaction);

        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        return WalletMapper.toWalletOutDto(walletRepository.save(wallet));
    }

    @Override
    @Transactional(readOnly = true)
    public WalletOutDto getWalletWithBalanceById(Long userId, Long walletId) {
        validator.validUserId(userId);
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ElementNotFoundException("Кошелек с ID " + walletId + " не зарегистрирован"));
        validator.validWalletBelongsToUser(userId, wallet);
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
        validator.validWalletBelongsToUser(userId, wallet);
        if (wallet.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new RestrictedOperationException("Удалить можно только кошелек с нулевым балансом. Баланс кошелька с ID " +
                    walletId + " составляет " + wallet.getBalance());
        }
        walletRepository.delete(wallet);
    }
}
