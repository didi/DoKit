package com.didichuxing.doraemonkit.kit.largepicture;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-26-17:28
 * 描    述：大图信息
 * 修订历史：
 * ================================================
 */
public class LargeImageInfo {
    private String url;
    private String fileSize;
    private String memorySize;
    private int width;
    private int height;
    private String from;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setMemorySize(String memorySize) {
        this.memorySize = memorySize;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getUrl() {
        return url;
    }

    public String getMemorySize() {
        return memorySize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    @Override
    public String toString() {
        return "LargeImageInfo{" +
                "url='" + url + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", memorySize='" + memorySize + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", from='" + from + '\'' +
                '}';
    }
}
