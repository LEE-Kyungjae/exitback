package com.personal.imp.service;

import com.personal.imp.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final String TOPIC = "chat-topic";

    @Autowired
    private KafkaTemplate<String, ChatMessage> kafkaTemplate;

    @Autowired
    private ChatMessageService chatMessageService;

    public void sendMessage(ChatMessage message) {
        kafkaTemplate.send(TOPIC, message);
        chatMessageService.saveMessage(message);
    }
}
