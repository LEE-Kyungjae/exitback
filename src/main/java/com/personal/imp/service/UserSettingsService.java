package com.personal.imp.service;

import com.personal.imp.model.UserSettings;
import com.personal.imp.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSettingsService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    public UserSettings getUserSettings(Long userId) {
        return userSettingsRepository.findByUserId(userId);
    }

    public UserSettings updateUserSettings(Long userId, UserSettings settings) {
        UserSettings existingSettings = userSettingsRepository.findByUserId(userId);
        existingSettings.setNotificationsEnabled(settings.isNotificationsEnabled());
        existingSettings.setNotificationTone(settings.getNotificationTone());
        return userSettingsRepository.save(existingSettings);
    }
}
