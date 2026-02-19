package backend.onmoim.global.schedular;

import backend.onmoim.domain.analytics.repository.AnalyticsRespository;
import backend.onmoim.domain.analytics.service.AnalyticsCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyticsScheduler {
    private final AnalyticsCommandService analyticsCommandService;

    @Scheduled(cron = "0 0 0 * * ?",zone="Asia/Seoul")
    public void generateDailyAnalytics(){
        analyticsCommandService.createDailyAnalyticsForAllEvents();
    }


    @Scheduled(cron = "0 59 23 * * ?",zone="Asia/Seoul")
    public void storeParticipantNum() {analyticsCommandService.countTodayFinalParticipantNum();}
}
