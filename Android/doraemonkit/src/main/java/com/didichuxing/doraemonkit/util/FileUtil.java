package com.didichuxing.doraemonkit.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.format.Formatter;

import java.io.File;
import java.util.Locale;

/**
 * Created by wanglikun on 2018/10/17.
 */

public class FileUtil {
    private static final String DATA_TYPE_ALL = "*/*";
    private static final String DATA_TYPE_APK = "application/vnd.android.package-archive";
    private static final String DATA_TYPE_VIDEO = "video/*";
    private static final String DATA_TYPE_AUDIO = "audio/*";
    private static final String DATA_TYPE_HTML = "text/html";
    private static final String DATA_TYPE_IMAGE = "image/*";
    private static final String DATA_TYPE_PPT = "application/vnd.ms-powerpoint";
    private static final String DATA_TYPE_EXCEL = "application/vnd.ms-excel";
    private static final String DATA_TYPE_WORD = "application/msword";
    private static final String DATA_TYPE_CHM = "application/x-chm";
    private static final String DATA_TYPE_TXT = "text/plain";
    private static final String DATA_TYPE_PDF = "application/pdf";
    private static final String DATA_TYPE_RTF = "application/rtf";

    public static final String TXT = "txt";
    public static final String JPG = "jpg";

    private FileUtil() {
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
        if (file == null || !file.exists()){
            return;
        }
        String suffix = getSuffix(file);
        String type;
        switch (suffix) {
            case "doc":
            case "docx":
                type = DATA_TYPE_WORD;
                break;
            case "pdf":
                type = DATA_TYPE_PDF;
                break;
            case "ppt":
            case "pptx":
                type = DATA_TYPE_PPT;
                break;
            case "xls":
            case "xlsx":
                type = DATA_TYPE_EXCEL;
                break;
            case "rtf":
                type = DATA_TYPE_RTF;
                break;
            case "mpg":
            case "mpeg":
            case "3gp":
            case "mp4":
                type = DATA_TYPE_VIDEO;
                break;
            case "m4a":
            case "mp3":
            case "mid":
            case "xmf":
            case "ogg":
            case "wav":
                type = DATA_TYPE_AUDIO;
                break;
            case "gif":
            case "jpg":
            case "jpeg":
            case "png":
            case "bmp":
                type = DATA_TYPE_IMAGE;
                break;
            case "txt":
            case "sh":
                type = DATA_TYPE_TXT;
                break;
            case "html":
                type = DATA_TYPE_HTML;
                break;
            case "xml":
            case "rss":
                type = "application/rss+xml";
                break;
            case "js":
                type = "application/javascript";
                break;
            case "chm":
                type = DATA_TYPE_CHM;
                break;
            case "json":
                type = "application/json";
                break;
            case "class":
                type = "application/java-vm";
                break;
            case "jar":
                type = "application/java-archive";
                break;
            case "gtar":
                type = "application/x-gtar";
                break;
            case "tar":
                type = "application/x-tar";
                break;
            case "css":
                type = "text/css";
                break;
            case "7z":
                type = "application/x-7z-compressed";
                break;
            case "swf":
                type = "application/x-7z-compressed";
                break;
            case "zip":
                type = "application/zip";
                break;
            case "csv":
                type = "text/csv";
                break;
            case "apk":
                type = DATA_TYPE_APK;
                break;
            default:
                type = DATA_TYPE_ALL;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        uri = FileProvider.getUriForFile(context, context.getPackageName() + ".debugfileprovider", file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, type);
        if (intent.resolveActivity(context.getPackageManager()) == null) {
            intent.setDataAndType(uri, DATA_TYPE_ALL);
        }
        context.startActivity(intent);
    }

    public static boolean isImage(File file) {
        if (file == null) {
            return false;
        }
        String suffix = getSuffix(file);
        return "jpg".equals(suffix) || "jpeg".equals(suffix) || "png".equals(suffix) || "bmp".equals(suffix);
    }
}
