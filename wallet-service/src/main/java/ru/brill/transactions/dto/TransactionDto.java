package ru.brill.transactions.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

    private Long senderWalletId;
    private Long receiverWalletId;
    private BigDecimal amount;
}