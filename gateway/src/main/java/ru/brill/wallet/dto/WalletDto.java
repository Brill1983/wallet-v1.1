package ru.brill.wallet.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.brill.service.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WalletDto {

    private Long id; // TODO перенести в Wallet-service
//    private String walletName; // TODO на вторую итерацию
    private LocalDateTime created; // TODO перенести в wallet-service

    @NotNull(groups = Update.class, message = "Нельзя пополнить баланс на отрицательное число")
    @Positive(groups = Update.class, message = "Нельзя пополнить баланс на 0 единиц")
    @Digits(groups = Update.class, integer = 6, fraction = 2)
    private BigDecimal balance;
    // TODO добавить валюту
}
