package com.didichuxing.doraemonkit.kit.connect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.kit.connect.data.LoginData;
import com.didichuxing.doraemonkit.kit.connect.data.PackageType;
import com.didichuxing.doraemonkit.kit.connect.data.TextPackage;
import com.didichuxing.doraemonkit.kit.connect.ws.OkHttpWebSocketSession;
import com.didichuxing.doraemonkit.kit.connect.ws.OnWebSocketMessageListener;
import com.didichuxing.doraemonkit.kit.connect.ws.OnWebSocketStatusChangeListener;
import com.didichuxing.doraemonkit.util.GsonUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * didi Create on 2022/4/12 .
 * <p>
 * Copyright (c) 2022/4/12 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/12 11:56 上午
 * @Description 用一句话说明文件功能
 */

public class WebSocketSessionTest {

    private OkHttpClient client;

    @Before
    public void init() {
        client = new OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(3, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(3, TimeUnit.SECONDS)//设置连
            .build();
    }


    @Test
    public void testConnect() throws Exception {
        OkHttpWebSocketSession session = new OkHttpWebSocketSession(client);

        session.setOnWebSocketMessageListener(new OnWebSocketMessageListener() {
            @Override
            public void onMessage(@NonNull OkHttpWebSocketSession webSocket, @NonNull String text) {
                log("onMessage() text=" + text);
            }
        });
        session.setOnWebSocketStatusChangeListener(new OnWebSocketStatusChangeListener() {
            @Override
            public void onClosed(@NonNull OkHttpWebSocketSession webSocket, int code, @NonNull String reason) {
                log("onClosed() code=" + code + ",reason=" + reason);
            }

            @Override
            public void onOpen(@NonNull OkHttpWebSocketSession webSocket, @NonNull String response) {
                log("onOpen() response=" + response);

                LoginData loginData = new LoginData("dn", "android", "9.0", "300*400",
                    "MAC", "0.0.0.0", "", "test", "1.0");
                String data = GsonUtils.toJson(loginData);
                TextPackage textPackage = new TextPackage("pid0", PackageType.LOGIN, data,
                    "android", "cos", "text", "", 0);
                String text = GsonUtils.toJson(textPackage);
                webSocket.send(text);

                ByteParserTest byteParserTest = new ByteParserTest();

                session.send(byteParserTest.getByteString());
            }

            @Override
            public void onFailure(@NonNull OkHttpWebSocketSession webSocket, @NonNull Throwable t, @Nullable String response) {
                log("onFailure() response=" + response);
            }

            @Override
            public void onClosing(@NonNull OkHttpWebSocketSession webSocket, int code, @NonNull String reason) {
                log("onClosing() code=" + code + ",reason=" + reason);
            }
        });
        session.connect("ws://172.23.141.219:8000/proxy/userInterfaceAutomation/XLQBYYHP");



        Thread.currentThread().join();
    }

    public void log(String msg) {
        String tn = Thread.currentThread().getName();
        System.out.println("WS[" + tn + "]::" + msg);
    }
}
