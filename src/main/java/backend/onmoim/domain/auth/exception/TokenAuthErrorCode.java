package backend.onmoim.domain.auth.exception;

import backend.onmoim.global.common.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TokenAuthErrorCode implements BaseErrorCode {
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,
            "TOKEN_401",
            "존재하지 않는 Token입니다."),
    INVALID_TOKEN_FORMAT(HttpStatus.UNAUTHORIZED,
            "TOKEN_400",
            "유효하지 않은 Token 형식입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,
            "REFRESH_401",
            "Refresh Token이 존재하지 않습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,
            "REFRESH_402",
            "Refresh Token이 만료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
