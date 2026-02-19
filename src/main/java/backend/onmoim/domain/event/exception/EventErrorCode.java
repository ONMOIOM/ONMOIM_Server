package backend.onmoim.domain.event.exception;

import backend.onmoim.global.common.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EventErrorCode implements BaseErrorCode {
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND,
            "EVENT_404",
            "존재하지 않는 행사입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
