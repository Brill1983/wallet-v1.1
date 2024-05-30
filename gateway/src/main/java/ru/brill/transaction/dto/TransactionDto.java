package ru.brill.transaction.dto;

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
public class TransactionDto {

    @Positive(groups = Create.class, message = "№ кошелька отправителя не может быть меньше или равен 0")
    private Long senderWalletId;

    @NotNull(groups = Create.class, message = "Кошелек покупателя обязателен к указанию")
    @Positive(groups = Create.class, message = "№ кошелька получателя не может быть меньше или равен 0")
    private Long receiverWalletId;

    @NotNull(groups = Create.class, message = "Сумма перевода не может быть NULL")
    @Positive(groups = Create.class, message = "Нельзя пополнить баланс на 0 единиц")
    @Digits(groups = Create.class, integer = 6, fraction = 2)
    private BigDecimal amount;
}
