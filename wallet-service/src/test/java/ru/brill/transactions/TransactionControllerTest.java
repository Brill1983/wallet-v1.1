package ru.brill.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.brill.transactions.dto.TransactionDto;
import ru.brill.transactions.dto.TransactionDtoOut;
import ru.brill.transactions.dto.TransactionForWallet;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.brill.service.Constants.HEADER;

@AutoConfigureMockMvc
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @MockBean
    private TransactionsServiceImpl service;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;
    private TransactionDto transactionDto;
    private TransactionDtoOut transactionDtoOut;
    private TransactionForWallet transactionForWallet;

    @BeforeEach
    public void create() {
        transactionDto = new TransactionDto(1L, 2L, BigDecimal.ONE);
        transactionDtoOut = new TransactionDtoOut(1L, LocalDateTime.now(), 1L, 2L, BigDecimal.ONE);
        transactionForWallet = new TransactionForWallet(1L, LocalDateTime.now(), 1L, 2L, BigDecimal.ONE, false);
    }

    @Test
    void postTransactionTest() throws Exception {
        when(service.postTransaction(anyLong(), any()))
                .thenReturn(transactionDtoOut);

        mvc.perform(post("/transactions")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(transactionDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(transactionDtoOut)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(transactionDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.senderWalletId", is(transactionDtoOut.getSenderWalletId()), Long.class))
                .andExpect(jsonPath("$.receiverWalletId", is(transactionDtoOut.getReceiverWalletId()), Long.class))
                .andExpect(jsonPath("$.created").isNotEmpty());

        verify(service, times(1))
                .postTransaction(anyLong(), any());
    }

    @Test
    void getTransactionsByWalletIdTest() throws Exception {
        Long walletId = 1L;
        Integer from = 0;
        Integer size = 5;
        when(service.getTransactionsByWalletId(anyLong(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(transactionForWallet));

        mvc.perform(get("/transactions/{walletId}?from={from}&size={size}", walletId, from, size)
                        .header(HEADER, 1))
                .andExpect(content().json(mapper.writeValueAsString(List.of(transactionForWallet))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));


        verify(service, times(1))
                .getTransactionsByWalletId(anyLong(), anyLong(), anyInt(), anyInt());
    }
}
