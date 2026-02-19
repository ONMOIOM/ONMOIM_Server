package backend.onmoim.domain.comment.service.query;

import backend.onmoim.domain.comment.dto.response.CommentResponseDTO;

public interface CommentQueryService {

    CommentResponseDTO.CommentCursorListDTO getCommentList(Long eventId, Long lastCommentId);
}
