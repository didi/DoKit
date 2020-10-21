package com.didichuxing.doraemondemo;

import java.util.HashMap;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/22-11:38
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class AopTest {
    private static final String TAG = "AopTest";
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void test() {
        HashMap<String, String> map = new HashMap<>();
        map.put("a", "a");
        map.put("b", "b");
        map.put("c", "c");

    }


}
