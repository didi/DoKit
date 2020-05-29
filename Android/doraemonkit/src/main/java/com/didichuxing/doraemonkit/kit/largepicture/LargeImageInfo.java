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
    private String framework = "other";
    private String url;
    private double fileSize;
    private double memorySize;
    private int width;
    private int height;

    public void setUrl(String url) {
        this.url = url;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public void setMemorySize(double memorySize) {
        this.memorySize = memorySize;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public double getMemorySize() {
        return memorySize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    @Override
    public String toString() {
        return "LargeImageInfo{" +
                "framework='" + framework + '\'' +
                ", url='" + url + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", memorySize='" + memorySize + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
