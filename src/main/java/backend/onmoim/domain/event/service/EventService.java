package backend.onmoim.domain.event.service;

import backend.onmoim.domain.event.dto.req.VoteRequest;
import backend.onmoim.domain.event.dto.res.EventDetailResponse;
import backend.onmoim.domain.event.dto.res.EventListResponse;
import backend.onmoim.domain.event.dto.res.EventResDTO;
import backend.onmoim.domain.event.dto.res.EventUpdateDTO;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.domain.event.dto.res.ParticipantDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {
    EventResDTO createDraftEvent(User user);
    EventResDTO patchEvent(Long eventID, EventUpdateDTO updateDTO,User user);
    EventResDTO publishEvent(Long eventID, User user);

    EventDetailResponse getEventDetail(Long eventId);
    List<EventListResponse> getEvents();
    void deleteEvent(Long eventId, User user);
    void castVote(Long eventId, User user, VoteRequest request);

    List<EventResDTO> getUserParticipatingEvents(Long userId);
    List<EventResDTO> getUserHostedEvents(Long userId);
    List<ParticipantDto> getParticipants(Long eventId);
    
    String uploadEventImage(Long eventId, User user, MultipartFile image);
}
