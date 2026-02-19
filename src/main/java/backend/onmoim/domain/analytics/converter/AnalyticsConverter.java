package backend.onmoim.domain.analytics.converter;

import backend.onmoim.domain.analytics.dto.res.AnalyticsResDto;
import backend.onmoim.domain.analytics.entity.Analytics;

public class AnalyticsConverter {

    public static AnalyticsResDto.SessionStartResDto toSessionStartDTO(
            String sessionId
    ){
        return AnalyticsResDto.SessionStartResDto.builder()
                .sessionId(sessionId)
                .build();
    }

    public static AnalyticsResDto.SessionEndResDto toSessionEndDTO(
            String sessionId
    ){
        return AnalyticsResDto.SessionEndResDto.builder()
                .sessionId(sessionId)
                .build();
    }

    public static AnalyticsResDto.DailyAnalyticsDto toDailyDto(Analytics a, int totalParticipants) {
        long totalSeconds = a.getAvgSessionTimeSec();
        int minutes = (int) totalSeconds / 60;
        int seconds = (int) totalSeconds % 60;

        return AnalyticsResDto.DailyAnalyticsDto.builder()
                .date(a.getDate())
                .clickCount(a.getClickCount())
                .participantCount(totalParticipants)
                .avgSession(AnalyticsResDto.AvgSessionDto.builder()
                        .minutes(minutes)
                        .seconds(seconds)
                        .build())
                .participationRate(totalParticipants > 0
                        ? ((double) a.getClickCount() / totalParticipants) * 100
                        : 0.0)
                .build();
    }
}
