package backend.onmoim.global.utils;

import backend.onmoim.domain.auth.exception.TokenAuthErrorCode;
import backend.onmoim.domain.auth.exception.TokenAuthException;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Duration accessExpiration;
    private final Duration refreshExpiration;
    private final RedisTemplate<String, String> redisTemplate;


    public JwtUtil(
            @Value("${jwt.token.secretKey}") String secret,
            @Value("${jwt.token.expiration.access}") Long accessExpiration,
            @Value("${jwt.token.expiration.refresh}" ) Long refreshExpiration, StringRedisTemplate redisTemplate
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = Duration.ofMillis(accessExpiration);
        this.refreshExpiration = Duration.ofMillis(refreshExpiration);
        this.redisTemplate = redisTemplate;
    }

    // AccessToken 생성
    public String createAccessToken(User user) {
        return createAccessToken(user, accessExpiration);
    }

    // RefreshToken 생성
    public String createRefreshToken(User user) {
        return createRefreshToken(user, refreshExpiration);
    }

    // access 토큰 생성
    private String createAccessToken(User user, Duration expiration) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("nickname", user.getNickname())
                .claim("tokenType","access")
                .issuedAt(Date.from(now)) // 언제 발급한지
                .expiration(Date.from(now.plus(expiration))) // 언제까지 유효한지
                .signWith(secretKey) // sign할 Key
                .compact();
    }

    // refresh 토큰 생성
    private String createRefreshToken(User user, Duration expiration) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("tokenType","refresh")
                .issuedAt(Date.from(now)) // 언제 발급한지
                .expiration(Date.from(now.plus(expiration))) // 언제까지 유효한지
                .signWith(secretKey) // sign할 Key
                .compact();
    }

    // 토큰 정보 가져오기
    public Claims getClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .clockSkewSeconds(60)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getId(String token) {
        try {
            Claims claims = getClaims(token);
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            throw new TokenAuthException(TokenAuthErrorCode.INVALID_TOKEN);
        }
    }

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true); // XSS 방지
        cookie.setSecure(false); // 개발: false / 운영: true (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge((int) refreshExpiration.toSeconds());
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

    public String getRefreshTokenCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    String value = cookie.getValue();
                    // 빈 문자, 공백 검사
                    if (value != null && !value.trim().isEmpty()) {
                        return value.trim();
                    }
                }
            }
            return null;
        }
        return null;
    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

    public boolean isValidAccessToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new TokenAuthException(TokenAuthErrorCode.INVALID_TOKEN_FORMAT);
            }
            Claims claims = getClaims(token);
            return "access".equals(claims.get("tokenType"));
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isValidRefreshToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new TokenAuthException(TokenAuthErrorCode.INVALID_TOKEN_FORMAT);
            }
            Claims claims = getClaims(token);
            return "refresh".equals(claims.get("tokenType"));
        } catch (JwtException e) {
            return false;
        }
    }

    // refresh token 무효화
    public void invalidateRefreshToken(Long userId) {
        String refreshKey = "refresh:token:" + userId;
        redisTemplate.delete(refreshKey);
    }

    public void storeRefreshToken(String key, String refreshToken) {
        redisTemplate.opsForValue().set(key, refreshToken, refreshExpiration);
    }
}