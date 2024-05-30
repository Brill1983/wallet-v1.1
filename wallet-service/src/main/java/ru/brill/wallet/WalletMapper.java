package ru.brill.wallet;

import lombok.experimental.UtilityClass;
import ru.brill.wallet.dto.WalletOutDto;
import ru.brill.wallet.model.Wallet;

@UtilityClass
public class WalletMapper {

    public WalletOutDto toWalletOutDto(Wallet wallet) {
        return new WalletOutDto(
                wallet.getId(),
                wallet.getCreated(),
                wallet.getBalance()
        );
    }
}
