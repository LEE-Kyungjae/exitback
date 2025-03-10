package com.personal.imp.controller;

import com.personal.imp.model.UserSettings;
import com.personal.imp.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
public class UserSettingsController {

    @Autowired
    private UserSettingsService userSettingsService;

    @GetMapping("/{userId}")
    public UserSettings getUserSettings(@PathVariable Long userId) {
        return userSettingsService.getUserSettings(userId);
    }

    @PutMapping("/{userId}")
    public UserSettings updateUserSettings(@PathVariable Long userId, @RequestBody UserSettings settings) {
        return userSettingsService.updateUserSettings(userId, settings);
    }
}
