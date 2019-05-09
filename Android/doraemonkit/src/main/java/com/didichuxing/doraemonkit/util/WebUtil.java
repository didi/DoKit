package com.didichuxing.doraemonkit.util;

import android.content.Context;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by wanglikun on 2019/4/15
 */
public class WebUtil {
    public static void webViewLoadLocalHtml(WebView view, String jsPath) {
        String htmlData = assetFileToString(view.getContext(), jsPath);
        view.loadDataWithBaseURL("http://localhost", htmlData, "text/html", "UTF-8", null);
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