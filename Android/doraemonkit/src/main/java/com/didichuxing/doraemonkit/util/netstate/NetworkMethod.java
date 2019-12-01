package com.didichuxing.doraemonkit.util.netstate;

import java.lang.reflect.Method;

/**
 * @author maple on 2019/7/9 17:00.
 * @version v1.0
 * @see 1040441325@qq.com
 */
class NetworkMethod {
    private Method method;
    private Class<?> param;//参数class
    private NetType type;

    public NetworkMethod(Method method, Class<?> param, NetType type) {
        this.method = method;
        this.param = param;
        this.type = type;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getParam() {
        return param;
    }

    public void setParam(Class<?> param) {
        this.param = param;
    }

    public NetType getType() {
        return type;
    }

    public void setType(NetType type) {
        this.type = type;
    }
}
