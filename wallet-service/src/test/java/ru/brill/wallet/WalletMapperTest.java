package ru.brill.wallet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.brill.user.model.User;
import ru.brill.wallet.dto.WalletOutDto;
import ru.brill.wallet.model.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WalletMapperTest {

    private Wallet wallet;
    private final User user = new User(1L, "Иван", "Иванович", "ii@Mail.ru");

    @BeforeEach
    public void create() {
        wallet = new Wallet(1L, user, LocalDateTime.now(), BigDecimal.ONE);
    }

    @Test
    void toWalletOutDtoTest() {
        WalletOutDto walletDto = WalletMapper.toWalletOutDto(wallet);

        assertEquals(wallet.getId(), walletDto.getId());
        assertEquals(wallet.getCreated(), walletDto.getCreated());
        assertEquals(wallet.getBalance(), walletDto.getBalance());
    }

    @Test
    void toNewWallet() {
        Wallet wal = WalletMapper.toNewWallet(user);
        assertEquals(user.getId(), wal.getUser().getId());
        assertEquals(user.getLastName(), wal.getUser().getLastName());
        assertEquals(user.getFirstName(), wal.getUser().getFirstName());
        assertEquals(BigDecimal.ZERO, wal.getBalance());
        assertNotNull(wal.getCreated());
    }
}
