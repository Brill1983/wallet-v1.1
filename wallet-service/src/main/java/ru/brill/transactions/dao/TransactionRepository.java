package ru.brill.transactions.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.brill.transactions.model.WalletTransaction;

public interface TransactionRepository extends JpaRepository<WalletTransaction, Long> {

    Page<WalletTransaction> findAllBySenderWalletIdAndReceiverWalletIdOrderByCreatedDesc(Long senderWalletId,
                                                                                         Long receiverWalletId,
                                                                                         Pageable page);
}
