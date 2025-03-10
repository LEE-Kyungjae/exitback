package com.personal.imp.controller;

import com.personal.imp.model.UserSettings;
import com.personal.imp.service.UserSettingsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserSettingsController.class)
public class UserSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSettingsService userSettingsService;

    @Test
    void testGetUserSettings() throws Exception {
        Long userId = 1L;
        UserSettings settings = new UserSettings(userId, true, "EN");

        when(userSettingsService.getUserSettings(userId)).thenReturn(settings);

        mockMvc.perform(get("/settings/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.notificationsEnabled").value(true))
                .andExpect(jsonPath("$.language").value("EN"));
    }

    @Test
    void testUpdateUserSettings() throws Exception {
        Long userId = 1L;
        UserSettings updatedSettings = new UserSettings(userId, false, "FR");

        when(userSettingsService.updateUserSettings(eq(userId), any(UserSettings.class))).thenReturn(updatedSettings);

        mockMvc.perform(put("/settings/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"notificationsEnabled\":false,\"language\":\"FR\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.notificationsEnabled").value(false))
                .andExpect(jsonPath("$.language").value("FR"));
    }
}
