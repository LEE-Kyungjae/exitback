package com.personal.imp.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebSocketConfigTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private WebSocketConfig webSocketConfig;

    @Test
    void testMessageBrokerConfiguration() {
        // Given: MessageBrokerRegistry 모의 객체 생성
        MessageBrokerRegistry registry = new MessageBrokerRegistry(null);

        // When: webSocketConfig의 configureMessageBroker 호출
        webSocketConfig.configureMessageBroker(registry);

        // Then: 메시지 브로커 설정 확인
        assertEquals("/topic", registry.getSimpleBrokerDestinationPrefixes().get(0), "SimpleBroker prefix가 예상과 다릅니다.");
        assertEquals("/app", registry.setApplicationDestinationPrefixes().get(0), "Application Destination prefix가 예상과 다릅니다.");
    }

    @Test
    void testStompEndpointConfiguration() {
        // Given: StompEndpointRegistry 모의 객체 생성
        StompEndpointRegistry registry = new StompEndpointRegistry(null, null);

        // When: webSocketConfig의 registerStompEndpoints 호출
        webSocketConfig.registerStompEndpoints(registry);

        // Then: 엔드포인트가 설정되었는지 확인
        assertEquals("/ws", registry.getEndpoints().iterator().next().getPath(), "STOMP 엔드포인트가 예상과 다릅니다.");
    }
}