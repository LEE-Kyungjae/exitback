package com.personal.imp.controller;

import com.personal.imp.model.kakao.KakaoUser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.personal.imp.model.User;
import com.personal.imp.service.JwtTokenService;
import com.personal.imp.service.UserService;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenService jwtTokenService;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
        }
        userService.saveLocalUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenService.generateToken(authentication.getName(), Collections.singleton("USER"));
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException e) {
            // 추가된 로그 메시지
            System.out.println("로그인 실패 - 잘못된 자격 증명: 이메일 = " + user.getEmail() + ", 비밀번호 = " + user.getPassword());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }


    @GetMapping("/verifyToken")
    public boolean verifyToken(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = jwtTokenService.extractUsernameFromToken(token);
        UserDetails userDetails = userService.loadUserByUsername(email);
        return jwtTokenService.validateToken(token, userDetails);
    }

    @GetMapping("/check_email")
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        boolean isAvailable = userService.findByEmail(email).isEmpty();
        return Collections.singletonMap("available", isAvailable);
    }

    @PostMapping("/oauth")
    public ResponseEntity<String> kakaoLogin(@RequestBody String token) throws IOException {
        Request request = new Request.Builder()
                .url("https://kapi.kakao.com/v2/user/me")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            KakaoUser kakaoUser = gson.fromJson(response.body().string(), KakaoUser.class);

            // Check if email already exists in the system
            User user = userService.findByEmail(kakaoUser.getKakao_account().getEmail()).orElse(null);

            if (user == null) {
                // User does not exist, perform registration
                user = new User();
                user.setEmail(kakaoUser.getKakao_account().getEmail());
                user.setProfilePictureUrl(kakaoUser.getKakao_account().getProfile().getProfile_image_url());
                user.setGender(User.Gender.valueOf(kakaoUser.getKakao_account().getGender().toUpperCase()));
                user.setPhoneNumber(kakaoUser.getKakao_account().getPhone_number());
                userService.saveKakaoUser(user, kakaoUser.getKakao_account().getProfile().getNickname());
            } else if (user.isLocalAccount()) {
                // User exists and is a local account
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered with local account");
            } else {
                // User exists and is a Kakao account, update last login time
                user.setLastLogin(new java.util.Date());
                userService.saveKakaoUser(user, kakaoUser.getKakao_account().getProfile().getNickname());
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return ResponseEntity.ok(jwtTokenService.generateToken(user.getEmail(), Collections.singleton("USER")));
        }
    }
}
