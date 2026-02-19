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
            "이미지 업로드에 실패했습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
