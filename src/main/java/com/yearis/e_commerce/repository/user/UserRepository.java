package com.yearis.e_commerce.repository.user;

import com.yearis.e_commerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    void deleteByIsVerifiedFalseAndCreatedAtBefore(LocalDateTime cutoffTime);
}
