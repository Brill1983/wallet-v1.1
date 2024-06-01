package ru.brill.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.brill.wallet.dto.AmountDto;
import ru.brill.wallet.dto.WalletOutDto;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.brill.service.Constants.HEADER;

@AutoConfigureMockMvc
@WebMvcTest(WalletController.class)
public class WalletControllerTest {

    @MockBean
    private WalletServiceImpl walletService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;
    private WalletOutDto walletDto;

    @BeforeEach
    public void create() {
        walletDto = new WalletOutDto(1L, LocalDateTime.now(), BigDecimal.ZERO);
    }

    @Test
    void createWalletTest() throws Exception {
        when(walletService.createWallet(any()))
                .thenReturn(walletDto);
        mvc.perform(post("/wallets")
                        .header(HEADER, 1))
                .andExpect(content().json(mapper.writeValueAsString(walletDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(walletDto.getId()), Long.class))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.balance", is(walletDto.getBalance()), BigDecimal.class));

        verify(walletService, times(1))
                .createWallet(any());
    }

    @Test
    void sendMoneyToWallet() throws Exception {
        Long walletId = 1L;
        AmountDto amountDto = new AmountDto(BigDecimal.ZERO);
        when(walletService.sendMoneyToWallet(anyLong(), anyLong(), any()))
                .thenReturn(walletDto);
        mvc.perform(patch("/wallets/{walletId}", walletId)
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(amountDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(walletDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(walletDto.getId()), Long.class))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.balance", is(walletDto.getBalance()), BigDecimal.class));

        verify(walletService, times(1))
                .sendMoneyToWallet(anyLong(), anyLong(), any());
    }

    @Test
    void getWalletWithBalanceById() throws Exception {
        Long walletId = 1L;
        when(walletService.getWalletWithBalanceById(anyLong(), anyLong()))
                .thenReturn(walletDto);
        mvc.perform(get("/wallets/{walletId}", walletId)
                        .header(HEADER, 1))
                .andExpect(content().json(mapper.writeValueAsString(walletDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(walletDto.getId()), Long.class))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.balance", is(walletDto.getBalance()), BigDecimal.class));

        verify(walletService, times(1))
                .getWalletWithBalanceById(anyLong(), anyLong());
    }

    @Test
    void getUserWalletsTest() throws Exception {
        when(walletService.getUserWallets(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(walletDto));

        Integer from = 0;
        Integer size = 5;
        mvc.perform(get("/wallets?from={from}&size={size}", from, size)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(walletDto))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(walletService, times(1))
                .getUserWallets(anyLong(), anyInt(), anyInt());
    }

    @Test
    void deleteWalletTest() throws Exception {
        Long walletId = 1L;
        mvc.perform(delete("/wallets/{walletId}", walletId)
                        .header(HEADER, 1))
                .andExpect(status().isOk());

        verify(walletService, times(1))
                .deleteWallet(anyLong(), anyLong());
    }
}
