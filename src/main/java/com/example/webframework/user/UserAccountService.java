package com.example.webframework.user;

import com.example.webframework.user.dto.*;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional // 서비스 전체에 걸거나, 함수마다 걸기
@RequiredArgsConstructor
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    // 내정보조회
    @Transactional(readOnly = true)
    public MeResponse me(Long accountId) {
        UserAccountEntity userAccount = userAccountRepository.findByIdAndDeleted(accountId, false)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용할 수 없는 계정입니다."));

        return new MeResponse(userAccount.getId(), userAccount.getEmail(), userAccount.getNickname());
    }

    // login
    public LoginResDto login(LoginReqDto request) {

        // BCrypt 입력 제한 : 문자 수와 UTF-8 바이트 수는 다르다.
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            // throw
            throw loginFailed();
        }

        UserAccountEntity userAccount = userAccountRepository
                .findByEmailAndDeleted(request.email(), false)
                .orElseThrow(() -> loginFailed());

        // 입력한 비밀번호와 DB에 저장된 해시를 비교
        boolean matched = passwordEncoder.matches(
                request.password(), userAccount.getPasswordHash()
        );
        if (!matched) {
            throw loginFailed();
        }

        // jwt 발행 (유효시간 15분)
        Instant now = Instant.now();
        long expiresIn = 15 * 60;

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("webframework-server")
                .subject(userAccount.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("nickname", userAccount.getNickname())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();

        String accessToken = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        return new LoginResDto(accessToken, "Bearer", expiresIn);
    }

    private ResponseStatusException loginFailed() {

        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다");
    }

    // register
    public SignUpResDto signUp(SignUpReqDto request) {
        // 이메일, 닉네임 이미 DB에 있는지 체크
        // 체크했는데 이미 있으면 에러(Exception)
        if (userAccountRepository.existsByEmail(request.email())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."
            );
        }

        if (userAccountRepository.existsByNickname(request.nickname())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."
            );
        }

        String passwordHash = passwordEncoder.encode((request.password()));
        UserAccountEntity userAccount = new UserAccountEntity(request.email(), passwordHash, request.nickname());
        UserAccountEntity saved = userAccountRepository.save(userAccount);
        return new SignUpResDto(saved.getId());
    }

    // c r u d
    // create -> insert
    public String addUserAccount(String email, String password, String nickname) {

        UserAccountEntity userAccount = new UserAccountEntity(email, password, nickname);
        userAccountRepository.save(userAccount);

        return "계정 생성 성공!";
    }

    // 이메일 중복 체크
    public boolean checkEmail(String email) {
        return userAccountRepository.existsByEmail(email);
    }
    // 닉네임 중복 체크
    public boolean checkNickname(String nickname) {
        return userAccountRepository.existsByNickname(nickname);
    }

    // read -> select
    public String getUserAccount(String email) {
        Optional<UserAccountEntity> userAccountEntityOptional = userAccountRepository.findByEmailAndDeleted(email,false);

        if (userAccountEntityOptional.isPresent()){
            return userAccountEntityOptional.get().getNickname();
        }

        return "존재하지 않는 이메일입니다.";
    }

    // update -> update
    public String updateUserAccount(String email, String password, String nickname) {
        Optional<UserAccountEntity> userAccountEntityOptional = userAccountRepository.findByEmailAndDeleted(email,false);

//      방법 1.
        UserAccountEntity userAccountEntity = userAccountRepository
                .findByEmailAndDeleted(email,false)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        userAccountEntity.setNickname(nickname);
        userAccountEntity.setPasswordHash(password);

        return "수정 성공!";

//      방법 2.
//      if (userAccountEntityOptional.isPresent()){
//          UserAccountEntity userAccount = userAccountEntityOptional.get();
//          userAccount.setNickname(nickname);
//          userAccount.setPasswordHash(password);
//      }
//      else {
//          return "존재하지 않는 이메일입니다.";
//      }
//
//      return "수정 성공!";
    }

    // delete -> delete (soft)
    public String deleteUserAccount(String email){
        Optional<UserAccountEntity> userAccountEntityOptional = userAccountRepository.findByEmailAndDeleted(email,false);

        if (userAccountEntityOptional.isPresent()) {
            UserAccountEntity userAccount = userAccountEntityOptional.get();
            // userAccountRepository.delete(userAccount); // hard delete -> error (base entity에서 막아놨음)
            userAccount.softDelete(); // soft delete
        }
        else {
            return "존재하지 않는 이메일입니다.";
        }

        return "삭제 성공!";
    }
}
