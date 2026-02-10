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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import backend.onmoim.domain.analytics.service.AnalyticsCommandService;
import backend.onmoim.domain.event.dto.res.ParticipantDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;
    private final UserRepository userRepository;
    private final AnalyticsCommandService analyticsCommandService;

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
        return EventDetailResponse.from(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventListResponse> getEvents() {
        return eventRepository.findAll().stream()
                .map(EventListResponse::from)
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
        return  events.stream()
                .map(EventConverter::toResDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResDTO> getUserHostedEvents(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));
        
        List<Event> events = eventRepository.findByHost(user);
        return events.stream()
                .map(EventConverter::toResDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipantDto> getParticipants(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.EVENT_NOT_FOUND));

        return eventMemberRepository.findAllByEvent(event).stream()
                .map(ParticipantDto::from)
                .collect(Collectors.toList());
    }

}