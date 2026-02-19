package backend.onmoim.domain.auth.exception;

import backend.onmoim.global.common.exception.GeneralException;

public class EmailAuthException extends GeneralException {
    public EmailAuthException(EmailAuthErrorCode errorCode) {
        super(errorCode);
    }
}
