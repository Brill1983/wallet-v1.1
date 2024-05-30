package ru.brill.transactions.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.brill.transactions.model.WalletTransaction;

public interface TransactionRepository extends JpaRepository<WalletTransaction, Long> {
}
