package backend.onmoim.domain.auth.repository;

import backend.onmoim.domain.auth.entity.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {

    // 특정 이메일로 발송된 인증 정보중 가장 최근거 조회
    Optional<EmailAuth> findTopByEmailOrderByCreatedAtDesc(String email);
}
