package backend.onmoim.domain.event.repository;

import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByHost(User host);
}
