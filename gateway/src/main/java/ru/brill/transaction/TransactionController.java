package ru.brill.transaction;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.brill.transaction.dto.TransactionDto;

import static ru.brill.service.Constants.HEADER;

@Slf4j
@RestController
@RequestMapping(path = "/transactions")
@RequiredArgsConstructor
@Validated
public class TransactionController {

    private final TransactionsClient transactionsClient;

    @GetMapping("/{walletId}")
    public ResponseEntity<Object> getTransactionsByWalletId(@RequestHeader(HEADER) Long userId,
                                                            @PathVariable Long walletId,
                                                            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("В метод getTransactionsByWalletId передан userId {}, walletId {}, индекс первого элемента {}, " +
                "количество элементов на странице {}", userId, walletId, from, size);
        return transactionsClient.getTransactionsByWalletId(userId, walletId);
    }

    @PostMapping
    public ResponseEntity<Object> postTransaction(@RequestHeader(HEADER) Long userId,
                                                  @RequestBody @Valid TransactionDto transactionDto) {
        log.info("В метод postTransaction передан userId {}, transactionDto {}", userId, transactionDto);
        return transactionsClient.postTransaction(userId, transactionDto);
    }
}
