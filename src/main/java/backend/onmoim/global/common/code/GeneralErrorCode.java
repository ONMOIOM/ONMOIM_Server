package backend.onmoim.global.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralErrorCode implements BaseErrorCode{

    BAD_REQUEST(HttpStatus.BAD_REQUEST,
            "COMMON_400",
            "잘못된 요청입니다."),
    VALID_FAIL(HttpStatus.BAD_REQUEST,
            "COMMON_401",
            "유효성 검사에 실패했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON_500",
            "서버 연결에 실패했습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,
            "COMMON_403",
            "인증되지 않은 사용자입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,
            "MEMBER_404",
            "존재하지 않는 사용자입니다."),
    USER_INACTIVE(HttpStatus.FORBIDDEN,
            "MEMBER_403",
            "비활성화된 사용자입니다."),
    NICKNAME_GENERATION_FAILED(HttpStatus.BAD_REQUEST,
            "NICKNAME_400",
            "닉네임을 생성에 실패했습니다. (중복 닉네임)"),
    DUPLICATE_MEMBER(
            HttpStatus.CONFLICT,
            "MEMBER_409",
            "이미 존재하는 사용자입니다."),
    INVALID_IMAGE(HttpStatus.BAD_REQUEST,
            "IMAGE_400",
            "이미지가 없습니다."),
    IMAGE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST,
            "IMAGE_401",
            "이미지 크기가 10MB를 초과합니다."),
    INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST,
            "IMAGE_402",
            "지원하지 않는 이미지 형식입니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,
            "IMAGE_500",
            "이미지 업로드에 실패했습니다."),
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND,
            "EVENT_404",
                    "존재하지 않는 행사입니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
