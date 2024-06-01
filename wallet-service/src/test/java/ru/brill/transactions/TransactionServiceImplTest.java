package ru.brill.transactions;

import jakarta.persistence.EntityManager;
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
import ru.brill.transactions.dto.TransactionDto;
import ru.brill.transactions.dto.TransactionDtoOut;
import ru.brill.transactions.model.WalletTransaction;
import ru.brill.user.UserService;
import ru.brill.user.dto.UserDto;
import ru.brill.wallet.WalletService;
import ru.brill.wallet.dto.AmountDto;
import ru.brill.wallet.model.Wallet;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TransactionServiceImplTest {

    private final EntityManager em;
    private final WalletService walletService;
    private final UserService userService;
    private final TransactionsService transactionsService;

    private TransactionDto transactionDto;
    private UserDto userDto1;
    private UserDto userDto2;


    @BeforeEach
    public void create() {
        userDto1 = new UserDto(1L, "Иван", "Иванович", "ii@mail.ru");
        userDto2 = new UserDto(2L, "Петр", "Петров", "pp@mail.ru");
        userService.createUser(userDto2);
        walletService.createWallet(userService.createUser(userDto1).getId());
        walletService.sendMoneyToWallet(1L, 1L, new AmountDto(BigDecimal.valueOf(1000.00)));
        walletService.createWallet(userService.createUser(userDto2).getId());
        transactionDto = new TransactionDto(1L, 2L, BigDecimal.valueOf(100));
    }

    @Test
    void postTransactionTest() {
        TransactionDtoOut tr = transactionsService.postTransaction(userDto1.getId(), transactionDto);

        assertThat(transactionDto.getSenderWalletId(), equalTo(tr.getSenderWalletId()));
        assertThat(transactionDto.getReceiverWalletId(), equalTo(tr.getReceiverWalletId()));
        assertThat(transactionDto.getAmount(), equalTo(tr.getAmount()));

        TypedQuery<WalletTransaction> query = em.createQuery(
                "select w from WalletTransaction w where w.senderWallet.user.id = :id", WalletTransaction.class);
        WalletTransaction trans = query.setParameter("id", userDto1.getId()).getSingleResult();

        assertThat(trans.getSenderWallet().getId(), equalTo(tr.getSenderWalletId()));
        assertThat(trans.getReceiverWallet().getId(), equalTo(tr.getReceiverWalletId()));
        assertThat(trans.getAmount(), equalTo(tr.getAmount()));
        assertThat(trans.getCreated(), equalTo(tr.getCreated()));

        TypedQuery<Wallet> query1 = em.createQuery("select w from Wallet w where w.user.id = :id", Wallet.class);
        Wallet wallet1 = query1.setParameter("id", userDto1.getId()).getSingleResult();

        assertThat(wallet1.getBalance().doubleValue(), equalTo(BigDecimal.valueOf(900).doubleValue()));

        TypedQuery<Wallet> query2 = em.createQuery("select w from Wallet w where w.user.id = :id", Wallet.class);
        Wallet wallet2 = query1.setParameter("id", userDto2.getId()).getSingleResult();

        assertThat(wallet2.getBalance().doubleValue(), equalTo(BigDecimal.valueOf(100).doubleValue()));
    }

    @Test
    void postTransactionWithWrongUserIdTest() {
        try {
            transactionsService.postTransaction(3L, transactionDto);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + 3L + " не зарегистрирован"));
            assertThat(thrown.getClass(), equalTo(ElementNotFoundException.class));
        }
    }

    @Test
    void postTransactionFromWrongWalletTest() {
        transactionDto.setSenderWalletId(3L);
        try {
            transactionsService.postTransaction(1L, transactionDto);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Кошелек с ID " + 3L + " не зарегистрирован"));
            assertThat(thrown.getClass(), equalTo(ElementNotFoundException.class));
        }
    }

    @Test
    void postTransactionFromWalletOfAnotherUserTest() {
        transactionDto.setSenderWalletId(2L);
        try {
            transactionsService.postTransaction(1L, transactionDto);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Кошелек с ID " + 2L + " не принадлежит пользователю с ID " + 1L));
            assertThat(thrown.getClass(), equalTo(BadParameterException.class));
        }
    }

    @Test
    void postTransactionNotEnoughMoneyTest() {
        transactionDto.setAmount(BigDecimal.valueOf(1500));
        try {
            transactionsService.postTransaction(1L, transactionDto);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Денег на кошельке с ID " + transactionDto.getSenderWalletId() +
                    " меньше чем запрошенная сумма перевода " + transactionDto.getAmount()));
            assertThat(thrown.getClass(), equalTo(RestrictedOperationException.class));
        }
    }

    @Test
    void postTransactionReceiverWalletOutOfLimitsTest() {
        transactionDto.setAmount(BigDecimal.valueOf(1000));
        walletService.sendMoneyToWallet(2L, 2L, new AmountDto(BigDecimal.valueOf(999999.00)));
        try {
            transactionsService.postTransaction(1L, transactionDto);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("При пополнении на сумму " + transactionDto.getAmount() +
                    " будет превышен допустимый максимальный лимит кошелька, попробуйте перевод на другой кошелек"));
            assertThat(thrown.getClass(), equalTo(RestrictedOperationException.class));
        }
    }

    @Test
    void postTransactionToWrongWalletTest() {
        transactionDto.setReceiverWalletId(3L);
        try {
            transactionsService.postTransaction(1L, transactionDto);
        } catch (RuntimeException thrown) {
            assertThat(thrown.getMessage(), equalTo("Кошелек с ID " + 3L + " не зарегистрирован"));
            assertThat(thrown.getClass(), equalTo(ElementNotFoundException.class));
        }
    }
}
