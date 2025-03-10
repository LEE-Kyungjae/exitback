package com.personal.imp;

import com.personal.imp.model.User;
import com.personal.imp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateUser() {
        // Given: 새 사용자 정보 생성
        User newUser = new User();
        newUser.setName("John Doe");
        newUser.setEmail("johndoe@example.com");
        HttpEntity<User> request = new HttpEntity<>(newUser);

        // When: 사용자 등록 API 호출
        ResponseEntity<User> response = restTemplate.postForEntity("http://localhost:" + port + "/api/users", request, User.class);

        // Then: 상태 코드 확인 및 데이터베이스 저장 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        User savedUser = userRepository.findByEmail("johndoe@example.com").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("John Doe");
    }
}
