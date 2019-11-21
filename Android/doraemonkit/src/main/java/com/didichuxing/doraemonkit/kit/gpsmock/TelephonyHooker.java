package com.didichuxing.doraemonkit.kit.gpsmock;

import android.content.Context;
import android.os.IBinder;
import android.telephony.CellInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanglikun on 2019/4/2
 */
public class TelephonyHooker extends BaseServiceHooker {
    @Override
    public String getServiceName() {
        return Context.TELEPHONY_SERVICE;
    }

    @Override
    public String getStubName() {
        return "com.android.internal.telephony.ITelephony$Stub";
    }

    @Override
    public Map<String, MethodHandler> getMethodHandlers() {
        Map<String, MethodHandler> methodHandlers = new HashMap<>();
        methodHandlers.put("getAllCellInfo", new GetAllCellInfoMethodHandler());
        methodHandlers.put("getCellLocation", new GetCellLocationMethodHandler());
        methodHandlers.put("listen", new ListenMethodHandler());
        return methodHandlers;
    }

    @Override
    public void replaceBinder(Context context, IBinder proxy) {

    }

    public class GetAllCellInfoMethodHandler implements MethodHandler {

        @Override
        public Object onInvoke(Object originService, Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
            if (!GpsMockManager.getInstance().isMocking()) {
                return method.invoke(originService, args);
            }
            return new ArrayList<CellInfo>();
        }
    }

    public class GetCellLocationMethodHandler implements MethodHandler {

        @Override
        public Object onInvoke(Object originService, Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
            if (!GpsMockManager.getInstance().isMocking()) {
                return method.invoke(originService, args);
            }
            return null;
        }
    }

    private class ListenMethodHandler implements MethodHandler {
        @Override
        public Object onInvoke(Object originService, Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
            if (!GpsMockManager.getInstance().isMocking()) {
                return method.invoke(originService, args);
            }
            return null;
        }
    }
}