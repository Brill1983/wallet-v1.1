package ru.brill.wallet.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WalletOutDto {
    private Long id;
    private LocalDateTime created;
    private BigDecimal balance;
}
