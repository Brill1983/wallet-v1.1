package ru.brill.wallet.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AmountDto {

    private BigDecimal amount;
}
