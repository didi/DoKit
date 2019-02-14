package com.didichuxing.doraemondemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.didichuxing.doraemonkit.DoraemonKit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_test_urlconnection).setOnClickListener(this);
        findViewById(R.id.btn_test_okhttp).setOnClickListener(this);
        findViewById(R.id.btn_test_crash).setOnClickListener(this);
        findViewById(R.id.btn_switch_floating_Window).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_urlconnection:
                requestByPost("http://apis.baidu.com/txapi/weixin/wxhot?num=10&page=1&word=%E7%9B%97%E5%A2%93%E7%AC%94%E8%AE%B0");
                break;
            case R.id.btn_test_okhttp:
                requestByOkHttp();
                break;
            case R.id.btn_test_crash:
                testCrash().length();
                break;
            case R.id.btn_switch_floating_Window:
                switchFloatingWindow();
                break;
            default:
                break;
        }
    }


    public void switchFloatingWindow() {
        DoraemonKit.setFloatingWindow(!DoraemonKit.isFloatingWindowShowing());
    }

    public String testCrash() {
        return null;
    }

    public void requestByGet(final String path) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path.trim());
                    //打开连接
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("token", "10051:abc");
                    urlConnection.setRequestProperty("Content-type", "application/json");
                    int log = urlConnection.getResponseCode();
                    if (200 == log) {
                        //得到输入流
                        InputStream is = urlConnection.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while (-1 != (len = is.read(buffer))) {
                            baos.write(buffer, 0, len);
                            baos.flush();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    public void requestByPost(final String path) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path.trim());
                    //打开连接
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("token", "10051:abc");
                    urlConnection.setRequestProperty("Content-type", "application/json");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();
                    PrintWriter printWriter = new PrintWriter(urlConnection.getOutputStream());
                    printWriter.write("name=孙群&age=27");
                    printWriter.flush();
                    int log = urlConnection.getResponseCode();
                    if (200 == log) {
                        //得到输入流
                        InputStream is = urlConnection.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while (-1 != (len = is.read(buffer))) {
                            baos.write(buffer, 0, len);
                            baos.flush();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void requestByOkHttp() {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder().get().url("http://www.roundsapp.com/post")
                .addHeader("testHead", "hahahah").build();
        Call call = client.newCall(request);
        //异步调用并设置回调函数
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
            }
        });
    }
}
