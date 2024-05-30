package ru.brill.wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.brill.client.BaseClient;


import java.util.Map;

@Service
public class WalletClient extends BaseClient {

    private static final String API_PREFIX = "/wallets";

    @Autowired
    public WalletClient(@Value("${wallet-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public ResponseEntity<Object> createWallet(Long userId) {
        return post("", userId, null);
    }

    public ResponseEntity<Object> getWalletWithBalanceById(Long userId, Long walletId) {
        return get("/" + walletId, userId);
    }

    public ResponseEntity<Object> getUserWallets(Long userId, Integer from, Integer size) {
        Map<String, Object> params = Map.of("from", from, "size", size);
        return get("?from={from}&size={size}", userId, params);
    }

    public void deleteWallet(Long userId, Long walletId) {
        delete("/" + walletId, userId);
    }
}
