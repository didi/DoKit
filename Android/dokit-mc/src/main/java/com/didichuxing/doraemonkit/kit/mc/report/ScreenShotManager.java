package com.didichuxing.doraemonkit.kit.mc.report;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;


import com.didichuxing.doraemonkit.util.RandomUtils;


import java.io.File;
import java.io.FileOutputStream;

/**
 * didi Create on 2022/4/1 .
 * <p>
 * Copyright (c) 2022/4/1 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/1 4:25 下午
 * @Description 截屏管理器
 */

public class ScreenShotManager {


    private String screenFileDir;


    public ScreenShotManager(String screenFileDir) {
        this.screenFileDir = screenFileDir;
    }

    public String screenshot(Activity activity) {
        String fileName = createNextFileName();
        String fullFilepath = screenFileDir + "/" + fileName + ".jpeg";
        try {
            View decorView = activity.getWindow().getDecorView();
            decorView.setDrawingCacheEnabled(true);
            Bitmap bitmap = decorView.getDrawingCache();
            //保存图片
            FileOutputStream outputStream = new FileOutputStream(fullFilepath);
            boolean ok = bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            if (ok) {
                return fileName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String createNextFileName() {
        String name = RandomUtils.random64HexString();
        String dir = screenFileDir;
        String fullFilepath = dir + "/" + name + ".jpeg";
        File file = new File(fullFilepath);
        int index = 0;
        while (true) {
            if (file.exists()) {
                index++;
                name = name + "-" + index;
                file = new File(dir + "/" + name + ".jpeg");
                continue;
            }
            break;
        }
        return name;
    }


}
