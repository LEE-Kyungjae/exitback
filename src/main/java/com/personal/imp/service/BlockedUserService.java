package com.personal.imp.service;

import com.personal.imp.model.BlockedUser;
import com.personal.imp.model.User;
import com.personal.imp.repository.BlockedUserRepository;
import com.personal.imp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlockedUserService {

    @Autowired
    private BlockedUserRepository blockedUserRepository;

    @Autowired
    private UserRepository userRepository;  // UserRepository 추가

    public void blockUser(Long userId, Long blockedUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User blockedUserEntity = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new RuntimeException("Blocked user not found"));

        BlockedUser blockedUser = new BlockedUser();
        blockedUser.setUser(user);  // User 객체로 설정
        blockedUser.setBlockedUser(blockedUserEntity);  // BlockedUser 객체로 설정
        blockedUserRepository.save(blockedUser);
    }

    public void unblockUser(Long userId, Long blockedUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User blockedUserEntity = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new RuntimeException("Blocked user not found"));

        BlockedUser blockedUser = blockedUserRepository.findByUserAndBlockedUser(user, blockedUserEntity)
                .orElseThrow(() -> new RuntimeException("Blocked user relationship not found"));
        blockedUserRepository.delete(blockedUser);
    }

    public boolean isUserBlocked(Long userId, Long blockedUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User blockedUserEntity = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new RuntimeException("Blocked user not found"));

        return blockedUserRepository.findByUserAndBlockedUser(user, blockedUserEntity).isPresent();
    }
}
