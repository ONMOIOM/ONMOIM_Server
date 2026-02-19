package backend.onmoim.domain.comment.service.command;

import backend.onmoim.domain.comment.converter.CommentConverter;
import backend.onmoim.domain.comment.dto.request.CommentRequestDTO;
import backend.onmoim.domain.comment.entity.Comment;
import backend.onmoim.domain.comment.exception.CommentErrorCode;
import backend.onmoim.domain.comment.exception.CommentException;
import backend.onmoim.domain.comment.repository.CommentRepository;
import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.event.repository.EventRepository;
import backend.onmoim.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentCommandServiceImpl implements CommentCommandService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    @Override
    public Comment createComment(Long eventId, User user, CommentRequestDTO.CreateCommentDTO request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.EVENT_NOT_FOUND)); // 이벤트 존재 확인

        Comment comment = CommentConverter.toComment(request.content(), user, event);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));

        // 작성자 본인 확인
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new CommentException(CommentErrorCode.NOT_COMMENT_OWNER);
        }
        commentRepository.delete(comment);
    }
}
