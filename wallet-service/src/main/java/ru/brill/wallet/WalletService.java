package ru.brill.wallet;

import org.springframework.http.ResponseEntity;
import ru.brill.wallet.dto.WalletOutDto;

import java.util.List;

public interface WalletService {

    WalletOutDto createWallet(Long userId);

    WalletOutDto getWalletWithBalanceById(Long userId, Long walletId);

    List<WalletOutDto> getUserWallets(Long userId, Integer from, Integer size);

    void deleteWallet(Long userId, Long walletId);
}
