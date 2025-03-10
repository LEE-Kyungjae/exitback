package com.personal.imp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TypingIndicatorController.class)
public class TypingIndicatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;

    @Test
    void testSendTypingIndicator() throws Exception {
        String username = "testUser";
        String typingMessage = username + " is typing...";

        mockMvc.perform(post("/app/typing")
                        .content(username))
                .andExpect(status().isOk());

        verify(simpMessagingTemplate, times(1)).convertAndSend("/topic/typing", typingMessage);
    }
}
