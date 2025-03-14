package com.personal.imp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.personal.imp.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
