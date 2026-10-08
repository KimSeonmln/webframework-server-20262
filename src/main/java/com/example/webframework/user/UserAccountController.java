package com.example.webframework.user;

import com.example.webframework.user.dto.LoginReqDto;
import com.example.webframework.user.dto.LoginResDto;
import com.example.webframework.user.dto.SignUpReqDto;
import com.example.webframework.user.dto.SignUpResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-account")
@RequiredArgsConstructor
public class UserAccountController {
    private final UserAccountService userAccountService;

    // login api
    // 이메일, 패스워드 -> jwt
    // jwt는 payload에 특정 정보들을 적을 수 있다. 최소한의 정보만 적는게 원칙. (보안상)
    @PostMapping("/login")
    public ResponseEntity<LoginResDto> login(
            @Valid @RequestBody LoginReqDto request
    ) {
        LoginResDto response = userAccountService.login(request);

        return ResponseEntity.ok()
                .header("Cache-Control", "no-store")
                .body(response);

    }
    // me api
    // 로그인한 회원 정보 조회
    // 개인정보 (번호, 생일, 이름)

    // 회원가입
    @PostMapping("/signup")
    public SignUpResDto signUp(@Valid @RequestBody SignUpReqDto signUpReqDto) {

        return userAccountService.signUp(signUpReqDto);
    }

    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String email) {
        return userAccountService.checkEmail(email);
    }

    @GetMapping("/check-nickname")
    public boolean checkNickname(@RequestParam String nickname) {
        return userAccountService.checkNickname(nickname);
    }

    // localhost:8080/user-account/add?email=202230432@email.daelim.ac.kr&password=1234&nickname=seonmin
    @GetMapping("/add")
    public String addUserAccount(String email, String password, String nickname) {
        return userAccountService.addUserAccount(email, password, nickname);
    }

    // localhost:8080/user-account/find-by-email?email=202230432@email.daelim.ac.kr
    @GetMapping("/find-by-email")
    public String findUserAccountByEmail(String email) {
        return userAccountService.getUserAccount(email);
    }
    // localhost:8080/user-account/update?email=202230432@email.daelim.ac.kr&password=12345수정&nickname=seonmin
    @GetMapping("/update")
    public String updateUserAccount(String email, String password, String nickname) {
        return userAccountService.updateUserAccount(email, password, nickname);
    }
    // localhost:8080/user-account/delete?email=202230432@email.daelim.ac.kr
    @GetMapping("/delete")
    public String deleteUserAccount(String email) {
        return userAccountService.deleteUserAccount(email);
    }

}
