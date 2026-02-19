package backend.onmoim.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class CommentRequestDTO {
    @Builder
    public record CreateCommentDTO(
            @NotBlank(message = "댓글 내용은 비어있을 수 없습니다.")
            @Size(max = 500, message = "댓글은 500자를 초과할 수 없습니다.")
            String content
    ) {}
}
