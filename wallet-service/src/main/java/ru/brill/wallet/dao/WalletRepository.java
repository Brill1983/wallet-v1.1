package ru.brill.wallet.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.brill.wallet.model.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Page<Wallet> findAllByUserIdOrderByCreatedDesc(Long userId, Pageable page);
}
