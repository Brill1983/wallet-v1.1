package ru.brill.transactions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.brill.transactions.dto.TransactionDto;
import ru.brill.transactions.dto.TransactionDtoOut;
import ru.brill.transactions.dto.TransactionForWallet;

import java.util.List;

import static ru.brill.service.Constants.HEADER;

@Slf4j
@RestController
@RequestMapping(path = "/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionsService transactionsService;

    @GetMapping("/{walletId}")
    public List<TransactionForWallet> getTransactionsByWalletId(@RequestHeader(HEADER) Long userId,
                                                                @PathVariable Long walletId,
                                                                @RequestParam Integer from,
                                                                @RequestParam Integer size) {
        log.info("В метод getTransactionsByWalletId передан userId {}, walletId {}, индекс первого элемента {}, " +
                "количество элементов на странице {}", userId, walletId, from, size);
        return transactionsService.getTransactionsByWalletId(userId, walletId);
    }

    @GetMapping
    public ResponseEntity<Object> getTransactionsByUserId(@RequestHeader(HEADER) Long userId,
                                                          @RequestParam Integer from,
                                                          @RequestParam Integer size) {
        log.info("В метод getTransactionsByUserId передан userId {}, индекс первого элемента {}, " +
                "количество элементов на странице {}", userId, from, size);
        return transactionsService.getTransactionsByUserId(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public TransactionDtoOut postTransaction(@RequestHeader(HEADER) Long userId,
                                             @RequestBody TransactionDto transactionDto) {
        log.info("В метод postTransaction передан userId {}, transactionDto {}", userId, transactionDto);
        return transactionsService.postTransaction(userId, transactionDto);
    }
}
