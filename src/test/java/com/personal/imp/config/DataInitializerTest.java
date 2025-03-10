package com.personal.imp.config;

import com.personal.imp.model.Role;
import com.personal.imp.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DataInitializerTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private DataInitializer dataInitializer;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRolesAreCreatedWhenNotExist() throws Exception {
        // Given: RoleRepository에 아무 역할도 존재하지 않는 상태
        when(roleRepository.findByName(Role.RoleName.ROLE_USER)).thenReturn(Optional.empty());
        when(roleRepository.findByName(Role.RoleName.ROLE_ADMIN)).thenReturn(Optional.empty());
        when(roleRepository.findByName(Role.RoleName.ROLE_BUSINESS_PARTNER)).thenReturn(Optional.empty());

        // When: DataInitializer의 init() 메서드 실행
        dataInitializer.init().run();

        // Then: 각 역할이 한 번씩 저장되었는지 확인
        verify(roleRepository, times(1)).save(new Role(Role.RoleName.ROLE_USER));
        verify(roleRepository, times(1)).save(new Role(Role.RoleName.ROLE_ADMIN));
        verify(roleRepository, times(1)).save(new Role(Role.RoleName.ROLE_BUSINESS_PARTNER));
    }

    @Test
    void testRolesAreNotDuplicatedIfAlreadyExist() throws Exception {
        // Given: RoleRepository에 ROLE_USER, ROLE_ADMIN, ROLE_BUSINESS_PARTNER가 이미 존재하는 상태
        when(roleRepository.findByName(Role.RoleName.ROLE_USER)).thenReturn(Optional.of(new Role(Role.RoleName.ROLE_USER)));
        when(roleRepository.findByName(Role.RoleName.ROLE_ADMIN)).thenReturn(Optional.of(new Role(Role.RoleName.ROLE_ADMIN)));
        when(roleRepository.findByName(Role.RoleName.ROLE_BUSINESS_PARTNER)).thenReturn(Optional.of(new Role(Role.RoleName.ROLE_BUSINESS_PARTNER)));

        // When: DataInitializer의 init() 메서드 실행
        dataInitializer.init().run();

        // Then: 이미 존재하는 역할은 저장되지 않도록 확인
        verify(roleRepository, never()).save(new Role(Role.RoleName.ROLE_USER));
        verify(roleRepository, never()).save(new Role(Role.RoleName.ROLE_ADMIN));
        verify(roleRepository, never()).save(new Role(Role.RoleName.ROLE_BUSINESS_PARTNER));
    }
}
