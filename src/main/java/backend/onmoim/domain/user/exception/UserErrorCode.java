package backend.onmoim.domain.user.exception;

import backend.onmoim.global.common.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,
            "MEMBER_404",
            "존재하지 않는 사용자입니다."),
    USER_INACTIVE(HttpStatus.FORBIDDEN,
            "MEMBER_403",
            "비활성화된 사용자입니다."),
    NICKNAME_GENERATION_FAILED(HttpStatus.BAD_REQUEST,
            "NICKNAME_400",
            "닉네임 생성에 실패했습니다. (중복 닉네임)"),
    DUPLICATE_MEMBER(
            HttpStatus.CONFLICT,
            "MEMBER_409",
            "이미 존재하는 사용자입니다.");



    private final HttpStatus status;
    private final String code;
    private final String message;
}
