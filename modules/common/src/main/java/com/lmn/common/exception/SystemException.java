package com.lmn.common.exception;

public class SystemException extends BaseException {

    public SystemException(String message) {
        super(message, new Throwable(message));
    }

    public SystemException(Throwable cause) {
        super(cause);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

}
