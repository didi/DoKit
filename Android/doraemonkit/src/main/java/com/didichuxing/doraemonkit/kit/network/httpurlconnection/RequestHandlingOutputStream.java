package com.didichuxing.doraemonkit.kit.network.httpurlconnection;

import android.support.annotation.NonNull;

import com.didichuxing.doraemonkit.util.LogHelper;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

/**
 * @desc: OutputStream的包装类，在写入时记录写入的内容
 */
public class RequestHandlingOutputStream extends FilterOutputStream {
    private final WeakReference<URLConnectionManager> mManager;

    public RequestHandlingOutputStream(@NonNull OutputStream out, URLConnectionManager manager) {
        super(out);
        mManager = new WeakReference<>(manager);
    }

    @Override
    public void flush() throws IOException {
        super.flush();
    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {
        LogHelper.i("RequestHand","write()");
        if (mManager.get() != null) {
            mManager.get().mOutputStream.write(b, off, len);
        }
        super.write(b, off, len);
    }


    @Override
    public void close() throws IOException {
        super.close();
        mManager.clear();
    }
}
