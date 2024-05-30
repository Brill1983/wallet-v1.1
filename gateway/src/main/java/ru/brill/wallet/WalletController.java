package ru.brill.wallet;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.brill.wallet.dto.AmountDto;

import static ru.brill.service.Constants.HEADER;

@Slf4j
@RestController
@RequestMapping(path = "/wallets")
@RequiredArgsConstructor
@Validated
public class WalletController {

    private final WalletClient walletClient;

    @PostMapping
    public ResponseEntity<Object> createWallet(@RequestHeader(HEADER) Long userId) {
        log.info("В метод saveWallet передан userId {}", userId);
        return walletClient.createWallet(userId);
    }

    @PatchMapping("/{walletId}")
    public ResponseEntity<Object> sendMoneyToWallet(@RequestHeader(HEADER) Long userId,
                                                    @PathVariable Long walletId,
                                                    @RequestBody @Valid AmountDto amountDto){
        log.info("В метод sendMoneyToWallet передан userId {}, walletId {}, amountDto {}", userId, walletId, amountDto);
        return walletClient.sendMoneyToWallet(userId, walletId, amountDto);
    }

    // Получение кошелька, в нем есть баланс
    @GetMapping("/{walletId}")
    public ResponseEntity<Object> getWalletWithBalanceById(@RequestHeader(HEADER) Long userId,
                                                           @PathVariable Long walletId) {
        log.info("В метод getWalletWithBalanceById передан userId {}, walletId {}", userId, walletId);
        return walletClient.getWalletWithBalanceById(userId, walletId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserWallets(@RequestHeader(HEADER) Long userId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("В метод getUserWallets передан userId {}, индекс первого элемента {}, количество элементов на " +
                "странице {}", userId, from, size);
        return walletClient.getUserWallets(userId, from, size);
    }

    @DeleteMapping("/{walletId}")
    public void deleteWallet(@RequestHeader(HEADER) Long userId,
                             @PathVariable Long walletId) {
        log.info("В метод deleteWallet передан userId {}, walletId {}", userId, walletId);
        walletClient.deleteWallet(userId, walletId);
    }
}
