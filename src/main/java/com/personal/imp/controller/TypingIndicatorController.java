package com.personal.imp.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class TypingIndicatorController {

    @MessageMapping("/typing")
    @SendTo("/topic/typing")
    public String sendTypingIndicator(String username) {
        return username + " is typing...";
    }
}
