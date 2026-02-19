package backend.onmoim.domain.analytics.code;

import backend.onmoim.global.common.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AnalyticsErrorCode implements BaseErrorCode {
    ANALYTICS_NOT_FOUND(HttpStatus.NOT_FOUND,"ANALYTICS_400", "해당 Analytics 데이터를 찾을 수 없습니다."),
    REDIS_SAVE_FAIL(HttpStatus.BAD_REQUEST,"ANALYTICS_401", "Redis에 세션 저장 실패"),
    BAD_EVENT_ID(HttpStatus.NOT_FOUND,"ANALYTICS_402", "해당 eventID 데이터를 찾을 수 없습니다."),
    REDIS_NOT_FOUND(HttpStatus.NOT_FOUND,"ANALYTICS_403","SESSION에 저장되지 않는 키입니다"),
    REDIS_DESERIALIZE_FAIL(HttpStatus.BAD_REQUEST,"ANALYTICS_405","역직렬화에 실패했습니다"),
    NOT_HOST(HttpStatus.UNAUTHORIZED,"ANALYTICS_406","행사의 호스트가 아닙니다");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
