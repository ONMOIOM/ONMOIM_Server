package backend.onmoim.domain.comment.service.command;

import backend.onmoim.domain.comment.dto.request.CommentRequestDTO;
import backend.onmoim.domain.comment.entity.Comment;
import backend.onmoim.domain.user.entity.User;

public interface CommentCommandService {
    Comment createComment(Long eventId, User user, CommentRequestDTO.CreateCommentDTO request);

    void deleteComment(Long commentId, User user);
}
