package backend.onmoim.domain.event.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateDTO {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String streetAddress;
    private String lotNumberAddress;
    private String playlistUrl;
    private Integer capacity;
    private Integer price;
    private String introduction;
}
