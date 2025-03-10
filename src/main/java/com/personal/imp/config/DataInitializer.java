package com.personal.imp.config;

import com.personal.imp.model.Role;
import com.personal.imp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @Bean
    CommandLineRunner init() {
        return args -> {
            if (roleRepository.findByName(Role.RoleName.ROLE_USER).isEmpty()) {
                roleRepository.save(new Role(Role.RoleName.ROLE_USER));
            }
            if (roleRepository.findByName(Role.RoleName.ROLE_ADMIN).isEmpty()) {
                roleRepository.save(new Role(Role.RoleName.ROLE_ADMIN));
            }
            if (roleRepository.findByName(Role.RoleName.ROLE_BUSINESS_PARTNER).isEmpty()) {
                roleRepository.save(new Role(Role.RoleName.ROLE_BUSINESS_PARTNER));
            }
        };
    }
}
