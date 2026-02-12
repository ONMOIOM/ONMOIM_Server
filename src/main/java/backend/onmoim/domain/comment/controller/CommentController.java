package backend.onmoim.domain.comment.controller;

import backend.onmoim.domain.comment.converter.CommentConverter;
import backend.onmoim.domain.comment.dto.request.CommentRequestDTO;
import backend.onmoim.domain.comment.dto.response.CommentResponseDTO;
import backend.onmoim.domain.comment.entity.Comment;
import backend.onmoim.domain.comment.service.command.CommentCommandService;
import backend.onmoim.domain.comment.service.query.CommentQueryService;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.common.ApiResponse;
import backend.onmoim.global.common.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment API", description = "댓글 관련 API입니다.")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController { // PascalCase 클래스 네이밍 준수

    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;
    private final CommentConverter commentConverter;

    @Operation(summary = "댓글 작성 API", description = "특정 이벤트에 댓글을 작성합니다.")
    @PostMapping("/events/{eventId}/comments")
    public ApiResponse<CommentResponseDTO.CommentResultDTO> createComment(
            @PathVariable(name = "eventId") Long eventId,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CommentRequestDTO.CreateCommentDTO request) {

        Comment comment = commentCommandService.createComment(eventId, user, request);

        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, commentConverter.toCommentResultDTO(comment));
    }

    @Operation(summary = "댓글 목록 조회 API (커서 기반)", description = "특정 행사의 댓글을 최신순으로 조회합니다.")
    @GetMapping("/events/{eventId}/comments") // 경로에서 eventId를 확실히 받음
    public ApiResponse<CommentResponseDTO.CommentCursorListDTO> getCommentList(
            @PathVariable(name = "eventId") Long eventId,
            @RequestParam(name = "lastCommentId", required = false) Long lastCommentId) {

        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, commentQueryService.getCommentList(eventId, lastCommentId));
    }

    @Operation(summary = "댓글 삭제 API")
    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<String> deleteComment(
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticationPrincipal User user) {

        commentCommandService.deleteComment(commentId, user);
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, "댓글이 성공적으로 삭제되었습니다.");
    }
}
