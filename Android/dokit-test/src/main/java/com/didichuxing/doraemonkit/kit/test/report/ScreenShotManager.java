package com.didichuxing.doraemonkit.kit.test.report;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

import com.didichuxing.doraemonkit.util.FileIOUtils;
import com.didichuxing.doraemonkit.util.FileManager;
import com.didichuxing.doraemonkit.util.FileUtils;
import com.didichuxing.doraemonkit.util.RandomUtils;
import com.didichuxing.doraemonkit.util.Utils;

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
        String dir = Utils.getApp().getFilesDir().getAbsolutePath();
        this.screenFileDir = dir + "/" + screenFileDir;
    }

    public Bitmap screenshotBitmap(Activity activity) {
        try {
            View decorView = activity.getWindow().getDecorView();
            decorView.setDrawingCacheEnabled(true);
            Bitmap bitmap = decorView.getDrawingCache();
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getScreenFile(String fileName) {
        String fullFilepath = screenFileDir + "/" + fileName + ".jpeg";
        return fullFilepath;
    }

    public String saveBitmap(Bitmap bitmap, String fileName) {
        String fullFilepath = screenFileDir + "/" + fileName + ".jpeg";
        try {
            File file = new File(screenFileDir);
            if (!file.exists()) {
                file.mkdirs();
            }

            File file2 = new File(fullFilepath);
            if (!file2.exists()) {
                file2.createNewFile();
            }
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

    public String screenshot(Activity activity) {
        Bitmap bitmap = screenshotBitmap(activity);
        String fileName = createNextFileName();
        return saveBitmap(bitmap, fileName);
    }

    public String createNextFileName() {
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
