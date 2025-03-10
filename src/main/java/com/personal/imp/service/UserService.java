package com.personal.imp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.personal.imp.model.Role;
import com.personal.imp.model.User;
import com.personal.imp.repository.RoleRepository;
import com.personal.imp.repository.UserRepository;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password."));

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName().name()));
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveLocalUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Date now = new Date();
        user.setRegistrationDate(now);
        user.setLastLogin(now);
        user.setUsername(NicknameGenerator.generateNickname());

        // 기본 역할 추가
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(userRole);

        // Set local account flag
        user.setLocalAccount(true);

        return userRepository.save(user);
    }

    public User saveKakaoUser(User user, String kakaoNickname) {
        Date now = new Date();
        user.setRegistrationDate(now);
        user.setLastLogin(now);
        user.setUsername(kakaoNickname);

        // 기본 역할 추가
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(userRole);

        // Set local account flag to false
        user.setLocalAccount(false);

        return userRepository.save(user);
    }
}
