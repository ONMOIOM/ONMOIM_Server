package backend.onmoim.domain.comment.repository;

import backend.onmoim.domain.comment.entity.Comment;
import backend.onmoim.domain.event.entity.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.event.id = :eventId ORDER BY c.id DESC")
    Slice<Comment> findFirstByEventId(@Param("eventId") Long eventId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.event.id = :eventId AND c.id < :lastId ORDER BY c.id DESC")
    Slice<Comment> findByEventIdAndIdLessThan(@Param("eventId") Long eventId, @Param("lastId") Long lastId, Pageable pageable);// 두번째 조회 lastCommentId이후
}
