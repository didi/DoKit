package com.didichuxing.doraemonkit;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        //System.out.println("ssss");
        JSONObject object = new JSONObject();
        System.out.println("object===>" + object.toString());
//        String newPath = dealPath("/kop_stable/a/b/gateway", 2);
//        System.out.println("newPath===>" + newPath);
    }

    private String dealPath(String oldPath, int fromSDK) {
        if (fromSDK == 1) {
            return oldPath;
        }
        String newPath = oldPath;
        //包含多级路径
        if (oldPath.contains("/kop") && oldPath.split("\\/").length > 1) {
            String[] childPaths = oldPath.split("\\/");
            String firstPath = childPaths[1];
            if (firstPath.contains("kop")) {
                newPath = oldPath.replace("/" + firstPath, "");
            }
        }
        return newPath;
    }
}