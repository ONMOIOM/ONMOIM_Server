package backend.onmoim.domain.user.service;

import backend.onmoim.domain.user.entity.User;
import backend.onmoim.domain.user.enums.Status;
import backend.onmoim.domain.user.exception.UserErrorCode;
import backend.onmoim.domain.user.exception.UserException;
import backend.onmoim.domain.user.repository.UserRepository;
import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import backend.onmoim.global.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public void withdraw(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (user.getStatus() != Status.ACTIVE) {
            throw new UserException(UserErrorCode.USER_INACTIVE);
        }

        user.withdraw();

        // Refresh 토큰 무효화
        jwtUtil.invalidateRefreshToken(userId);

        // 7일 재가입 차단 (Redis)
        String banKey = "ban:email:" + user.getEmail();
        redisTemplate.opsForValue().set(
                banKey,
                "withdrawn:" + LocalDateTime.now(),
                Duration.ofDays(7)
        );

        log.info("사용자ID {} 탈퇴 처리됨, 7일간 재가입 차단 ({}까지)",
                userId, LocalDateTime.now().plusDays(7));
    }
}
