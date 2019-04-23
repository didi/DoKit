package com.didichuxing.doraemonkit.util;

import java.lang.reflect.Field;

/**
 * @author: linjizong
 * @date: 2019/3/11
 * @desc:
 */
public class ReflectUtils {
    public static Object getField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
