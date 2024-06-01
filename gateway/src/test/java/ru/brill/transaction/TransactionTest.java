package ru.brill.transaction;


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
import ru.brill.transaction.dto.TransactionDto;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.brill.service.Constants.HEADER;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc
public class TransactionTest {

    @MockBean
    private TransactionsClient transactionsClient;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private TransactionDto transactionDto;
    private ResponseEntity<Object> transactionResponse;

    @BeforeEach
    public void create() {
        transactionDto = new TransactionDto(1L, 2L, BigDecimal.valueOf(123.45));
        transactionResponse = new ResponseEntity<>(transactionDto, HttpStatus.OK);
    }

    @Test
    void postTransactionTest() throws Exception {

        when(transactionsClient.postTransaction(anyLong(), any()))
                .thenReturn(transactionResponse);

        mvc.perform(post("/transactions")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(transactionDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senderWalletId", is(transactionDto.getSenderWalletId()), Long.class))
                .andExpect(jsonPath("$.receiverWalletId", is(transactionDto.getReceiverWalletId()), Long.class))
                .andExpect(jsonPath("$.amount", is(transactionDto.getAmount()), BigDecimal.class))
                .andExpect(status().isOk());

        verify(transactionsClient, times(1))
                .postTransaction(anyLong(), any());
    }

    @Test
    void postTransactionWithNullSenderWalletTest() throws Exception {
        transactionDto.setSenderWalletId(null);

        mvc.perform(post("/transactions")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(transactionDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(transactionsClient, never())
                .postTransaction(anyLong(), any());
    }

    @Test
    void postTransactionWithSenderEqualsReceiverWalletTest() throws Exception {
        transactionDto.setReceiverWalletId(1L);

        mvc.perform(post("/transactions")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(transactionDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(transactionsClient, never())
                .postTransaction(anyLong(), any());
    }

    @Test
    void postTransactionWithZeroSenderWalletTest() throws Exception {
        transactionDto.setSenderWalletId(0L);

        mvc.perform(post("/transactions")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(transactionDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(transactionsClient, never())
                .postTransaction(anyLong(), any());
    }

    @Test
    void postTransactionWithMinusReceiverWalletTest() throws Exception {
        transactionDto.setReceiverWalletId(-1L);

        mvc.perform(post("/transactions")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(transactionDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(transactionsClient, never())
                .postTransaction(anyLong(), any());
    }

    @Test
    void postTransactionWithNullAmountTest() throws Exception {
        transactionDto.setAmount(null);

        mvc.perform(post("/transactions")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(transactionDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(transactionsClient, never())
                .postTransaction(anyLong(), any());
    }

    @Test
    void postTransactionWithZeroAmountTest() throws Exception {
        transactionDto.setAmount(BigDecimal.ZERO);

        mvc.perform(post("/transactions")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(transactionDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(transactionsClient, never())
                .postTransaction(anyLong(), any());
    }

    @Test
    void postTransactionWithMinusAmountTest() throws Exception {
        transactionDto.setAmount(BigDecimal.valueOf(-123.22));

        mvc.perform(post("/transactions")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(transactionDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(transactionsClient, never())
                .postTransaction(anyLong(), any());
    }

    @Test
    void postTransactionWithTooBigAmountTest() throws Exception {
        transactionDto.setAmount(BigDecimal.valueOf(1000000.22));

        mvc.perform(post("/transactions")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(transactionDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(transactionsClient, never())
                .postTransaction(anyLong(), any());
    }
}
