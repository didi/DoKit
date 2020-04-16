package com.didichuxing.doraemonkit.widget.easyrefresh.exception;

/**
 * Created by guanaj on 16/9/21.
 */

public class ERVHRuntimeException extends RuntimeException {

    public ERVHRuntimeException() {
        super();
    }

    public ERVHRuntimeException(Throwable throwable) {
        super(throwable);
    }

    public ERVHRuntimeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ERVHRuntimeException(String detailMessage) {
        super(detailMessage);
    }
}
