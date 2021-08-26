package com.didichuxing.doraemonkit.rpc;

import android.view.View;

import com.didi.hummer.context.HummerContext;
import com.didi.hummer.pool.ComponentPool;
import com.didi.hummer.render.component.view.HMBase;
import com.didichuxing.doraemonkit.DoKit;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by XiaoFeng on 2021/8/12.
 */
public class DoKitMCHummerHelper {
    public static void registerHummerMCEventListener(HummerContext context) {
        context.registerJSFunction("sendMCCustomEvent", params -> {
            long objId = ((Number) params[0]).longValue();
            HMBase hmBase = context.getObjectPool().get(objId);
            String eventType = (String) params[1];
            Map<String, String> ps = (Map) params[2];
            DoKit.sendCustomEvent(eventType, hmBase.getView(), ps);
            return null;
        });
    }

    public static void processHummerMCClientEvent(HummerContext context, View view, String eventType, Map<String, String> params) {
        HMBase hmbase = searchHMView(context, view);
        if (hmbase != null) {
            context.getJsContext().callFunction("processMCClientEvent", hmbase.getJSValue(), eventType, params);
        }
    }

    private static HMBase searchHMView(HummerContext context, View view) {
        HMBase hmbase = null;
        try {
            ComponentPool pool = (ComponentPool) context.getObjectPool();
            Class<? extends ComponentPool> clazz = pool.getClass();
            Field mInstanceField = clazz.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Map<Long, Object> instance = (Map) mInstanceField.get(pool);
            for (Long objId : instance.keySet()) {
                HMBase b = (HMBase) instance.get(objId);
                if (b.getView().equals(view)) {
                    hmbase = b;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hmbase;
    }
}
