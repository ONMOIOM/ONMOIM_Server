package backend.onmoim.domain.comment.service.query;

import backend.onmoim.domain.comment.converter.CommentConverter;
import backend.onmoim.domain.comment.dto.response.CommentResponseDTO;
import backend.onmoim.domain.comment.entity.Comment;
import backend.onmoim.domain.comment.repository.CommentRepository;
import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.event.repository.EventRepository;
import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentQueryServiceImpl implements CommentQueryService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final CommentConverter commentConverter;

    @Override
    public CommentResponseDTO.CommentCursorListDTO getCommentList(Long eventId, Long lastCommentId) {
        // 한 페이지당 10개씩 조회
        Pageable pageable = PageRequest.of(0, 10);

        Slice<Comment> commentSlice;
        if (lastCommentId == null) {
            // 처음 조회할 때 해당 행사의 댓글만 가져옴
            commentSlice = commentRepository.findFirstByEventId(eventId, pageable);
        } else {
            // 커서 기반으로 해당 행사의 다음 댓글들을 가져옴
            commentSlice = commentRepository.findByEventIdAndIdLessThan(eventId, lastCommentId, pageable);
        }

        // 마지막 요소의 ID를 다음 커서로 설정
        Long nextCursor = commentSlice.hasNext() ? commentSlice.getContent().get(commentSlice.getContent().size() - 1).getId() : null;

        // Converter를 통해 DTO로 변환 시 eventId를 함께 전달
        return commentConverter.toCommentCursorListDTO(eventId, commentSlice, nextCursor);
    }
}
