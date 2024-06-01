package ru.brill.wallet;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.brill.exceptions.BadParameterException;
import ru.brill.exceptions.ElementNotFoundException;
import ru.brill.exceptions.RestrictedOperationException;
import ru.brill.transactions.model.WalletTransaction;
import ru.brill.user.UserService;
import ru.brill.user.dto.UserDto;
import ru.brill.wallet.dto.AmountDto;
import ru.brill.wallet.dto.WalletOutDto;
import ru.brill.wallet.model.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class WalletServiceImplTest {

    private final EntityManager em;
    private final WalletService walletService;
    private final UserService userService;

    UserDto userDto;

    @BeforeEach
    public void create() {
        userDto = new UserDto(1L, "Иван", "Иванович", "ii@mail.ru");
        userService.createUser(userDto);
    }

    @Test
    void createWalletTest() {
        walletService.createWallet(userDto.getId());
        TypedQuery<Wallet> query = em.createQuery("select w from Wallet w where w.user.id = :id", Wallet.class);
        Wallet wallet = query.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(wallet.getId(), equalTo(1L));
        assertThat(wallet.getUser().getId(), equalTo(userDto.getId()));
        assertThat(wallet.getUser().getFirstName(), equalTo(userDto.getFirstName()));
        assertThat(wallet.getUser().getLastName(), equalTo(userDto.getLastName()));
        assertThat(wallet.getCreated(), instanceOf(LocalDateTime.class));
        assertThat(wallet.getBalance(), equalTo(BigDecimal.ZERO));
    }

    @Test
    void createWalletWithWrongUserIdTest() {
        try {
            walletService.createWallet(2L);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + 2L + " не зарегистрирован"));
            assertThat(thrown.getClass(), equalTo(ElementNotFoundException.class));
        }
    }

    @Test
    void sendMoneyToWalletTest() {
        AmountDto amountDto = new AmountDto(BigDecimal.TEN);
        walletService.createWallet(userDto.getId());
        WalletOutDto walletOutDto = walletService.sendMoneyToWallet(userDto.getId(), 1L, amountDto);

        TypedQuery<Wallet> query1 = em.createQuery("select w from Wallet w where w.user.id = :id", Wallet.class);
        Wallet wallet = query1.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(wallet.getId(), equalTo(1L));
        assertThat(wallet.getUser().getId(), equalTo(userDto.getId()));
        assertThat(wallet.getBalance(), equalTo(BigDecimal.TEN));
        assertThat(walletOutDto.getId(), equalTo(wallet.getId()));
        assertThat(walletOutDto.getBalance(), equalTo(wallet.getBalance()));
        assertThat(walletOutDto.getCreated(), equalTo(wallet.getCreated()));

        TypedQuery<WalletTransaction> query2 = em.createQuery(
                "select t from WalletTransaction t where t.receiverWallet.id = :id", WalletTransaction.class);
        WalletTransaction tr = query2.setParameter("id", wallet.getId()).getSingleResult();

        assertThat(tr.getId(), equalTo(1L));
        assertThat(tr.getSenderWallet(), nullValue());
        assertThat(tr.getReceiverWallet().getId(), equalTo(wallet.getId()));
        assertThat(tr.getAmount(), equalTo(BigDecimal.TEN));
    }

    @Test
    void sendMoneyToWalletWithWrongUserIdTest() {
        AmountDto amountDto = new AmountDto(BigDecimal.TEN);
        walletService.createWallet(userDto.getId());
        try {
            walletService.sendMoneyToWallet(2L, 1L, amountDto);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + 2L + " не зарегистрирован"));
            assertThat(thrown.getClass(), equalTo(ElementNotFoundException.class));
        }
    }

    @Test
    void sendMoneyToWalletWhichNotExistTest() {
        AmountDto amountDto = new AmountDto(BigDecimal.TEN);
        walletService.createWallet(userDto.getId());
        try {
            walletService.sendMoneyToWallet(userDto.getId(), 2L, amountDto);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Кошелек с ID " + 2L + " не зарегистрирован"));
            assertThat(thrown.getClass(), equalTo(ElementNotFoundException.class));
        }
    }

    @Test
    void sendMoneyToAnotherUserWalletTest() {
        AmountDto amountDto = new AmountDto(BigDecimal.TEN);
        walletService.createWallet(userDto.getId());

        UserDto secondUserDto = new UserDto(2L, "Петр", "Петров", "pp@mail.ru");
        userService.createUser(secondUserDto);
        walletService.createWallet(secondUserDto.getId());

        try {
            walletService.sendMoneyToWallet(userDto.getId(), 2L, amountDto);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Кошелек с ID " + 2L + " не принадлежит пользователю с ID " + 1L));
            assertThat(thrown.getClass(), equalTo(BadParameterException.class));
        }
    }

    @Test
    void sendMoneyToWalletWhichBalanceLimitTest() {
        AmountDto amountDto = new AmountDto(BigDecimal.valueOf(999998.99));
        walletService.createWallet(userDto.getId());
        walletService.sendMoneyToWallet(userDto.getId(), 1L, amountDto);
        amountDto = new AmountDto(BigDecimal.valueOf(5));
        try {
            walletService.sendMoneyToWallet(userDto.getId(), 1L, amountDto);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("При пополнении на сумму " + amountDto.getAmount() +
                    " будет превышен допустимый максимальный лимит кошелька, попробуйте перевод на другой кошелек"));
            assertThat(thrown.getClass(), equalTo(RestrictedOperationException.class));
        }
    }

    @Test
    void getWalletWithBalanceByIdTest() {
        AmountDto amountDto = new AmountDto(BigDecimal.TEN);
        Long walletId = walletService.createWallet(userDto.getId()).getId();
        walletService.sendMoneyToWallet(userDto.getId(), 1L, amountDto);

        WalletOutDto wallet = walletService.getWalletWithBalanceById(userDto.getId(), walletId);

        TypedQuery<Wallet> query = em.createQuery("select w from Wallet w where w.user.id = :id", Wallet.class);
        Wallet walletFromDb = query.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(walletFromDb.getId(), equalTo(wallet.getId()));
        assertThat(walletFromDb.getUser().getId(), equalTo(userDto.getId()));
        assertThat(walletFromDb.getBalance(), equalTo(wallet.getBalance()));
        assertThat(walletFromDb.getCreated(), equalTo(wallet.getCreated()));
    }

    @Test
    void getWalletWithBalanceByWrongUserIdTest() {
        walletService.createWallet(userDto.getId());
        try {
            walletService.getWalletWithBalanceById(2L, 1L);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + 2L + " не зарегистрирован"));
            assertThat(thrown.getClass(), equalTo(ElementNotFoundException.class));
        }
    }

    @Test
    void getWalletWithBalanceByWrongWalletIdTest() {
        walletService.createWallet(userDto.getId());
        try {
            walletService.getWalletWithBalanceById(userDto.getId(), 2L);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Кошелек с ID " + 2L + " не зарегистрирован"));
            assertThat(thrown.getClass(), equalTo(ElementNotFoundException.class));
        }
    }

    @Test
    void getWalletWithBalanceByIdBelongsToAnotherUserTest() {
        walletService.createWallet(userDto.getId());

        UserDto secondUserDto = new UserDto(2L, "Петр", "Петров", "pp@mail.ru");
        userService.createUser(secondUserDto);
        walletService.createWallet(secondUserDto.getId());

        try {
            walletService.getWalletWithBalanceById(userDto.getId(), 2L);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Кошелек с ID " + 2L + " не принадлежит пользователю с ID " + userDto.getId()));
            assertThat(thrown.getClass(), equalTo(BadParameterException.class));
        }
    }

    @Test
    void getUserWalletsTest() {
        walletService.createWallet(userDto.getId());
        walletService.createWallet(userDto.getId());

        List<WalletOutDto> wallets = walletService.getUserWallets(userDto.getId(), 0, 5);

        TypedQuery<Wallet> query = em.createQuery("select w from Wallet w where w.user.id = :id order by w.created desc", Wallet.class);
        List<Wallet> walletsFromDb = query.setParameter("id", userDto.getId()).getResultList();

        assertThat(walletsFromDb.size(), equalTo(wallets.size()));
        assertThat(wallets.size(), equalTo(2));
        assertThat(walletsFromDb.get(0).getId(), equalTo(wallets.get(0).getId()));
        assertThat(walletsFromDb.get(0).getCreated(), equalTo(wallets.get(0).getCreated()));
        assertThat(walletsFromDb.get(1).getId(), equalTo(wallets.get(1).getId()));
        assertThat(walletsFromDb.get(1).getCreated(), equalTo(wallets.get(1).getCreated()));
    }

    @Test
    void getUserWalletsByWrongUserIdTest() {
        walletService.createWallet(userDto.getId());
        try {
            walletService.getUserWallets(2L, 0, 5);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + 2L + " не зарегистрирован"));
            assertThat(thrown.getClass(), equalTo(ElementNotFoundException.class));
        }
    }

    @Test
    void deleteWalletTest() {
        Long walletId = walletService.createWallet(userDto.getId()).getId();
        walletService.deleteWallet(userDto.getId(), walletId);

        TypedQuery<Wallet> query = em.createQuery("select w from Wallet w where w.id = :id", Wallet.class);
        try {
            query.setParameter("id", walletId).getSingleResult();
        } catch (NoResultException thrown) {
            assertThat(thrown.getClass(), equalTo(NoResultException.class));
        }
    }

    @Test
    void deleteWalletByWrongUserIdTest() {
        Long walletId = walletService.createWallet(userDto.getId()).getId();

        try {
            walletService.deleteWallet(2L, walletId);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + 2L + " не зарегистрирован"));
            assertThat(thrown.getClass(), equalTo(ElementNotFoundException.class));
        }
    }

    @Test
    void deleteWalletByWrongWalletIdTest() {
        walletService.createWallet(userDto.getId());

        try {
            walletService.deleteWallet(userDto.getId(), 2L);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Кошелек с ID " + 2L + " не зарегистрирован"));
            assertThat(thrown.getClass(), equalTo(ElementNotFoundException.class));
        }
    }

    @Test
    void deleteAnotherUserWalletTest() {
        walletService.createWallet(userDto.getId());

        UserDto secondUserDto = new UserDto(2L, "Петр", "Петров", "pp@mail.ru");
        userService.createUser(secondUserDto);
        walletService.createWallet(secondUserDto.getId());

        try {
            walletService.deleteWallet(userDto.getId(), 2L);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Кошелек с ID " + 2L + " не принадлежит пользователю с ID " + userDto.getId()));
            assertThat(thrown.getClass(), equalTo(BadParameterException.class));
        }
    }

    @Test
    void deleteWalletWithMoneyRestrictedTest() {
        AmountDto amountDto = new AmountDto(BigDecimal.TEN);
        Long walletId = walletService.createWallet(userDto.getId()).getId();
        walletService.sendMoneyToWallet(userDto.getId(), walletId, amountDto);

        try {
            walletService.deleteWallet(userDto.getId(), 1L);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Удалить можно только кошелек с нулевым балансом. Баланс кошелька с ID " +
                    walletId + " составляет " + amountDto.getAmount()));
            assertThat(thrown.getClass(), equalTo(RestrictedOperationException.class));
        }
    }
}
