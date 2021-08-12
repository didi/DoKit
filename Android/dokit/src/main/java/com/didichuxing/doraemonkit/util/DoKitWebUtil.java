package com.didichuxing.doraemonkit.util;

import android.content.Context;
import android.webkit.WebView;

import com.didichuxing.doraemonkit.config.GpsMockConfig;
import com.didichuxing.doraemonkit.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by wanglikun on 2019/4/15
 */
public class DoKitWebUtil {
    public static void webViewLoadLocalHtml(final WebView view, String jsPath) {
        String htmlData = assetFileToString(view.getContext(), jsPath);
        view.loadDataWithBaseURL("http://localhost", htmlData, "text/html", "UTF-8", null);
        //必须要延迟一定的时间 方便html字符串先加载完
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                LatLng latLng = GpsMockConfig.getMockLocation();
                if (latLng == null) {
                    latLng = new LatLng(0, 0);
                }
                String url = String.format("javascript:init(%s,%s)", latLng.latitude, latLng.longitude);
                //String url = String.format("javascript:init(%s,%s)", 0, 0);
                //String url = String.format("javascript:init(%s,%s)", 39.901933, 116.396613);
                view.loadUrl(url);
            }
        }, 1000);

    }

    public static String assetFileToString(Context c, String urlStr) {
        InputStream in = null;

        try {
            in = c.getAssets().open(urlStr);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return inputStreamToString(in);
    }

    private static String inputStreamToString(InputStream in) {
        if (in == null) {
            return "";
        } else {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                String line = null;
                StringBuilder sb = new StringBuilder();

                do {
                    line = bufferedReader.readLine();
                    if (line != null) {
                        sb.append(line).append("\n");
                    }
                } while (line != null);

                bufferedReader.close();
                in.close();
                String var4 = sb.toString();
                return var4;
            } catch (Exception var14) {
                var14.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException var13) {
                        var13.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}