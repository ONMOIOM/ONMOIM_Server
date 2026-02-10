package backend.onmoim.domain.event.dto.res;

import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.event.enums.EventStatus;
import backend.onmoim.domain.event.enums.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EventDetailResponse {

    private Long eventId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String introduction;
    private String streetAddress;
    private String lotNumberAddress;
    private Integer price;
    private String playlistUrl;
    private Integer capacity;
    private Status status;
    private String imageUrl;

    public static EventDetailResponse from(Event event) {
        return EventDetailResponse.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .lotNumberAddress(event.getLotNumberAddress())
                .streetAddress(event.getStreetAddress())
                .price(event.getPrice())
                .introduction(event.getIntroduction())
                .status(event.getStatus())
                .playlistUrl(event.getPlaylistUrl())
                .capacity(event.getCapacity())
                .build();
    }
}