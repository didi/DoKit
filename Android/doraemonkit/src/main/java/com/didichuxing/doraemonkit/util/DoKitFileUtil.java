package com.didichuxing.doraemonkit.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;
import android.text.format.Formatter;

import java.io.File;
import java.util.Locale;

/**
 * Created by wanglikun on 2018/10/17.
 */

public class DoKitFileUtil {
    private static final String TAG = "FileUtil";

    public static final String TXT = "txt";
    public static final String JPG = "jpg";
    public static final String DB = "db";
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String XML = ".xml";

    private DoKitFileUtil() {
    }

    public static String getFileSize(Context context, File file) {
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        return Formatter.formatFileSize(context, file.length());
    }

    public static String getSuffix(File file) {
        if (file == null || !file.exists()) {
            return "";
        }
        return file.getName()
                .substring(file.getName().lastIndexOf(".") + 1)
                .toLowerCase(Locale.getDefault());
    }

    public static void systemShare(Context context, File file) {
        if (file == null || !file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        try {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".debugfileprovider", file);
            String type = context.getContentResolver().getType(uri);
            intent.setDataAndType(uri, type);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            if (intent.resolveActivity(context.getPackageManager()) == null) {
                intent.setDataAndType(uri, "*/*");
            }
            context.startActivity(intent);
        } catch (Exception e) {
            LogHelper.e(TAG,
                    "The selected file can't be shared: " + file.toString());
        }
    }

    public static boolean isImage(File file) {
        if (file == null) {
            return false;
        }
        String suffix = getSuffix(file);
        return "jpg".equals(suffix) || "jpeg".equals(suffix) || "png".equals(suffix) || "bmp".equals(suffix);
    }

    public static boolean isVideo(File file) {
        if (file == null) {
            return false;
        }
        String suffix = getSuffix(file);
        return "3gp".equals(suffix) || "mp4".equals(suffix) || "mkv".equals(suffix) || "webm".equals(suffix);
    }

    public static boolean isDB(File file) {
        if (file == null) {
            return false;
        }
        String suffix = getSuffix(file);
        return "db".equals(suffix);
    }

    public static boolean isSp(File file) {
        File parentFile = file.getParentFile();
        if (parentFile != null && parentFile.getName().equals(SHARED_PREFS) && file.getName().contains(XML)) {
            return true;
        }
        return false;
    }

    /**
     * @param file
     */
    public static void deleteDirectory(File file) {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (File f : listFiles) {
                deleteDirectory(f);
            }
            file.delete();
        } else {
            file.delete();
        }
    }

    public static long getDirectorySize(File directory) {
        long size = 0;
        File[] listFiles = directory.listFiles();
        if (listFiles == null) {
            return size;
        }
        for (File file : listFiles) {
            if (file.isDirectory()) {
                size += getDirectorySize(file);
            } else {
                size += file.length();
            }
        }
        return size;
    }
}
