package ru.brill.wallet;

import lombok.experimental.UtilityClass;
import ru.brill.user.model.User;
import ru.brill.wallet.dto.WalletOutDto;
import ru.brill.wallet.model.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@UtilityClass
public class WalletMapper {

    public WalletOutDto toWalletOutDto(Wallet wallet) {
        return new WalletOutDto(
                wallet.getId(),
                wallet.getCreated(),
                wallet.getBalance()
        );
    }

    public Wallet toNewWallet(User user) {
        return Wallet.builder()
                .user(user)
                .created(LocalDateTime.now())
                .balance(BigDecimal.ZERO)
                .build();
    }
}
