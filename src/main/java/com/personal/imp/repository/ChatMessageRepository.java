package com.personal.imp.repository;

import com.personal.imp.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
    List<ChatMessage> findByContentContainingAndChatRoomId(String content, Long chatRoomId);
}
