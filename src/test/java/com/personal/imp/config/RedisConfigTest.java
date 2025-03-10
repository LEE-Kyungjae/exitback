package com.personal.imp.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class RedisConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void testRedisConnectionFactoryBeanExists() {
        // Given & When: ApplicationContext에서 redisConnectionFactory 빈을 가져옴
        RedisConnectionFactory factory = context.getBean(RedisConnectionFactory.class);

        // Then: redisConnectionFactory 빈이 존재하는지 확인
        assertNotNull(factory, "redisConnectionFactory 빈이 존재하지 않습니다.");
    }

    @Test
    void testRedisTemplateBeanExists() {
        // Given & When: ApplicationContext에서 redisTemplate 빈을 가져옴
        RedisTemplate<String, Object> redisTemplate = context.getBean("redisTemplate", RedisTemplate.class);

        // Then: redisTemplate 빈이 존재하는지 확인
        assertNotNull(redisTemplate, "redisTemplate 빈이 존재하지 않습니다.");
        assertInstanceOf(StringRedisSerializer.class, redisTemplate.getKeySerializer(), "KeySerializer가 예상과 다릅니다.");
        assertInstanceOf(GenericJackson2JsonRedisSerializer.class, redisTemplate.getValueSerializer(), "ValueSerializer가 예상과 다릅니다.");
    }
}
