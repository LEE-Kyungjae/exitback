package com.personal.imp.controller;

import com.personal.imp.service.BlockedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/block")
public class BlockedUserController {

    @Autowired
    private BlockedUserService blockedUserService;

    @PostMapping("/{userId}/block/{blockedUserId}")
    public void blockUser(@PathVariable Long userId, @PathVariable Long blockedUserId) {
        blockedUserService.blockUser(userId, blockedUserId);
    }

    @DeleteMapping("/{userId}/unblock/{blockedUserId}")
    public void unblockUser(@PathVariable Long userId, @PathVariable Long blockedUserId) {
        blockedUserService.unblockUser(userId, blockedUserId);
    }

    @GetMapping("/{userId}/is-blocked/{blockedUserId}")
    public boolean isUserBlocked(@PathVariable Long userId, @PathVariable Long blockedUserId) {
        return blockedUserService.isUserBlocked(userId, blockedUserId);
    }
}
