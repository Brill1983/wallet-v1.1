package ru.brill.wallet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.brill.wallet.dto.WalletOutDto;

import java.util.List;

import static ru.brill.service.Constants.HEADER;

@Slf4j
@RestController
@RequestMapping(path = "/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public WalletOutDto createWallet(@RequestHeader(HEADER) Long userId) {
        log.info("В метод saveWallet передан userId {}", userId);
        return walletService.createWallet(userId);
    }

    @GetMapping("/{walletId}")
    public WalletOutDto getWalletWithBalanceById(@RequestHeader(HEADER) Long userId,
                                                 @PathVariable Long walletId) {
        log.info("В метод getWalletWithBalanceById передан userId {}, walletId {}", userId, walletId);
        return walletService.getWalletWithBalanceById(userId, walletId);
    }

    @GetMapping
    public List<WalletOutDto> getUserWallets(@RequestHeader(HEADER) Long userId,
                                             @RequestParam Integer from,
                                             @RequestParam Integer size) {
        log.info("В метод getUserWallets передан userId {}, индекс первого элемента {}, количество элементов на " +
                "странице {}", userId, from, size);
        return walletService.getUserWallets(userId, from, size);
    }

    @DeleteMapping("/{walletId}")
    public void deleteWallet(@RequestHeader(HEADER) Long userId,
                                               @PathVariable Long walletId) {
        log.info("В метод deleteWallet передан userId {}, walletId {}", userId, walletId);
        walletService.deleteWallet(userId, walletId);
    }
}
