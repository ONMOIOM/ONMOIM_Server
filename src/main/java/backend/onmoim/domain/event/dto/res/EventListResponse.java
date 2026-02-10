package backend.onmoim.domain.event.dto.res;

import backend.onmoim.domain.event.entity.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EventListResponse {

    private Long eventId;
    private String status;
    private String title;
    private Schedule schedule;
    private Location location;
    private Integer capacity;
    private Integer price;
    private String playlist;
    private String information;
    private String hostName;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EventListResponse from(Event event) {
        return EventListResponse.builder()
                .eventId(event.getId())
                .status(event.getStatus() != null ? event.getStatus().name() : null)
                .title(event.getTitle())
                .schedule(Schedule.builder()
                        .startDate(event.getStartTime())
                        .endDate(event.getEndTime())
                        .build())

                .location(Location.builder()
                        .streetAddress(event.getStreetAddress())
                        .lotNumber(event.getLotNumberAddress())
                        .build())

                .capacity(event.getCapacity())
                .playlist(event.getPlaylistUrl())
                .price(event.getPrice())
                .information(event.getIntroduction())
                .hostName(event.getHost() != null ? event.getHost().getNickname() : null)
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    @Getter
    @Builder
    public static class Schedule {
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }

    @Getter
    @Builder
    public static class Location {
        private String streetAddress;
        private String lotNumber;
    }
}