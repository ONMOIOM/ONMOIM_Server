package backend.onmoim.domain.event.service;

import backend.onmoim.domain.event.converter.EventConverter;
import backend.onmoim.domain.event.dto.req.VoteRequest;
import backend.onmoim.domain.event.dto.res.EventDetailResponse;
import backend.onmoim.domain.event.dto.res.EventListResponse;
import backend.onmoim.domain.event.dto.res.EventResDTO;
import backend.onmoim.domain.event.dto.res.EventUpdateDTO;
import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.event.entity.EventMember;
import backend.onmoim.domain.event.enums.Status;
import backend.onmoim.domain.event.repository.EventMemberRepository;
import backend.onmoim.domain.event.repository.EventRepository;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.domain.user.repository.UserRepository;
import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import backend.onmoim.global.utils.MinioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import backend.onmoim.domain.analytics.service.AnalyticsCommandService;
import backend.onmoim.domain.event.dto.res.ParticipantDto;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;
    private final UserRepository userRepository;
    private final AnalyticsCommandService analyticsCommandService;
    private final MinioUtil minioUtil;

    @Override
    public EventResDTO createDraftEvent(User user) {
        Event eventEntity = Event.builder()
                .status(Status.DRAFTED)
                .host(user)
                .build();

        Event saved = eventRepository.save(eventEntity);
        return EventConverter.toResDTO(saved);
    }

    @Override
    @Transactional
    public EventResDTO patchEvent(Long eventId, EventUpdateDTO updateDTO,User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.EVENT_NOT_FOUND));

        Event updatedevent = event.update(
                updateDTO.getTitle(),
                updateDTO.getStartTime(),
                updateDTO.getEndTime(),
                updateDTO.getStreetAddress(),
                updateDTO.getLotNumberAddress(),
                updateDTO.getPrice(),
                updateDTO.getPlaylistUrl(),
                updateDTO.getCapacity(),
                updateDTO.getIntroduction(),
                user
        );

        Event saved = eventRepository.save(updatedevent);
        return EventConverter.toResDTO(saved);
    }

    @Override
    @Transactional
    public EventResDTO publishEvent(Long eventID, User user) {
        Event event = eventRepository.findById(eventID)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.EVENT_NOT_FOUND));
        Event publishedEvent = event.publish(user);
        Event saved = eventRepository.save(publishedEvent);

        analyticsCommandService.createTodayAnalyticsTable(saved);
        return EventConverter.toResDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDetailResponse getEventDetail(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.EVENT_NOT_FOUND));
        
        String imageUrl = null;
        String hostImageUrl = null;
        
        try {
            imageUrl = minioUtil.getEventImageUrl(eventId);
        } catch (Exception e) {
            log.warn("행사 이미지 URL 생성 실패: {}", e.getMessage());
        }
        
        if (event.getHost() != null) {
            try {
                hostImageUrl = minioUtil.getProfileImageUrl(event.getHost().getId());
            } catch (Exception e) {
                log.warn("호스트 이미지 URL 생성 실패: {}", e.getMessage());
            }
        }
        
        return EventDetailResponse.from(event, imageUrl, hostImageUrl);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventListResponse> getEvents() {
        return eventRepository.findAll().stream()
                .map(event -> {
                    EventListResponse response = EventListResponse.from(event);
                    String imageUrl = null;
                    String hostImageUrl = null;
                    
                    try {
                        imageUrl = minioUtil.getEventImageUrl(event.getId());
                    } catch (Exception e) {
                        log.warn("행사 이미지 URL 생성 실패 (eventId: {}): {}", event.getId(), e.getMessage());
                    }
                    
                    if (event.getHost() != null) {
                        try {
                            hostImageUrl = minioUtil.getProfileImageUrl(event.getHost().getId());
                        } catch (Exception e) {
                            log.warn("호스트 이미지 URL 생성 실패 (userId: {}): {}", event.getHost().getId(), e.getMessage());
                        }
                    }
                    
                    return EventListResponse.builder()
                            .eventId(response.getEventId())
                            .status(response.getStatus())
                            .title(response.getTitle())
                            .schedule(response.getSchedule())
                            .location(response.getLocation())
                            .capacity(response.getCapacity())
                            .playlist(response.getPlaylist())
                            .price(response.getPrice())
                            .information(response.getInformation())
                            .hostName(response.getHostName())
                            .hostImageUrl(hostImageUrl)
                            .imageUrl(imageUrl)
                            .createdAt(response.getCreatedAt())
                            .updatedAt(response.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEvent(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.EVENT_NOT_FOUND));
        if (!event.getHost().getId().equals(user.getId())) {
            throw new GeneralException(GeneralErrorCode.BAD_REQUEST);
        }
        eventRepository.delete(event);
    }

    @Override
    @Transactional
    public void castVote(Long eventId, User user, VoteRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.EVENT_NOT_FOUND));

        eventMemberRepository.findByUserAndEvent(user, event)
                .ifPresentOrElse(
                        existingMember -> existingMember.updateStatus(request.getStatus()),
                        () -> {
                            EventMember newMember = EventMember.builder()
                                    .user(user)
                                    .event(event)
                                    .status(request.getStatus())
                                    .build();
                            eventMemberRepository.save(newMember);
                        }
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResDTO> getUserParticipatingEvents(Long userId){
        List<Event> events = eventMemberRepository.findEventByUserId(userId);
        return events.stream()
                .map(event -> {
                    EventResDTO dto = EventConverter.toResDTO(event);
                    String imageUrl = null;
                    String hostImageUrl = null;
                    
                    try {
                        imageUrl = minioUtil.getEventImageUrl(event.getId());
                    } catch (Exception e) {
                        log.warn("행사 이미지 URL 생성 실패 (eventId: {}): {}", event.getId(), e.getMessage());
                    }
                    
                    if (event.getHost() != null) {
                        try {
                            hostImageUrl = minioUtil.getProfileImageUrl(event.getHost().getId());
                        } catch (Exception e) {
                            log.warn("호스트 이미지 URL 생성 실패 (userId: {}): {}", event.getHost().getId(), e.getMessage());
                        }
                    }
                    
                    return EventResDTO.builder()
                            .eventId(dto.getEventId())
                            .title(dto.getTitle())
                            .startTime(dto.getStartTime())
                            .endTime(dto.getEndTime())
                            .streetAddress(dto.getStreetAddress())
                            .lotNumberAddress(dto.getLotNumberAddress())
                            .price(dto.getPrice())
                            .playlistUrl(dto.getPlaylistUrl())
                            .capacity(dto.getCapacity())
                            .introduction(dto.getIntroduction())
                            .status(dto.getStatus())
                            .imageUrl(imageUrl)
                            .host(event.getHost() != null ? EventResDTO.HostInfo.builder()
                                    .hostId(event.getHost().getId())
                                    .hostName(event.getHost().getNickname())
                                    .hostImageUrl(hostImageUrl)
                                    .build() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResDTO> getUserHostedEvents(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));
        
        List<Event> events = eventRepository.findByHost(user);
        return events.stream()
                .map(event -> {
                    EventResDTO dto = EventConverter.toResDTO(event);
                    String imageUrl = null;
                    String hostImageUrl = null;
                    
                    try {
                        imageUrl = minioUtil.getEventImageUrl(event.getId());
                    } catch (Exception e) {
                        log.warn("행사 이미지 URL 생성 실패 (eventId: {}): {}", event.getId(), e.getMessage());
                    }
                    
                    try {
                        hostImageUrl = minioUtil.getProfileImageUrl(user.getId());
                    } catch (Exception e) {
                        log.warn("호스트 이미지 URL 생성 실패 (userId: {}): {}", user.getId(), e.getMessage());
                    }
                    
                    return EventResDTO.builder()
                            .eventId(dto.getEventId())
                            .title(dto.getTitle())
                            .startTime(dto.getStartTime())
                            .endTime(dto.getEndTime())
                            .streetAddress(dto.getStreetAddress())
                            .lotNumberAddress(dto.getLotNumberAddress())
                            .price(dto.getPrice())
                            .playlistUrl(dto.getPlaylistUrl())
                            .capacity(dto.getCapacity())
                            .introduction(dto.getIntroduction())
                            .status(dto.getStatus())
                            .imageUrl(imageUrl)
                            .host(EventResDTO.HostInfo.builder()
                                    .hostId(user.getId())
                                    .hostName(user.getNickname())
                                    .hostImageUrl(hostImageUrl)
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipantDto> getParticipants(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.EVENT_NOT_FOUND));

        return eventMemberRepository.findAllByEvent(event).stream()
                .map(member -> ParticipantDto.from(member, minioUtil))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String uploadEventImage(Long eventId, User user, MultipartFile image) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.EVENT_NOT_FOUND));
        
        // 호스트만 이미지 업로드 가능
        if (!event.getHost().getId().equals(user.getId())) {
            throw new GeneralException(GeneralErrorCode.BAD_REQUEST);
        }
        
        validateImage(image);
        
        try {
            minioUtil.uploadEventImage(image, eventId);
            return minioUtil.getEventImageUrl(eventId);
        } catch (Exception e) {
            log.error("행사 이미지 업로드 실패: {}", e.getMessage(), e);
            throw new GeneralException(GeneralErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new GeneralException(GeneralErrorCode.INVALID_IMAGE);
        }

        long size = image.getSize();
        if (size > 10 * 1024 * 1024) { // 10MB 제한
            throw new GeneralException(GeneralErrorCode.IMAGE_SIZE_EXCEEDED);
        }

        // 파일 형식 검증
        String contentType = image.getContentType();
        if (contentType == null || !isAllowedImageType(contentType)) {
            throw new GeneralException(GeneralErrorCode.INVALID_IMAGE_TYPE);
        }
    }

    private boolean isAllowedImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif");
    }

}