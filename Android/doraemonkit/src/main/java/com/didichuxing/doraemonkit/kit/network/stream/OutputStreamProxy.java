package com.didichuxing.doraemonkit.kit.network.stream;

import android.support.annotation.NonNull;

import com.didichuxing.doraemonkit.kit.network.httpurlconnection.IStreamCompleteListener;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @desc: OutputStream的包装类，在写入时记录写入的内容
 */
public class OutputStreamProxy extends FilterOutputStream {
    private IStreamCompleteListener mListener = null;
    private ByteArrayOutputStream mOutputStream = new ByteArrayOutputStream();


    public void setStreamCompleteListener(IStreamCompleteListener l) {
        mListener = l;
    }


    private void onStreamComplete() {
        if (mListener != null) {
            mListener.onOutputStreamComplete(mOutputStream);
        }
    }

    public OutputStreamProxy(@NonNull OutputStream out) {
        super(out);
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        onStreamComplete();
    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
        mOutputStream.write(b, off, len);
    }


    @Override
    public void close() throws IOException {
        super.close();
    }
}
