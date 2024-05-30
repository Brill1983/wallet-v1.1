package ru.brill.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.brill.client.BaseClient;
import ru.brill.transaction.dto.TransactionDto;

@Service
public class TransactionsClient extends BaseClient {

    private static final String API_PREFIX = "/transactions";

    @Autowired
    public TransactionsClient(@Value("${wallet-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public ResponseEntity<Object> getTransactionsByWalletId(Long userId, Long walletId) {
        return get("/" + walletId, userId);
    }

    public ResponseEntity<Object> getTransactionsByUserId(Long userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> postTransaction(Long userId, TransactionDto transactionDto) {
        return post("/", userId, transactionDto);
    }
}
