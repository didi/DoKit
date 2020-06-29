package com.didichuxing.doraemonkit.widget.easyrefresh.exception

/**
 * Created by guanaj on 16/9/21.
 */
class ERVHRuntimeException : RuntimeException {
    constructor() : super() {}
    constructor(throwable: Throwable?) : super(throwable) {}
    constructor(detailMessage: String?, throwable: Throwable?) : super(detailMessage, throwable) {}
    constructor(detailMessage: String?) : super(detailMessage) {}
}