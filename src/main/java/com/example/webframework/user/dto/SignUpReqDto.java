package com.example.webframework.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Locale;

public record SignUpReqDto(
        @NotBlank(message = "이메일을 입력하시오.")
        @Email(message = "올바른 이메일 형식으로 입력하시오.")
        @Size(max = 100, message = "이메일은 100자 이하로 입력하시오.")
        String email,

        @NotBlank(message = "비밀번호를 입력하시오.")
        @Size(min = 8, max = 24, message = "비밀번호는 8~24자 사이로 입력하시오.")
        String password,

        @NotBlank(message = "닉네임을 입력하시오.")
        @Size(min = 2, max = 16, message = "닉네임은 2~16자 사이로 입력하시오.")
        String nickname
) {
    public SignUpReqDto {
        email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
        nickname = nickname == null ? null : nickname.trim();
    }

    // 비밀번호 형식 체크는 다음에
}
