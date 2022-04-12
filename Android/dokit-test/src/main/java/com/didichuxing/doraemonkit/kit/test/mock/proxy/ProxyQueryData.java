package com.didichuxing.doraemonkit.kit.test.mock.proxy;

/**
 * didi Create on 2022/3/9 .
 * <p>
 * Copyright (c) 2022/3/9 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/3/9 4:50 下午
 * @Description 用一句话说明文件功能
 */

public class ProxyQueryData {

    private String pid;
    private ProxyRequest proxyRequest;
    private ProxyCallback proxyCallback;

    public ProxyQueryData(String pid, ProxyRequest proxyRequest, ProxyCallback proxyCallback) {
        this.pid = pid;
        this.proxyRequest = proxyRequest;
        this.proxyCallback = proxyCallback;
    }

    public String getPid() {
        return pid;
    }

    public ProxyRequest getProxyRequest() {
        return proxyRequest;
    }

    public ProxyCallback getProxyCallback() {
        return proxyCallback;
    }
}
