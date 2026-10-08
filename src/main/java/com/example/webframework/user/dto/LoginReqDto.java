package com.example.webframework.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Locale;

public record LoginReqDto(
        @NotBlank(message = "이메일을 입력해주세요")
        @Email(message = "올바른 이메일 양식을 입력해주세요")
        @Size(max = 100)
        String email,

        @NotBlank(message = "비밀먼호를 입력해주세요")
        @Size(max = 64)
        String password
) {
    public LoginReqDto {
        email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);

    }
}
