package com.didichuxing.doraemonkit.kit.test.report;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewParent;

import com.didichuxing.doraemonkit.kit.test.utils.WindowPathUtil;
import com.didichuxing.doraemonkit.kit.test.utils.XposedHookUtil;
import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.util.RandomUtils;
import com.didichuxing.doraemonkit.util.ReflectUtils;
import com.didichuxing.doraemonkit.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

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


    /**
     * 创建各个可见Window的图像
     * 备注按需要调整图层顺序
     */
    public List<MyWindowBitmap> createMyWindowBitmap(List<ViewParent> parents) {
        List<MyWindowBitmap> pageBitmaps = new ArrayList<>();
        List<MyWindowBitmap> otherBitmaps = new ArrayList<>();
        List<MyWindowBitmap> doKitBitmaps = new ArrayList<>();

        for (ViewParent parent : parents) {
            View view = ReflectUtils.reflect(parent).field("mView").get();
            Bitmap bitmap = getViewBitmap(parent);
            Rect winFrame = ReflectUtils.reflect(parent).field("mWinFrame").get();
            boolean appVisible = ReflectUtils.reflect(parent).field("mAppVisible").get();
            boolean page = WindowPathUtil.isPageViewRoot(parent);
            boolean doKit = WindowPathUtil.isDoKitViewRoot(parent);

            MyWindowBitmap windowBitmap = new MyWindowBitmap(parent, view, bitmap, winFrame, appVisible, page, doKit);

            if (doKit) {
                doKitBitmaps.add(windowBitmap);
                continue;
            }
            if (page) {
                pageBitmaps.add(windowBitmap);
                continue;
            }
            otherBitmaps.add(windowBitmap);

        }
        pageBitmaps.addAll(otherBitmaps);
        pageBitmaps.addAll(doKitBitmaps);
        return pageBitmaps;
    }

    /**
     * 使用图像合成方式生成截图
     * 备注：无需权限，但是仅可以获取到应用内的图像
     */
    public Bitmap screenshotBitmap() {
        List<ViewParent> parents = XposedHookUtil.INSTANCE.getROOT_VIEWS();
        List<ViewParent> showParents = WindowPathUtil.filterShowViewRoot(parents);
        List<MyWindowBitmap> myWindowBitmaps = createMyWindowBitmap(showParents);

        Paint paint = new Paint();
        if (myWindowBitmaps.size() <= 1) {
            return screenshotBitmap(ActivityUtils.getTopActivity());
        }
        Bitmap canvasBitmap = myWindowBitmaps.get(0).getBitmap();
        Canvas canvas = new Canvas(canvasBitmap);
        int size = myWindowBitmaps.size();
        for (int i = 1; i < size; i++) {
            MyWindowBitmap bitmap = myWindowBitmaps.get(i);
            Bitmap map = bitmap.getBitmap();
            if (map != null){
                Rect out = bitmap.getWinFrame();
                if (!bitmap.getDoKitView() && bitmap.getDecorView()) {
                    canvas.drawARGB(90, 0, 0, 0);
                }
                canvas.drawBitmap(map, out.left, out.top, paint);
            }
        }
        return canvasBitmap;
    }

    private Bitmap getViewBitmap(ViewParent viewParent) {
        View view = ReflectUtils.reflect(viewParent).field("mView").get();
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
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
            boolean ok = bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
            if (ok) {
                return fullFilepath;
            } else {
                file2.delete();
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fullFilepath;
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
