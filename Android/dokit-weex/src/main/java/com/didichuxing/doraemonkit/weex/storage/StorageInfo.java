package com.didichuxing.doraemonkit.weex.storage;

import java.io.Serializable;

/**
 * @author haojianglong
 * @date 2019-06-18
 */
public class StorageInfo implements Serializable {

    public String key;
    public String value;
    public String timestamp;

    public StorageInfo() {
    }

    public StorageInfo(String key, String value) {
        this.key = key;
        this.value = value;
    }

}
