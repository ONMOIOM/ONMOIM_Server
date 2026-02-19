package backend.onmoim.domain.comment.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponseDTO {
    public record CommentResultDTO(
            Long commentId,
            String nickname,      // 닉네임
            String profileImageUrl, // 프로필 이미지 URL
            String content,       // 내용
            LocalDateTime createdAt // 작성일자
    ) {}

    public record CommentCursorListDTO(
            Long eventId,
            List<CommentResultDTO> commentList,
            Long nextCursor,
            Boolean hasNext
    ) {}

}
