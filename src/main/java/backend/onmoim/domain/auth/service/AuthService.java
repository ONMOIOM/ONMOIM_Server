package backend.onmoim.domain.auth.service;

import backend.onmoim.domain.auth.dto.response.RotateTokenResponseDTO;
import backend.onmoim.domain.auth.exception.TokenAuthErrorCode;
import backend.onmoim.domain.auth.exception.TokenAuthException;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.domain.user.enums.Status;
import backend.onmoim.domain.user.exception.UserErrorCode;
import backend.onmoim.domain.user.exception.UserException;
import backend.onmoim.domain.user.repository.UserQueryRepository;
import backend.onmoim.global.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import static reactor.netty.http.HttpConnectionLiveness.log;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserQueryRepository userQueryRepository;
    private final RedisTemplate<String, String> redisTemplate;  // 블랙리스트용

    @Transactional(readOnly = true)
    public RotateTokenResponseDTO rotateAccessToken(String refreshToken) {

        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new TokenAuthException(TokenAuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 기존 검증 로직 (유효성, 블랙리스트, 사용자 확인)
        if (!jwtUtil.isValidRefreshToken(refreshToken)) {
            throw new TokenAuthException(TokenAuthErrorCode.INVALID_REFRESH_TOKEN);
        }
        Long ttl = getTokenExpiry(refreshToken);
        Boolean firstUse = redisTemplate.opsForValue()
                .setIfAbsent("blacklist:" + refreshToken, "true", ttl, TimeUnit.MILLISECONDS);
        if (Boolean.FALSE.equals(firstUse)) {
            throw new TokenAuthException(TokenAuthErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        Long userId = jwtUtil.getId(refreshToken);
        User user = userQueryRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (user.getStatus() != Status.ACTIVE) {
            SecurityContextHolder.clearContext();
            throw new UserException(UserErrorCode.USER_INACTIVE);
        }

        // 새 access + 새 refresh 생성
        String newAccessToken = jwtUtil.createAccessToken(user);
        String newRefreshToken = jwtUtil.createRefreshToken(user);

        log.info("Token rotation success for userId: {}", userId);

        return RotateTokenResponseDTO.builder()
                .newAccessToken(newAccessToken)
                .newRefreshToken(newRefreshToken)
                .build();
    }

    // 로그아웃
    public void logout(String refreshToken, HttpServletResponse response) {
        // 멱등성 유지를 위해 토큰이 없을 때에도 쿠키 삭제
        if (refreshToken == null || !jwtUtil.isValidRefreshToken(refreshToken)) {
            jwtUtil.deleteRefreshTokenCookie(response);
            return;
        }

        // Refresh 블랙리스트 추가 (만료까지)
        Long ttl = getTokenExpiry(refreshToken);
        redisTemplate.opsForValue().set("blacklist:" + refreshToken, "true", ttl, TimeUnit.MILLISECONDS);

        jwtUtil.deleteRefreshTokenCookie(response);
    }

    private Long getTokenExpiry(String token) {
        Claims claims = jwtUtil.getClaims(token);
        Date expiry = claims.getExpiration();
        return expiry.getTime() - System.currentTimeMillis();
    }

}
