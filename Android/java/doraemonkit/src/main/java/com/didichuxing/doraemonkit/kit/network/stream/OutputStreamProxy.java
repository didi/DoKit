package com.didichuxing.doraemonkit.kit.network.stream;


import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @desc: OutputStream的包装类，在写入时记录写入的内容
 */
public abstract class OutputStreamProxy extends FilterOutputStream {
    protected final ByteArrayOutputStream mOutputStream = new ByteArrayOutputStream();


    public OutputStreamProxy(@NonNull OutputStream out) {
        super(out);
    }

    protected abstract void onStreamComplete() throws IOException;

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
