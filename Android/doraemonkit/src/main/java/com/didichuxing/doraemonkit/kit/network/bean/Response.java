package com.didichuxing.doraemonkit.kit.network.bean;

import java.io.Serializable;

/**
 * 响应bean,不包含内容Body
 */
public class Response  implements Serializable{

    public String url;

    public int status;

    public String headers;

    public String mimeType;

    @Override
    public String toString() {
        return String.format("[%s %d  %s %s]", url, status, headers.toString(), mimeType);
    }
}