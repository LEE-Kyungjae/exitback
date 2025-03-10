package com.personal.imp.controller;

import com.personal.imp.service.BlockedUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(BlockedUserController.class)
public class BlockedUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlockedUserService blockedUserService;

    private final Long userId = 1L;
    private final Long blockedUserId = 2L;

    @Test
    void testBlockUser() throws Exception {
        mockMvc.perform(post("/block/{userId}/block/{blockedUserId}", userId, blockedUserId))
                .andExpect(status().isOk());

        verify(blockedUserService).blockUser(userId, blockedUserId);
    }

    @Test
    void testUnblockUser() throws Exception {
        mockMvc.perform(delete("/block/{userId}/unblock/{blockedUserId}", userId, blockedUserId))
                .andExpect(status().isOk());

        verify(blockedUserService).unblockUser(userId, blockedUserId);
    }

    @Test
    void testIsUserBlocked() throws Exception {
        when(blockedUserService.isUserBlocked(userId, blockedUserId)).thenReturn(true);

        mockMvc.perform(get("/block/{userId}/is-blocked/{blockedUserId}", userId, blockedUserId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(blockedUserService).isUserBlocked(userId, blockedUserId);
    }
}
