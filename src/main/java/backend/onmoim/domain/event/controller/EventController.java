package backend.onmoim.domain.event.controller;

import backend.onmoim.domain.event.dto.req.VoteRequest;
import backend.onmoim.domain.event.dto.res.EventDetailResponse;
import backend.onmoim.domain.event.dto.res.EventListResponse;
import backend.onmoim.domain.event.dto.res.EventResDTO;
import backend.onmoim.domain.event.dto.res.EventUpdateDTO;
import backend.onmoim.domain.event.dto.res.ParticipantDto;
import backend.onmoim.domain.event.service.EventService;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.common.ApiResponse;
import backend.onmoim.global.common.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class EventController implements EventControllerDocs {

    private final EventService eventService;

    @PostMapping("/events")
    public ApiResponse<EventResDTO> createDraft(@AuthenticationPrincipal User user) {
        EventResDTO eventResDTO = eventService.createDraftEvent(user);
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, eventResDTO);
    }

    @PatchMapping("/events/{eventId}")
    public ApiResponse<EventResDTO> patchEvent
            (@PathVariable Long eventId, @RequestBody EventUpdateDTO updates, @AuthenticationPrincipal User user) {
        EventResDTO eventResDTO = eventService.patchEvent(eventId, updates, user);
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, eventResDTO);
    }

    @PostMapping("/events/{eventId}/published")
    public ApiResponse<EventResDTO> publishEvent(@PathVariable Long eventId, @AuthenticationPrincipal User user) {
        EventResDTO eventResDTO = eventService.publishEvent(eventId, user);
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, eventResDTO);
    }

    @GetMapping("/events")
    public ApiResponse<List<EventListResponse>> getEvents() {
        List<EventListResponse> responses = eventService.getEvents();
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, responses);
    }

    @GetMapping("/events/{eventId}")
    public ApiResponse<EventDetailResponse> getEventDetail(@PathVariable Long eventId) {
        EventDetailResponse response = eventService.getEventDetail(eventId);
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, response);
    }

    @PostMapping("/events/{eventId}/participants/{userId}")
    public ApiResponse<String> castVote(@PathVariable Long eventId,
                                        @PathVariable Long userId,
                                        @AuthenticationPrincipal User user,
                                        @RequestBody VoteRequest request) {
        eventService.castVote(eventId, user, request);
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, "투표가 완료되었습니다.");
    }

    @GetMapping("/events/{eventId}/participants")
    public ApiResponse<List<ParticipantDto>> getParticipants(@PathVariable Long eventId) {
        List<ParticipantDto> participants = eventService.getParticipants(eventId);
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, participants);
    }

    @DeleteMapping("/events/{eventId}")
    public ApiResponse<Void> deleteEvent(@PathVariable Long eventId,
                                         @AuthenticationPrincipal User user) {
        eventService.deleteEvent(eventId, user);
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, null);
    }

    @GetMapping("/{userId}/events")
    public ApiResponse<List<EventResDTO>> getUserEvents(@AuthenticationPrincipal User user) {
        List<EventResDTO> events = eventService.getUserParticipatingEvents(user.getId());
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, events);
    }
}