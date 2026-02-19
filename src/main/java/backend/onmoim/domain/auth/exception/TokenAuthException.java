package backend.onmoim.domain.auth.exception;

import backend.onmoim.global.common.exception.GeneralException;

public class TokenAuthException extends GeneralException {
    public TokenAuthException(TokenAuthErrorCode errorCode) {
        super(errorCode);
    }
}
