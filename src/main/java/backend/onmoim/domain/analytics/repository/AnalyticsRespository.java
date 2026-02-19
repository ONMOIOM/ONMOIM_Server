package backend.onmoim.domain.analytics.repository;

import backend.onmoim.domain.analytics.entity.Analytics;
import backend.onmoim.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsRespository extends JpaRepository<Analytics,Long> {
    @Query("SELECT a FROM Analytics a WHERE a.event.id = :eventId AND a.date = :date")
    Optional<Analytics> findByEventIdAndDate(@Param("eventId") Long eventId,
                                             @Param("date") LocalDate date);

    @Modifying
    @Query("UPDATE Analytics a SET a.clickCount = a.clickCount + 1 WHERE a.event.id = :eventId AND a.date = :date")
    int incrementClickCount(@Param("eventId") Long eventId, @Param("date") LocalDate date);

    @Modifying
    @Query("UPDATE Analytics a " +
            "SET a.avgSessionTimeSec = (a.avgSessionTimeSec * (a.clickCount - 1) + :sessionTime) / a.clickCount " +
            "WHERE a.event.id = :eventId AND a.date = :date")
    int updateAverageDuration(@Param("eventId") Long eventId,
                              @Param("date") LocalDate date,
                              @Param("sessionTime") long sessionTime);

    boolean existsByEventAndDate(Event event, LocalDate date);

    @Query("SELECT a FROM Analytics a WHERE a.event.id = :eventId AND :startDate <= a.date AND a.date<= :endDate ORDER BY a.date ASC")
    List<Analytics> countWeeklyAnalytics(@Param("eventId") Long eventId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    List<Analytics> findAllByDate(LocalDate date);
}
