package backend.onmoim.domain.analytics.controller;

import backend.onmoim.domain.analytics.dto.res.AnalyticsResDto;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.common.ApiResponse;
import backend.onmoim.global.validation.annotation.ExistEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Tag(name = "Event 통계 API", description = "통계 관련 API")
public interface AnalyticsControllerDocs {
    @Operation(summary = "입장시간 기록 및 카운트", description = "입장 시간을 기록합니다.")
    public ApiResponse<AnalyticsResDto.SessionStartResDto> sessionStart(@AuthenticationPrincipal User user,@ExistEvent @PathVariable Long eventId);

    @Operation(summary = "퇴장 시간 기록 및 머문 시간 계산", description = "머문 시간을 측정하고 click 수를 카운트 합니다 평균을 계산합니다")
    public ApiResponse<AnalyticsResDto.SessionEndResDto> sessionEnd(@ExistEvent @PathVariable Long eventId,@PathVariable String sessionId);


    @Operation(summary = "일주일치 통계 자료 획득", description = "일주일치 통계자료를 일별로 획득합니다.")
    public ApiResponse<AnalyticsResDto.GetAnalyticsListDto> analyticsGet(@AuthenticationPrincipal User user,@ExistEvent @RequestParam Long eventId);
}
