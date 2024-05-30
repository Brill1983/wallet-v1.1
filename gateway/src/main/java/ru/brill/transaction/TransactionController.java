package ru.brill.transaction;

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

    @GetMapping
    public ResponseEntity<Object> getTransactionsByUserId(@RequestHeader(HEADER) Long userId,
                                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("В метод getTransactionsByUserId передан userId {}, индекс первого элемента {}, " +
                "количество элементов на странице {}", userId, from, size);
        return transactionsClient.getTransactionsByUserId(userId);
    }

    /**
     * Если в TransactionDto указан senderWalletId = NULL (т.е. в теле запроса отсутствует),
     * значит хозяин кошелька пополняет баланс переводом со стороннего сервиса (с карты, сбп и др).
     * Если указан senderWalletId - значит идет перемещение средств между кошельками внутри системы.
     */
    @PostMapping
    public ResponseEntity<Object> postTransaction(@RequestHeader(HEADER) Long userId,
                                                  @RequestBody TransactionDto transactionDto) {
        log.info("В метод postTransaction передан userId {}, transactionDto {}", userId, transactionDto);
        return transactionsClient.postTransaction(userId, transactionDto);
    }
}
