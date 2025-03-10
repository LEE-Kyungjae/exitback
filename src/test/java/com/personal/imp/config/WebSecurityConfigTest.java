package com.personal.imp.config;

import com.personal.imp.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebSecurityConfig.class)
public class WebSecurityConfigTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthFilter;

    @Test
    void testBeansExistence() {
        // PasswordEncoder, AuthenticationProvider, AuthenticationManager 빈이 존재하는지 확인
        PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
        assertNotNull(passwordEncoder, "PasswordEncoder 빈이 존재하지 않습니다.");

        AuthenticationProvider authenticationProvider = context.getBean(AuthenticationProvider.class);
        assertNotNull(authenticationProvider, "AuthenticationProvider 빈이 존재하지 않습니다.");

        AuthenticationManager authenticationManager = context.getBean(AuthenticationManager.class);
        assertNotNull(authenticationManager, "AuthenticationManager 빈이 존재하지 않습니다.");
    }

    @Test
    void testWhitelistUrlsAreAccessible() throws Exception {
        // 화이트리스트된 URL들이 접근 가능한지 테스트
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/login"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/swagger-ui.html"))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedUrlsAreUnauthorizedWithoutAuth() throws Exception {
        // 인증되지 않은 상태에서 보호된 URL이 차단되는지 테스트
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/dashboard"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        mockMvc.perform(MockMvcRequestBuilders.get("/partner/overview"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/profile"))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }
}
