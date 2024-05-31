package ru.brill.wallet;

import ru.brill.wallet.dto.AmountDto;
import ru.brill.wallet.dto.WalletOutDto;

import java.util.List;

public interface WalletService {

    WalletOutDto createWallet(Long userId);

    WalletOutDto sendMoneyToWallet(Long userId, Long walletId, AmountDto amountDto);

    WalletOutDto getWalletWithBalanceById(Long userId, Long walletId);

    List<WalletOutDto> getUserWallets(Long userId, Integer from, Integer size);

    void deleteWallet(Long userId, Long walletId);

}
