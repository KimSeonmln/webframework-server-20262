package com.example.webframework.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {
    // passwordEncoder: 비밀번호 해시 생성과 검증 기능을 정의한 인터페이스
    // BCryptPasswordEncoder: BCrypt방식으로 실제 처리하는 구현체
    @Bean
    public PasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
