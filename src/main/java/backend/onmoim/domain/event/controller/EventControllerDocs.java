package backend.onmoim.domain.event.controller;

import backend.onmoim.domain.event.dto.req.VoteRequest;
import backend.onmoim.domain.event.dto.res.EventDetailResponse;
import backend.onmoim.domain.event.dto.res.EventListResponse;
import backend.onmoim.domain.event.dto.res.EventResDTO;
import backend.onmoim.domain.event.dto.res.EventUpdateDTO;
import backend.onmoim.domain.event.dto.res.ParticipantDto;
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
    public ApiResponse<EventResDTO> createDraft(
            @AuthenticationPrincipal User user);

    @Operation(summary = "행사 내용 수정", description = "행사의 제목, 시간, 장소 등의 모든 정보를 부분 수정합니다.")
    public ApiResponse<EventResDTO> patchEvent
            (@Parameter(description = "수정할 행사 ID", required = true, example = "1") @PathVariable Long eventId,
             @RequestBody EventUpdateDTO updates,
             @AuthenticationPrincipal User user);

    @Operation(summary = "행사 최종 생성", description = "행사 상태를 published(최종 생성) 상태로 수정하고 Analytics 테이블을 생성합니다.")
    public ApiResponse<EventResDTO> publishEvent(
            @Parameter(description = "발행할 행사 ID", required = true, example = "1")
            @PathVariable Long eventId,
            @AuthenticationPrincipal User user);

    @Operation(summary = "전체 행사 목록 조회", description = "모든 행사 목록을 조회합니다.")
    public ApiResponse<List<EventListResponse>> getEvents();

    @Operation(summary = "행사 상세 조회", description = "특정 행사의 상세 정보를 조회합니다.")
    public ApiResponse<EventDetailResponse> getEventDetail(
            @Parameter(description = "조회할 행사 ID", required = true, example = "1")
            @PathVariable Long eventId);

    @Operation(summary = "행사 참여 투표", description = "행사에 참여 의사를 투표합니다. (ATTEND: 참여, PENDING: 고민중)")
    public ApiResponse<String> castVote(
            @Parameter(description = "투표할 행사 ID", required = true, example = "1")
            @PathVariable Long eventId,
            @AuthenticationPrincipal User user,
            @RequestBody VoteRequest request);

    @Operation(summary = "행사 참여자 목록 조회", description = "특정 행사의 참여자 목록과 투표 상태를 조회합니다.")
    public ApiResponse<List<ParticipantDto>> getParticipants(
            @Parameter(description = "조회할 행사 ID", required = true, example = "1")
            @PathVariable Long eventId);

    @Operation(summary = "행사 삭제", description = "행사를 삭제합니다. 호스트만 삭제할 수 있습니다.")
    public ApiResponse<Void> deleteEvent(
            @Parameter(description = "삭제할 행사 ID", required = true, example = "1")
            @PathVariable Long eventId,
            @AuthenticationPrincipal User user);

    @Operation(summary = "내가 참여한 행사 조회", description = "현재 로그인한 사용자가 참여(투표 상태 = ATTEND)한 행사 목록을 조회합니다.")
    public ApiResponse<List<EventResDTO>> getParticipatingEvents(@AuthenticationPrincipal User user);

    @Operation(summary = "내가 만든 행사 조회", description = "현재 로그인한 사용자가 호스트로 생성한 행사 목록을 조회합니다.")
    public ApiResponse<List<EventResDTO>> getHostedEvents(@AuthenticationPrincipal User user);
}
