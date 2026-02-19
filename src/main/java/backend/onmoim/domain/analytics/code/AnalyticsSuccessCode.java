package backend.onmoim.domain.analytics.code;

import backend.onmoim.global.common.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AnalyticsSuccessCode implements BaseSuccessCode {
    REQUEST_OK(HttpStatus.OK,
            "SESSION_200",
            "세션이 성공적으로 처리되었습니다."),
    GET_ANALYTICS(HttpStatus.OK,"ANALYTICS_201","통계 정보를 성공적으로 불러왔습니다");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
