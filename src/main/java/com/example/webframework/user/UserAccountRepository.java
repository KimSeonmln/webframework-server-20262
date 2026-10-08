// user/UserAccountRepository

package com.example.webframework.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccountEntity, Long> {

    Optional<UserAccountEntity> findByIdAndDeleted(Long id, Boolean deleted);

    List<UserAccountEntity> findByEmail(String email);
    Optional<UserAccountEntity> findByEmailAndDeleted(String email, boolean deleted);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
