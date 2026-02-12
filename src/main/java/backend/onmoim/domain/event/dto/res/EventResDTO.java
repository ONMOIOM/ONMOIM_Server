package backend.onmoim.domain.event.dto.res;


import backend.onmoim.domain.event.enums.Status;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResDTO {
    private Long eventId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String streetAddress;
    private String lotNumberAddress;
    private Integer price;
    private String playlistUrl;
    private Integer capacity;
    private String introduction;
    private Status status;
    private String imageUrl;
    private HostInfo host;

    @Getter
    @Builder
    public static class HostInfo {
        private Long hostId;
        private String hostName;
        private String hostImageUrl;
    }
}
