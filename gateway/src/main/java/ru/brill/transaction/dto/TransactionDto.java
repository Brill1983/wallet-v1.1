package ru.brill.transaction.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.brill.service.SenderNotEqualReceiver;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SenderNotEqualReceiver
public class TransactionDto {

    @NotNull(message = "Кошелек отправителя обязателен к указанию")
    @Positive(message = "№ кошелька отправителя не может быть меньше или равен 0")
    private Long senderWalletId;

    @NotNull(message = "Кошелек получателя обязателен к указанию")
    @Positive(message = "№ кошелька получателя не может быть меньше или равен 0")
    private Long receiverWalletId;

    @NotNull(message = "Сумма перевода не может быть NULL")
    @Positive(message = "Нельзя перевести 0 единиц или отрицательное значение")
    @Digits(integer = 6, fraction = 2, message = "Нельзя перевести более 999 999,99 единиц")
    private BigDecimal amount;
}
