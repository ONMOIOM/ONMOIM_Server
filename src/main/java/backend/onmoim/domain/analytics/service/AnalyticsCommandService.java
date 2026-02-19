package backend.onmoim.domain.analytics.service;

import backend.onmoim.domain.analytics.code.AnalyticsErrorCode;
import backend.onmoim.domain.analytics.converter.AnalyticsConverter;
import backend.onmoim.domain.analytics.dto.res.AnalyticsResDto;
import backend.onmoim.domain.analytics.entity.Analytics;
import backend.onmoim.domain.analytics.repository.AnalyticsRespository;
import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.event.repository.EventRepository;
import backend.onmoim.domain.event.repository.EventMemberRepository;
import backend.onmoim.global.common.exception.GeneralException;
import backend.onmoim.global.common.session.RedisSessionTracker;


import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import static backend.onmoim.domain.analytics.code.AnalyticsErrorCode.BAD_EVENT_ID;
import static backend.onmoim.domain.analytics.code.AnalyticsErrorCode.NOT_HOST;

@Transactional
@Service
@RequiredArgsConstructor
public class AnalyticsCommandService {

    private final RedisSessionTracker redisSessionTracker;
    private final AnalyticsRespository analyticsRepository;
    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;

    public String sessionEnter(Long userId,Long eventId){
        String sessionId=redisSessionTracker.enter(userId,eventId);
        return sessionId;
    }


    public void exitCount(Long eventId){
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        int updated = analyticsRepository.incrementClickCount(eventId, today);
        if (updated == 0) {

            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new GeneralException(BAD_EVENT_ID));

            if (!analyticsRepository.existsByEventAndDate(event, today)) {
                analyticsRepository.save(
                        Analytics.builder()
                                .event(event)
                                .date(today)
                                .clickCount(1)
                                .avgSessionTimeSec(0)
                                .build()
                );
            } else {
                analyticsRepository.incrementClickCount(eventId, today);
            }
        }
    }

    public void sessionExit(String sessionId,Long eventId){

        RedisSessionTracker.SessionData data=redisSessionTracker.exit(sessionId);
        if(data==null){
            throw new GeneralException(AnalyticsErrorCode.REDIS_NOT_FOUND);
        }
        if (!data.getEventId().equals(eventId)) {
            throw new GeneralException(BAD_EVENT_ID);
        }
        LocalDateTime enterTime = data.getEnterTime();
        LocalDateTime exitTime= LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        Duration duration = Duration.between(enterTime,exitTime);
        long seconds = duration.getSeconds();

        analyticsRepository.updateAverageDuration(data.getEventId(),LocalDate.now(ZoneId.of("Asia/Seoul")),seconds);
    }

    public void createDailyAnalyticsForAllEvents() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        List<Event> events = eventRepository.findAll();

        for(Event event : events){
            Analytics analytics = Analytics.builder().
                                    event(event).
                                    date(today).
                                    clickCount(0).
                                    avgSessionTimeSec(0).
                                    build();

            try {
                analyticsRepository.save(analytics);
            } catch (DataIntegrityViolationException e) {
                // 이미 존재시 아무것도 안함
            }
        }
    }

    public void createTodayAnalyticsTable(Event event){

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        if(event==null){
            throw new GeneralException(BAD_EVENT_ID);
        }

        Analytics analytics = Analytics.builder().
                event(event).
                date(today).
                clickCount(0).
                avgSessionTimeSec(0).
                build();
        analyticsRepository.save(analytics);
    }

    public void countTodayFinalParticipantNum(){

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        List<Analytics> analyticsList = analyticsRepository.findAllByDate(today);

        for (Analytics analytics : analyticsList) {
            Long eventId = analytics.getEvent().getId();

            int participantCount = eventMemberRepository.countAttendedByEventId(eventId);

            analytics.setParticipantNum(participantCount);
            analyticsRepository.save(analytics);
        }
    }


    public AnalyticsResDto.GetAnalyticsListDto analyticsGet(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(BAD_EVENT_ID));

        if (!event.getHost().getId().equals(userId)) {
            throw new GeneralException(NOT_HOST);
        }

        LocalDate endDate = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate startDate = endDate.minusDays(6);

        List<Analytics> analyticsList = analyticsRepository.countWeeklyAnalytics(eventId, startDate, endDate);
        int realTimeParticipants = eventMemberRepository.countAttendedByEventId(eventId);

        List<AnalyticsResDto.DailyAnalyticsDto> stats = analyticsList.stream()
                .map(a -> {
                    int pNum = a.getDate().equals(endDate) ? realTimeParticipants : a.getParticipantNum();
                    return AnalyticsConverter.toDailyDto(a, pNum);
                })
                .collect(Collectors.toList());

        return new AnalyticsResDto.GetAnalyticsListDto(eventId, stats);
    }
}
