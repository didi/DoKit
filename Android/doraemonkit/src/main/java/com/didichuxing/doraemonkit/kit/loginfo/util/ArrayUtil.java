package com.didichuxing.doraemonkit.kit.loginfo.util;

import java.lang.reflect.Array;
import java.util.List;

public class ArrayUtil {

    public static <T> int indexOf(T[] array, T object) {
        for (int i = 0; i < array.length; i++) {
            if (object.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }


    public static <T> T[] toArray(List<T> list, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(clazz, list.size());
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

}
