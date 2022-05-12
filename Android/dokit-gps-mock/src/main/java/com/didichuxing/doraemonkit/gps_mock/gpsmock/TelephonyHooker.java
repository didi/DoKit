package com.didichuxing.doraemonkit.gps_mock.gpsmock;

import android.content.Context;
import android.os.IBinder;
import android.telephony.CellInfo;
import android.telephony.gsm.GsmCellLocation;

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
    public String serviceName() {
        return Context.TELEPHONY_SERVICE;
    }

    @Override
    public String stubName() {
        return "com.android.internal.telephony.ITelephony$Stub";
    }

    @Override
    public Map<String, MethodHandler> registerMethodHandlers() {
        Map<String, MethodHandler> methodHandlers = new HashMap<>();
        methodHandlers.put("getAllCellInfo", new GetAllCellInfoMethodHandler());
        methodHandlers.put("getCellLocation", new GetCellLocationMethodHandler());
        methodHandlers.put("listen", new ListenMethodHandler());
        return methodHandlers;
    }

    @Override
    public void replaceBinderProxy(Context context, IBinder proxy) {

    }

    static class GetAllCellInfoMethodHandler extends MethodHandler {

        @Override
        public Object onInvoke(Object originObject, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
            if (!GpsMockManager.getInstance().isMocking()) {
                return method.invoke(originObject, args);
            }
            return new ArrayList<CellInfo>();
        }
    }

    static class GetCellLocationMethodHandler extends MethodHandler {

        @Override
        public Object onInvoke(Object originObject, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
            if (!GpsMockManager.getInstance().isMocking()) {
                return method.invoke(originObject, args);
            }

            return new GsmCellLocation();
        }
    }

    static class ListenMethodHandler extends MethodHandler {
        @Override
        public Object onInvoke(Object originObject, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
            if (!GpsMockManager.getInstance().isMocking()) {
                return method.invoke(originObject, args);
            }
            return null;
        }
    }
}
