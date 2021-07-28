package com.didichuxing.doraemonkit.util;

import com.didichuxing.doraemonkit.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by wanglikun on 2018/11/17.
 */
public class DoKitCacheUtils {
    private static final String TAG = "CacheUtils";


    public static boolean saveObject(String key, Serializable ser) {
        File file = new File(Utils.getApp().getCacheDir() + "/" + key);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return saveObject(ser, file);
    }

    public static Serializable readObject(String key) {
        File file = new File(Utils.getApp().getCacheDir() + "/" + key);
        return readObject(file);
    }

    public static boolean saveObject(Serializable ser, File file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Serializable readObject(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (IOException e) {
            if (e instanceof InvalidClassException) {
                file.delete();
            }
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LogHelper.e(TAG, e.toString());
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    LogHelper.e(TAG, e.toString());
                }
            }
        }
    }
}