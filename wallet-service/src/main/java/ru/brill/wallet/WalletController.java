package ru.brill.wallet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.brill.wallet.dto.AmountDto;
import ru.brill.wallet.dto.WalletOutDto;

import java.util.List;

import static ru.brill.service.Constants.HEADER;

@Slf4j
@RestController
@RequestMapping(path = "/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public WalletOutDto createWallet(@RequestHeader(HEADER) Long userId) {
        log.info("В метод saveWallet передан userId {}", userId);
        return walletService.createWallet(userId);
    }

    @PatchMapping("/{walletId}")
    public WalletOutDto sendMoneyToWallet(@RequestHeader(HEADER) Long userId,
                                          @PathVariable Long walletId,
                                          @RequestBody AmountDto amountDto) {
        log.info("В метод sendMoneyToWallet передан userId {}, walletId {}, amountDto {}", userId, walletId, amountDto);
        return walletService.sendMoneyToWallet(userId, walletId, amountDto);
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
