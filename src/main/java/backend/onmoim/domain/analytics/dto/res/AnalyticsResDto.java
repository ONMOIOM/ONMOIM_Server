package backend.onmoim.domain.analytics.dto.res;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public class AnalyticsResDto {
    @Builder
    public record SessionStartResDto(String sessionId){}

    @Builder
    public record SessionEndResDto(String sessionId){}

    @JsonAutoDetect
    @Builder
    public record AvgSessionDto(int minutes, int seconds) {}

    @JsonAutoDetect
    @Builder
    public record DailyAnalyticsDto (
            LocalDate date,
            int clickCount,
            int participantCount,
            AvgSessionDto avgSession,
            double participationRate
    ) {}

    @JsonAutoDetect
    @Builder
    public record GetAnalyticsListDto (
            Long eventId,
            List<DailyAnalyticsDto> stats
    ){}
}
