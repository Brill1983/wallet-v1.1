package ru.brill.wallet;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.brill.wallet.dto.AmountDto;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.brill.service.Constants.HEADER;

@WebMvcTest(WalletController.class)
@AutoConfigureMockMvc
public class WalletControllerTest {

    @MockBean
    private WalletClient walletClient;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private AmountDto amount;
    private ResponseEntity<Object> walletResponse;
    private Long walletId;

    @BeforeEach
    public void ceate() {
        amount = new AmountDto(BigDecimal.valueOf(123.45));
        walletResponse = new ResponseEntity<>(amount, HttpStatus.OK);
        walletId = 1L;
    }

    @Test
    void createWalletTest() throws Exception {

        when(walletClient.createWallet(anyLong()))
                .thenReturn(walletResponse);

        mvc.perform(post("/wallets")
                        .header(HEADER, 1))
                .andExpect(status().isOk());

        verify(walletClient, times(1))
                .createWallet(anyLong());
    }

    @Test
    void sendMoneyToWalletTest() throws Exception {
        when(walletClient.sendMoneyToWallet(anyLong(), anyLong(), any()))
                .thenReturn(walletResponse);

        mvc.perform(patch("/wallets/{walletId}", walletId)
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(amount))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(amount.getAmount()), BigDecimal.class));

        verify(walletClient, times(1))
                .sendMoneyToWallet(anyLong(), anyLong(), any());
    }

    @Test
    void sendMoneyToWalletWithNullAmountTest() throws Exception {
        amount.setAmount(null);

        mvc.perform(patch("/wallets/{walletId}", walletId)
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(amount))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(walletClient, never())
                .sendMoneyToWallet(anyLong(), anyLong(), any());
    }

    @Test
    void sendMoneyToWalletWithZeroAmountTest() throws Exception {
        amount.setAmount(BigDecimal.ZERO);

        mvc.perform(patch("/wallets/{walletId}", walletId)
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(amount))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(walletClient, never())
                .sendMoneyToWallet(anyLong(), anyLong(), any());
    }


    @Test
    void sendMoneyToWalletWithNegativeAmountTest() throws Exception {
        amount.setAmount(BigDecimal.valueOf(-123.23));

        mvc.perform(patch("/wallets/{walletId}", walletId)
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(amount))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(walletClient, never())
                .sendMoneyToWallet(anyLong(), anyLong(), any());
    }

    @Test
    void sendMoneyToWalletWithTooBigAmountTest() throws Exception {
        amount.setAmount(BigDecimal.valueOf(1000000.00));

        mvc.perform(patch("/wallets/{walletId}", walletId)
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(amount))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(walletClient, never())
                .sendMoneyToWallet(anyLong(), anyLong(), any());
    }

    @Test
    void getWalletWithBalanceByIdTest() throws Exception {

        when(walletClient.getWalletWithBalanceById(anyLong(), anyLong()))
                .thenReturn(walletResponse);

        mvc.perform(get("/wallets/{walletId}", walletId)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(amount.getAmount().doubleValue())));

        verify(walletClient, times(1))
                .getWalletWithBalanceById(anyLong(), anyLong());
    }

    @Test
    void getUserWalletsTest() throws Exception {

        when(walletClient.getUserWallets(anyLong(), anyInt(), anyInt()))
                .thenReturn(walletResponse);

        mvc.perform(get("/wallets")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(amount.getAmount().doubleValue())));

        verify(walletClient, times(1))
                .getUserWallets(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getUserWalletsWithMinusFromParamTest() throws Exception {
        Integer from = -2;
        mvc.perform(get("/wallets?from={from}", from)
                        .header(HEADER, 1))
                .andExpect(status().isBadRequest());

        verify(walletClient, never())
                .getUserWallets(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getUserWalletsWithMinusSizeParamTest() throws Exception {
        Integer size = 0;
        mvc.perform(get("/wallets?size={size}", size)
                        .header(HEADER, 1))
                .andExpect(status().isBadRequest());

        verify(walletClient, never())
                .getUserWallets(anyLong(), anyInt(), anyInt());
    }

    @Test
    void deleteWalletTest() throws Exception {
        mvc.perform(delete("/wallets/{walletId}", walletId)
                        .header(HEADER, 1))
                .andExpect(status().isOk());

        verify(walletClient, times(1))
                .deleteWallet(anyLong(), anyLong());
    }
}
