package com.personal.imp.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class KafkaConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void testChatTopicBeanExists() {
        // Given & When: ApplicationContext에서 chatTopic 빈을 가져옴
        NewTopic chatTopic = context.getBean("chatTopic", NewTopic.class);

        // Then: chatTopic 빈이 존재하는지 확인
        assertNotNull(chatTopic, "chatTopic 빈이 존재하지 않습니다.");

        // 추가적인 속성 확인
        assertEquals("chat-topic", chatTopic.name(), "토픽 이름이 예상과 다릅니다.");
        assertEquals(10, chatTopic.numPartitions(), "파티션 수가 예상과 다릅니다.");
        assertEquals(1, chatTopic.replicationFactor(), "복제본 수가 예상과 다릅니다.");
    }
}
