package com.personal.imp.service;

import com.personal.imp.model.ChatMessage;
import com.personal.imp.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatMessageRepository repository;

    public ChatMessage saveMessage(ChatMessage message) {
        return repository.save(message);
    }

    public List<ChatMessage> getAllMessages() {
        return repository.findAll();
    }

    public void markMessageAsDeleted(Long id) {
        ChatMessage message = chatMessageRepository.findById(id).orElseThrow(() -> new RuntimeException("Message not found"));
        message.setDeleted(true);
        chatMessageRepository.save(message);
    }

    public void updateMessageContent(Long id, String newContent) {
        ChatMessage message = chatMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setContent(newContent);
        chatMessageRepository.save(message);
    }

    public void markMessageAsRead(Long id) {
        ChatMessage message = chatMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        chatMessageRepository.save(message);
    }
    public List<ChatMessage> searchMessages(Long chatRoomId, String keyword) {
        return chatMessageRepository.findByContentContainingAndChatRoomId(keyword, chatRoomId);
    }

}