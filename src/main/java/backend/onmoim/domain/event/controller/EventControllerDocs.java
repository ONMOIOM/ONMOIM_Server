package backend.onmoim.domain.event.controller;

import backend.onmoim.domain.event.dto.res.EventResDTO;
import backend.onmoim.domain.event.dto.res.EventUpdateDTO;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "행사 API", description = "행사 관련 API")
public interface EventControllerDocs {
    @Operation(summary = "행사 초안 생성", description = "행사 상태를 drafted(초안) 상태로 생성합니다. 행사의 모든 필드는 null 상태로 저장됩니다.")
    public ApiResponse<EventResDTO> createDraft();

    @Operation(summary = "행사 내용 수정", description = "행사의 제목, 시간, 장소 등의 모든 정보를 부분 수정합니다.")
    public ApiResponse<EventResDTO> patchEvent
            (@Parameter(description = "수정할 행사 ID", required = true, example = "1") @PathVariable Long eventId,
             @RequestBody EventUpdateDTO updates);

    @Operation(summary = "행사 최종 생성", description = "행사 상태를 published(최종 생성) 상태로 수정하고 Analytics 테이블을 생성합니다.")
    public ApiResponse<EventResDTO> publishEvent(
            @Parameter(description = "발행할 행사 ID", required = true, example = "1")
            @PathVariable Long eventId);

    @Operation(summary = "내가 참여한 행사 조회", description = "현재 로그인한 사용자가 참여(투표 상태 = ATTEND)한 행사 목록을 조회합니다.")
    public ApiResponse<List<EventResDTO>> getUserEvents(@AuthenticationPrincipal User user);
}
