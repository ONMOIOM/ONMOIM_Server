package backend.onmoim.domain.analytics.controller;

import backend.onmoim.domain.analytics.code.AnalyticsSuccessCode;
import backend.onmoim.domain.analytics.converter.AnalyticsConverter;
import backend.onmoim.domain.analytics.dto.res.AnalyticsResDto;
import backend.onmoim.domain.analytics.service.AnalyticsCommandService;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.common.ApiResponse;
import backend.onmoim.global.validation.annotation.ExistEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/analytics")
public class AnalyticsController implements AnalyticsControllerDocs{

    private final AnalyticsCommandService analyticsCommendService;

    @Override
    @PostMapping("/{eventId}/session")
    public ApiResponse<AnalyticsResDto.SessionStartResDto> sessionStart(@AuthenticationPrincipal User user,@ExistEvent @PathVariable Long eventId)
    {
        Long userId=user.getId();
        String sessionId =analyticsCommendService.sessionEnter(userId,eventId);

        return ApiResponse.onSuccess(
                AnalyticsSuccessCode.REQUEST_OK,
                AnalyticsConverter.toSessionStartDTO(sessionId)
        );
    }

    @Override
    @PostMapping("/{eventId}/session/{sessionId}")
    public ApiResponse<AnalyticsResDto.SessionEndResDto> sessionEnd(@ExistEvent @PathVariable Long eventId,@PathVariable String sessionId)
    {
        analyticsCommendService.exitCount(eventId);
        analyticsCommendService.sessionExit(sessionId,eventId);

        return ApiResponse.onSuccess(
                AnalyticsSuccessCode.REQUEST_OK,
                AnalyticsConverter.toSessionEndDTO(sessionId)
        );
    }

    @Override
    @GetMapping("/total")
    public ApiResponse<AnalyticsResDto.GetAnalyticsListDto> analyticsGet(@AuthenticationPrincipal User user,@ExistEvent @RequestParam Long eventId){
        Long userId=user.getId();

        return ApiResponse.onSuccess(AnalyticsSuccessCode.REQUEST_OK,
                analyticsCommendService.analyticsGet(userId,eventId));
    }
}



