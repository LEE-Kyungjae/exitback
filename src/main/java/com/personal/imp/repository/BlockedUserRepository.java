package com.personal.imp.repository;

import com.personal.imp.model.BlockedUser;
import com.personal.imp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockedUserRepository extends JpaRepository<BlockedUser, Long> {
    Optional<BlockedUser> findByUserAndBlockedUser(User user, User blockedUser);

}
