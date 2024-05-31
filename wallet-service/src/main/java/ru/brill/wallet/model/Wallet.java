package ru.brill.wallet.model;

import jakarta.persistence.*;
import lombok.*;
import ru.brill.user.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallets")
@Getter
@Setter
@Builder
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;
}
