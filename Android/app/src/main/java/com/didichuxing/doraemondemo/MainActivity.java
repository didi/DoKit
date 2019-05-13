package com.didichuxing.doraemondemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.common.CommonHeaders;
import com.didichuxing.doraemonkit.kit.network.common.CommonInspectorRequest;
import com.didichuxing.doraemonkit.kit.network.common.CommonInspectorResponse;
import com.didichuxing.doraemonkit.kit.network.common.NetworkPrinterHelper;
import com.didichuxing.doraemonkit.kit.timecounter.TimeCounterManager;
import com.didichuxing.doraemonkit.ui.realtime.RealTimeChartPage;
import com.didichuxing.doraemonkit.ui.realtime.datasource.DataSourceFactory;
import com.didichuxing.doraemonkit.util.threadpool.ThreadPoolProxyFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_test_urlconnection).setOnClickListener(this);
        findViewById(R.id.btn_test_okhttp).setOnClickListener(this);
        findViewById(R.id.btn_test_custom).setOnClickListener(this);
        findViewById(R.id.btn_test_crash).setOnClickListener(this);
        findViewById(R.id.btn_show_hide_icon).setOnClickListener(this);
        findViewById(R.id.btn_time_count).setOnClickListener(this);
        findViewById(R.id.btn_create_database).setOnClickListener(this);
        findViewById(R.id.btn_upload_test).setOnClickListener(this);
        findViewById(R.id.btn_download_test).setOnClickListener(this);
        okHttpClient = new OkHttpClient().newBuilder().build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_urlconnection:
                if (!NetworkManager.isActive()) {
                    String title = getString(com.didichuxing.doraemonkit.R.string.dk_kit_network_monitor);
                    int type = DataSourceFactory.TYPE_NETWORK;
                    NetworkManager.get().startMonitor();
                    RealTimeChartPage.openChartPage(title, type, RealTimeChartPage.DEFAULT_REFRESH_INTERVAL, null);
                    Toast.makeText(this, "click again", Toast.LENGTH_SHORT).show();
                    return;
                }
                requestByGet("http://apis.baidu.com/txapi/weixin/wxhot?num=10&page=1&word=%E7%9B%97%E5%A2%93%E7%AC%94%E8%AE%B0");
                break;
            case R.id.btn_test_okhttp:
                if (!NetworkManager.isActive()) {
                    String title = getString(com.didichuxing.doraemonkit.R.string.dk_kit_network_monitor);
                    int type = DataSourceFactory.TYPE_NETWORK;
                    NetworkManager.get().startMonitor();
                    RealTimeChartPage.openChartPage(title, type, RealTimeChartPage.DEFAULT_REFRESH_INTERVAL, null);
                    Toast.makeText(this, "click again", Toast.LENGTH_SHORT).show();
                    return;
                }
                requestByOkHttp();
                break;
            case R.id.btn_test_custom:
                if (!NetworkManager.isActive()) {
                    String title = getString(com.didichuxing.doraemonkit.R.string.dk_kit_network_monitor);
                    int type = DataSourceFactory.TYPE_NETWORK;
                    NetworkManager.get().startMonitor();
                    RealTimeChartPage.openChartPage(title, type, RealTimeChartPage.DEFAULT_REFRESH_INTERVAL, null);
                    Toast.makeText(this, "click again", Toast.LENGTH_SHORT).show();
                    return;
                }
                requestByCustom("http://apis.baidu.com/txapi/weixin/wxhot?num=10&page=1&word=%E7%9B%97%E5%A2%93%E7%AC%94%E8%AE%B0");
                break;
            case R.id.btn_test_crash:
                testCrash().length();
                break;
            case R.id.btn_time_count:
                // no-op版本未提供该方法，这里只是方便测试
                if (!TimeCounterManager.get().isRunning()) {
                    TimeCounterManager.get().start();
                    Toast.makeText(this, "click again", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(this, TestActivity.class));
                break;
            case R.id.btn_show_hide_icon:
                if (DoraemonKit.isShow()) {
                    DoraemonKit.hide();
                } else {
                    DoraemonKit.show();
                }
                break;
            case R.id.btn_create_database:
                MyDatabaseHelper dbHelper = new MyDatabaseHelper(this, "BookStore.db", null, 1);
                dbHelper.getWritableDatabase();
                dbHelper.close();
                break;
            case R.id.btn_upload_test:
                requestByFile(true);
                break;
            case R.id.btn_download_test:
                requestByFile(false);
                break;
            default:
                break;
        }
    }

    public String testCrash() {
        return null;
    }

    public void requestByGet(final String path) {
        ThreadPoolProxyFactory.getThreadPoolProxy().execute(new Runnable() {
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
        });
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
        Request request = new Request.Builder().get().url("http://www.roundsapp.com/post")
                .addHeader("testHead", "hahahah").build();
        Call call = okHttpClient.newCall(request);
        //异步调用并设置回调函数
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onHttpFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
            }
        });
    }

    /**
     * 手动添加网络抓包数据。目前只支持OKHttp3和HttpUrlConnection的自动注册，其他不基于OkHttp3和HttpUrlConnection的网络库如果
     * 想统计抓包数据，需要调用下面四个方法手动添加。添加方式如下
     * {@link NetworkPrinterHelper#obtainRequestId()}}
     * {@link NetworkPrinterHelper#updateRequest(CommonInspectorRequest)}
     * {@link NetworkPrinterHelper#updateResponse(CommonInspectorResponse)}
     * {@link NetworkPrinterHelper#updateResponseBody(int, String)}
     */
    public void requestByCustom(String url) {
        // obtain id for this request
        final int id = NetworkPrinterHelper.obtainRequestId();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Headers headers = request.headers();
                        CommonHeaders.Builder builder = new CommonHeaders.Builder();
                        for (int i = 0; i < headers.size(); i++) {
                            builder.add(headers.name(i), headers.value(i));
                        }
                        String body = null;
                        if (request.body() != null) {
                            body = request.body().toString();
                        }
                        // create request bean and update
                        CommonInspectorRequest rq = new CommonInspectorRequest(id, request.url().toString(), request.method(), body, builder.build());
                        NetworkPrinterHelper.updateRequest(rq);
                        Response response = chain.proceed(request);
                        headers = response.headers();
                        builder = new CommonHeaders.Builder();
                        for (int i = 0; i < headers.size(); i++) {
                            builder.add(headers.name(i), headers.value(i));
                        }
                        // create response bean and update
                        CommonInspectorResponse rp = new CommonInspectorResponse(id, rq.url(), response.code(), builder.build());
                        NetworkPrinterHelper.updateResponse(rp);
                        return response;
                    }
                }).build();
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onHttpFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                // update response body
                NetworkPrinterHelper.updateResponseBody(id, responseStr);
            }
        });
    }

    /**
     * 模拟上传或下载文件
     *
     * @param upload true上传 false下载
     */
    private void requestByFile(final boolean upload) {
        final ProgressDialog dialog = ProgressDialog.show(this, null, null);
        dialog.setCancelable(true);
        Request request = null;
        if (upload) {
            try {
                //模拟一个1M的文件用来上传
                final long length = 1L * 1024 * 1024;
                final File temp = new File(getFilesDir(), "test.tmp");
                if (!temp.exists() || temp.length() != length) {
                    final RandomAccessFile accessFile = new RandomAccessFile(temp, "rwd");
                    accessFile.setLength(length);
                    temp.createNewFile();
                }
                request = new Request.Builder()
                        .post(RequestBody.create(MediaType.parse(temp.getName()), temp))
                        .url("http://wallpaper.apc.360.cn/index.php?c=WallPaper&a=getAppsByOrder&order=create_time&start=0&count=1&from=360chrome")
                        .build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //下载一个2M的文件
            request = new Request.Builder()
                    .get()
                    .url("http://cdn1.lbesec.com/products/history/20131220/privacyspace_rel_2.2.1617.apk")
                    .build();
        }
        Call call = okHttpClient.newCall(request);
        final long startTime = SystemClock.uptimeMillis();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.cancel();
                onHttpFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    onFailure(call, new IOException(response.message()));
                    return;
                }
                final ResponseBody body = response.body();
                if (!upload) {
                    inputStream2File(body.byteStream(), new File(getFilesDir(), "test.apk"));
                }
                dialog.cancel();
                final long requestLength = upload ? call.request().body().contentLength() : 0;
                final long responseLength = body.contentLength() < 0 ? 0 : body.contentLength();
                final long endTime = SystemClock.uptimeMillis() - startTime;
                final long speed = (upload ? requestLength : responseLength) / endTime * 1000;
                final String message = String.format("请求大小：%s，响应大小：%s，耗时：%dms，均速：%s/s", Formatter.formatFileSize(getApplicationContext(), requestLength), Formatter.formatFileSize(getApplicationContext(), responseLength), endTime, Formatter.formatFileSize(getApplicationContext(), speed));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("onResponse", message);
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void onHttpFailure(final IOException e) {
        e.printStackTrace();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof UnknownHostException) {
                    Toast.makeText(MainActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                } else if (e instanceof SocketTimeoutException) {
                    Toast.makeText(MainActivity.this, "请求超时", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void inputStream2File(InputStream is, File saveFile) {
        try {
            int len;
            byte[] buf = new byte[2048];
            FileOutputStream fos = new FileOutputStream(saveFile);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            fos.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        okHttpClient.dispatcher().cancelAll();
    }
}
