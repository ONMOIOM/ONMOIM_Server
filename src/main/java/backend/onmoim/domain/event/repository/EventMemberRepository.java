package backend.onmoim.domain.event.repository;

import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.event.entity.EventMember;
import backend.onmoim.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventMemberRepository extends JpaRepository<EventMember, Long> {

    Optional<EventMember> findByUserAndEvent(User user, Event event);

    List<EventMember> findAllByEvent(Event event);

    void deleteAllByEvent(Event event);

    @Query("SELECT COUNT(em) FROM EventMember em WHERE em.event.id = :eventId")
    int countAttendedByEventId(@Param("eventId") Long eventId);

    @Query("SELECT em.event FROM EventMember em WHERE em.user.id = :userId AND em.status = 'ATTEND'")
    List<Event> findEventByUserId(@Param("userId") Long userId);
}