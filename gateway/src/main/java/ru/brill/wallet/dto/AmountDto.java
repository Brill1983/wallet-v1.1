package ru.brill.wallet.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.brill.service.Create;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AmountDto {

    @NotNull(message = "Сумма пополнения не может быть NULL")
    @Positive(message = "Нельзя пополнить баланс на 0 единиц или отрицательное значение")
    @Digits(integer = 6, fraction = 2)
    private BigDecimal amount;
}
