package ru.brill.wallet;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.brill.service.Create;
import ru.brill.wallet.dto.WalletDto;

import static ru.brill.service.Constants.HEADER;

@Slf4j
@RestController
@RequestMapping(path = "/wallets")
@RequiredArgsConstructor
@Validated
public class WalletController {

    private final WalletClient walletClient;

    @PostMapping
    public ResponseEntity<Object> createWallet(@RequestHeader(HEADER) Long userId,
                                               @RequestBody @Validated({Create.class}) WalletDto walletDto) {
        log.info("В метод saveWallet передан userId {}, walletDto {}", userId, walletDto);
        return walletClient.createWallet(userId, walletDto);
    }

    @PatchMapping("/{walletId}")
    public ResponseEntity<Object> updateWalletBalance(@RequestHeader(HEADER) Long userId,
                                                      @RequestBody WalletDto walletDto,
                                                      @PathVariable Long walletId) {
        log.info("В метод updateWalletBalance передан userId {}, walletId {}, walletDto {}", userId, walletId, walletDto);
        return walletClient.updateWalletBalance(userId, walletDto, walletId);
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<Object> getBalanceByWalletId(@RequestHeader(HEADER) Long userId,
                                                       @PathVariable Long walletId) {
        log.info("В метод getBalanceByWalletId передан userId {}, walletId {}", userId, walletId);
        return walletClient.getBalanceByWalletId(userId, walletId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserWallets(@RequestHeader(HEADER) Long userId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("В метод getUserWallets передан userId {}, индекс первого элемента {}, количество элементов на " +
                "странице {}", userId, from, size);
        return walletClient.getUserWallets(userId, from, size);
    }

    @GetMapping("/transactions/{walletId}")
    public ResponseEntity<Object> getTransactionsByWalletId(@RequestHeader(HEADER) Long userId,
                                                            @PathVariable Long walletId,
                                                            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("В метод getTransactionsByWalletId передан userId {}, walletId {}, индекс первого элемента {}, " +
                "количество элементов на странице {}", userId, walletId, from, size);
        return walletClient.getTransactionsByWalletId(userId, walletId);
    }

    @DeleteMapping("/{walletId}")
    public ResponseEntity<Object> deleteWallet(@RequestHeader(HEADER) Long userId,
                                               @PathVariable Long walletId) {
        log.info("В метод deleteWallet передан userId {}, walletId {}", userId, walletId);
        return walletClient.deleteWallet(userId, walletId);
    }
}
