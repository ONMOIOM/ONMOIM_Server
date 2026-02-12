package backend.onmoim.domain.comment.converter;

import backend.onmoim.domain.comment.dto.response.CommentResponseDTO;
import backend.onmoim.domain.comment.entity.Comment;
import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.utils.MinioUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentConverter {
    
    private final MinioUtil minioUtil;

    public static Comment toComment(String content, User user, Event event) {
        return Comment.builder()
                .content(content)
                .user(user)
                .event(event)
                .build();
    }

    public CommentResponseDTO.CommentResultDTO toCommentResultDTO(Comment comment) {
        User user = comment.getUser();
        
        // MinIO에서 프로필 이미지 URL 동적 생성
        String profileUrl = minioUtil.getProfileImageUrl(user.getId());

        return new CommentResponseDTO.CommentResultDTO(
                comment.getId(),
                user.getNickname(),
                profileUrl,
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

    public CommentResponseDTO.CommentCursorListDTO toCommentCursorListDTO(Long eventId, Slice<Comment> commentSlice, Long nextCursor) {
        List<CommentResponseDTO.CommentResultDTO> commentResultDTOList = commentSlice.getContent().stream()
                .map(this::toCommentResultDTO)
                .collect(Collectors.toList());

        return new CommentResponseDTO.CommentCursorListDTO(
                eventId,
                commentResultDTOList,
                nextCursor,
                commentSlice.hasNext()
        );
    }

}
