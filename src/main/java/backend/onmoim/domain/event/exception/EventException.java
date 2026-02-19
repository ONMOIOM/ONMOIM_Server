package backend.onmoim.domain.event.exception;

import backend.onmoim.global.common.code.BaseErrorCode;
import backend.onmoim.global.common.exception.GeneralException;

public class EventException extends GeneralException {
    public EventException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}