package com.didichuxing.doraemonkit.kit.network.httpurlconnection;

import java.io.ByteArrayOutputStream;

public interface IStreamCompleteListener {
    void onOutputStreamComplete(ByteArrayOutputStream outputStream);
}