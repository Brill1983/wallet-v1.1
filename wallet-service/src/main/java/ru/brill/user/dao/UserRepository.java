package ru.brill.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.brill.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
