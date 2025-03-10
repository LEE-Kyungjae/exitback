package com.personal.imp.controller;

import com.personal.imp.model.ChatMessage;
import com.personal.imp.service.ChatMessageService;
import com.personal.imp.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ChatMessageService chatMessageService;

    @PostMapping("/send")
    public void sendMessage(@RequestBody ChatMessage message) {
        kafkaProducerService.sendMessage(message);
    }

    @GetMapping("/messages")
    public List<ChatMessage> getAllMessages(){
        return chatMessageService.getAllMessages();
    }

    @PutMapping("/edit/{id}")
    public void editMessage(@PathVariable Long id, @RequestParam String newContent) {
        chatMessageService.updateMessageContent(id, newContent);
    }


    @PutMapping("/delete/{id}")
    public void deleteMessage(@PathVariable Long id) {
        chatMessageService.markMessageAsDeleted(id);
    }

    @PutMapping("/read/{id}")
    public void markMessageAsRead(@PathVariable Long id) {
        chatMessageService.markMessageAsRead(id);
    }
    @GetMapping("/search")
    public List<ChatMessage> searchMessages(@RequestParam Long chatRoomId, @RequestParam String keyword) {
        return chatMessageService.searchMessages(chatRoomId, keyword);
    }

}
