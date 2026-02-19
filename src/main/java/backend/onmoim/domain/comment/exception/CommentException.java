package backend.onmoim.domain.comment.exception;

import backend.onmoim.global.common.exception.GeneralException;

public class CommentException extends GeneralException {
    public CommentException(CommentErrorCode errorCode) {
        super(errorCode);
    }
}
