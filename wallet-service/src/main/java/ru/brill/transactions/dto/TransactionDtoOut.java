package ru.brill.transactions.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDtoOut {

    private Long id;
    private LocalDateTime created;
    private Long senderWalletId;
    private Long receiverWalletId;
    private BigDecimal amount;
}
