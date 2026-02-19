package backend.onmoim.domain.user.exception;

import backend.onmoim.global.common.exception.GeneralException;

public class UserException extends GeneralException {
    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }
}
