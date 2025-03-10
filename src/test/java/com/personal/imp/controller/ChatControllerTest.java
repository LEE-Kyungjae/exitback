package com.personal.imp.controller;

import com.personal.imp.model.ChatMessage;
import com.personal.imp.service.ChatMessageService;
import com.personal.imp.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @MockBean
    private ChatMessageService chatMessageService;

    @Test
    void testSendMessage() throws Exception {
        ChatMessage message = new ChatMessage();
        message.setContent("Hello World");

        mockMvc.perform(post("/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"Hello World\"}"))
                .andExpect(status().isOk());

        verify(kafkaProducerService).sendMessage(message);
    }

    @Test
    void testGetAllMessages() throws Exception {
        //List<ChatMessage> messages = Arrays.asList(new ChatMessage("Hello"), new ChatMessage("Hi"));
        //when(chatMessageService.getAllMessages()).thenReturn(messages);

        mockMvc.perform(get("/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Hello"))
                .andExpect(jsonPath("$[1].content").value("Hi"));
    }

    @Test
    void testEditMessage() throws Exception {
        Long messageId = 1L;
        String newContent = "Updated Content";

        mockMvc.perform(put("/edit/{id}", messageId)
                        .param("newContent", newContent))
                .andExpect(status().isOk());

        verify(chatMessageService).updateMessageContent(messageId, newContent);
    }

    @Test
    void testDeleteMessage() throws Exception {
        Long messageId = 1L;

        mockMvc.perform(put("/delete/{id}", messageId))
                .andExpect(status().isOk());

        verify(chatMessageService).markMessageAsDeleted(messageId);
    }

    @Test
    void testMarkMessageAsRead() throws Exception {
        Long messageId = 1L;

        mockMvc.perform(put("/read/{id}", messageId))
                .andExpect(status().isOk());

        verify(chatMessageService).markMessageAsRead(messageId);
    }

    @Test
    void testSearchMessages() throws Exception {
        Long chatRoomId = 1L;
        String keyword = "Hello";
        //List<ChatMessage> searchResults = Arrays.asList(new ChatMessage("Hello there"));
        //when(chatMessageService.searchMessages(chatRoomId, keyword)).thenReturn(searchResults);

        mockMvc.perform(get("/search")
                        .param("chatRoomId", chatRoomId.toString())
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("Hello there"));
    }
}
