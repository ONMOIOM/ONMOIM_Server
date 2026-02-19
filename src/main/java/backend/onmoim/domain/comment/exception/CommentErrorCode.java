package backend.onmoim.domain.comment.exception;

import backend.onmoim.global.common.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements BaseErrorCode {

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_4001", "존재하지 않는 댓글입니다."),
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_4002", "존재하지 않는 행사입니다."),
    NOT_COMMENT_OWNER(HttpStatus.FORBIDDEN, "COMMENT_4003", "댓글을 삭제할 권한이 없습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
