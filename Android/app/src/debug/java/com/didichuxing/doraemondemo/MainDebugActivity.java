package com.didichuxing.doraemondemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.didichuxing.doraemondemo.util.FrescoUtil;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.kit.largepicture.glide.LargeBitmapGlideTransformation;
import com.didichuxing.doraemonkit.kit.largepicture.picasso.LargeBitmapPicassoTransformation;
import com.didichuxing.doraemonkit.kit.methodtrace.MethodCost;
import com.didichuxing.doraemonkit.kit.network.common.CommonHeaders;
import com.didichuxing.doraemonkit.kit.network.common.CommonInspectorRequest;
import com.didichuxing.doraemonkit.kit.network.common.CommonInspectorResponse;
import com.didichuxing.doraemonkit.kit.network.common.NetworkPrinterHelper;
import com.didichuxing.doraemonkit.okgo.OkGo;
import com.didichuxing.doraemonkit.okgo.callback.StringCallback;
import com.didichuxing.doraemonkit.okgo.model.Response;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;


public class MainDebugActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainDebugActivity";


    private OkHttpClient okHttpClient;
    private LocationManager mLocationManager;
    AMapLocationClient mLocationClient;
    LocationClient mBaiduLocationClient;
    AMapLocationClientOption mMapOption;
    TencentLocationRequest mTencentLocationRequest;
    TencentLocationManager mTencentLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        TextView tvEnv = findViewById(R.id.tv_env);
        tvEnv.setText("当前编译环境:Debug");
        findViewById(R.id.btn_trace).setOnClickListener(this);
        findViewById(R.id.btn_jump).setOnClickListener(this);
        findViewById(R.id.btn_jump_leak).setOnClickListener(this);
        findViewById(R.id.btn_show_tool_panel).setOnClickListener(this);
        findViewById(R.id.btn_location).setOnClickListener(this);
        findViewById(R.id.btn_location_amap).setOnClickListener(this);
        findViewById(R.id.btn_location_tencent).setOnClickListener(this);
        findViewById(R.id.btn_location_baidu).setOnClickListener(this);
        findViewById(R.id.btn_load_img).setOnClickListener(this);
        findViewById(R.id.btn_okhttp_mock).setOnClickListener(this);
        findViewById(R.id.btn_connection_mock).setOnClickListener(this);
//        findViewById(R.id.btn_rpc_mock).setOnClickListener(this);
        findViewById(R.id.btn_test_crash).setOnClickListener(this);
        findViewById(R.id.btn_show_hide_icon).setOnClickListener(this);
        findViewById(R.id.btn_create_database).setOnClickListener(this);
        findViewById(R.id.btn_upload_test).setOnClickListener(this);
        findViewById(R.id.btn_download_test).setOnClickListener(this);
        okHttpClient = new OkHttpClient().newBuilder().build();
        //获取定位服务
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //高德定位服务
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mMapOption = new AMapLocationClientOption();
        //腾讯地图
        mTencentLocationRequest = TencentLocationRequest.create();
        mTencentLocationManager = TencentLocationManager.getInstance(getApplicationContext());
        //百度地图
        mBaiduLocationClient = new LocationClient(this);
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        // 打开gps
        option.setOpenGps(true);
        // 设置坐标类型
        option.setCoorType("bd09ll");
        option.setScanSpan(5000);
        mBaiduLocationClient.setLocOption(option);
        //获取获取当前单次定位
        mBaiduLocationClient.registerLocationListener(mbdLocationListener);
        EasyPermissions.requestPermissions(new PermissionRequest
                .Builder(this, 200,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).build());
    }


    private void test1() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        test2();
    }

    private void test2() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        test3();
    }

    private void test3() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        test4();
    }


    private void test4() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                String string = "lat====>" + location.getLatitude() + "  lng====>" + location.getLongitude();
                Log.i(TAG, "系统定位====>" + string);

            }
        }

        @Override
        public void onProviderDisabled(String arg0) {
        }

        @Override
        public void onProviderEnabled(String arg0) {
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        }
    };

    /**
     * 启动普通定位
     */
    @SuppressLint("MissingPermission")
    public void startNormaLocation() {

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);

    }

    /**
     * 启动高德定位服务
     */
    AMapLocationListener mapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            int errorCode = aMapLocation.getErrorCode();
            String errorInfo = aMapLocation.getErrorInfo();
            Log.i(TAG, "高德定位===lat==>" + aMapLocation.getLatitude() + "   lng==>" + aMapLocation.getLongitude() + "  errorCode===>" + errorCode + "   errorInfo===>" + errorInfo);
        }
    };

    /**
     * 启动高德地图定位
     */
    public void startAmapLocation() {
        mLocationClient.setLocationListener(mapLocationListener);
        mMapOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mMapOption.setOnceLocation(true);
        mLocationClient.setLocationOption(mMapOption);
        mLocationClient.stopLocation();
        mLocationClient.startLocation();
    }


    TencentLocationListener mTencentLocationListener = new TencentLocationListener() {
        @Override
        public void onLocationChanged(TencentLocation tencentLocation, int error, String errorInfo) {
            Log.i(TAG, "腾讯定位===onLocationChanged===lat==>" + tencentLocation.getLatitude() + "   lng==>" + tencentLocation.getLongitude() + "  error===>" + error + "  errorInfo===>" + errorInfo);
        }

        @Override
        public void onStatusUpdate(String name, int status, String desc) {
            Log.i(TAG, "腾讯定位===onStatusUpdate==>" + "  name===>" + name + " status===" + status + "  desc===" + desc);
        }
    };

    /**
     * 启动腾讯地图定位
     */
    public void startTencentLocation() {
        //mTencentLocationManager.requestLocationUpdates(mTencentLocationRequest, mTencentLocationListener);
        //获取获取当前单次定位
        mTencentLocationManager.requestSingleFreshLocation(mTencentLocationRequest, mTencentLocationListener, Looper.myLooper());
    }


    BDAbstractLocationListener mbdLocationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Log.i(TAG, "百度定位===onReceiveLocation===lat==>" + bdLocation.getLatitude() + "   lng==>" + bdLocation.getLongitude());
        }
    };


    /**
     * 启动百度地图定位
     */
    public void startBaiDuLocation() {
        mBaiduLocationClient.stop();
        mBaiduLocationClient.start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_trace:
                MethodCost.startMethodTracing("doramemon");
                test1();
                MethodCost.stopMethodTracingAndPrintLog("doramemon");
                break;

            case R.id.btn_show_tool_panel:
                //直接调起工具面板
                DoraemonKit.showToolPanel();
                break;

            case R.id.btn_jump:

                startActivity(new Intent(this, SecondActivity.class));

                break;
            case R.id.btn_jump_leak:

                startActivity(new Intent(this, LeakActivity.class));

                break;
            case R.id.btn_location:

                startNormaLocation();

                break;

            case R.id.btn_location_amap:
                startAmapLocation();
                break;

            case R.id.btn_location_tencent:
                startTencentLocation();
                break;

            case R.id.btn_location_baidu:
                startBaiDuLocation();
                break;

            case R.id.btn_load_img:
                //Glide 加载
                String imgUrl = "http://b-ssl.duitang.com/uploads/item/201808/27/20180827043223_twunu.jpg";
                Glide.with(MainDebugActivity.this)
                        .asBitmap()
                        .load(imgUrl)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .transform(new LargeBitmapGlideTransformation(imgUrl))
                        .into((ImageView) findViewById(R.id.iv_glide));

                Picasso.get().load(imgUrl)
                        .transform(new LargeBitmapPicassoTransformation(imgUrl))
                        .into((ImageView) findViewById(R.id.iv_picasso));

                FrescoUtil.loadImage((SimpleDraweeView) findViewById(R.id.iv_fresco), imgUrl);

                break;


            case R.id.btn_okhttp_mock:
                //OkGo.<String>get("http://gank.io/gateway?api=dj.map")
                OkGo.<String>get("https://www.v2ex.com/api/topics/hot.json")
                        .execute(new StringCallback() {


                            @Override
                            public void onSuccess(Response<String> response) {
                                Log.i(TAG, "okhttp====response===>" + response.body());
                            }
                        });
                break;

            case R.id.btn_connection_mock:
                //requestByGet("https://www.v2ex.com/api/topics/hot.json");
                //requestByGet("https://gank.io/api/today?a=哈哈&b=bb");
                requestByGet("http://gank.io/gateway?api=dj.map");
                break;
//            case R.id.btn_rpc_mock:
//                break;

            case R.id.btn_test_custom:

                requestByCustom("http://apis.baidu.com/txapi/weixin/wxhot?num=10&page=1&word=%E7%9B%97%E5%A2%93%E7%AC%94%E8%AE%B0");
                break;
            case R.id.btn_test_crash:
                testCrash().length();
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
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() throws Throwable {
                try {
                    URL url = new URL(path.trim());
                    //打开连接
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    //urlConnection.setRequestProperty("token", "10051:abc");
                    //urlConnection.setRequestProperty("Content-type", "application/json");
                    //int log = urlConnection.getResponseCode();
                    //得到输入流
                    InputStream is = urlConnection.getInputStream();
                    return ConvertUtils.inputStream2String(is, "utf-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "error";
            }

            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "httpUrlConnection====response===>===>" + result);
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
                    public okhttp3.Response intercept(Chain chain) throws IOException {
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
                        // create request bean and updateInterceptApi
                        CommonInspectorRequest rq = new CommonInspectorRequest(id, request.url().toString(), request.method(), body, builder.build());
                        NetworkPrinterHelper.updateRequest(rq);
                        okhttp3.Response response = chain.proceed(request);
                        headers = response.headers();
                        builder = new CommonHeaders.Builder();
                        for (int i = 0; i < headers.size(); i++) {
                            builder.add(headers.name(i), headers.value(i));
                        }
                        // create response bean and updateInterceptApi
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
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                final String responseStr = response.body().string();
                // updateInterceptApi response body
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
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
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
                        Toast.makeText(MainDebugActivity.this, message, Toast.LENGTH_LONG).show();
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
                    Toast.makeText(MainDebugActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                } else if (e instanceof SocketTimeoutException) {
                    Toast.makeText(MainDebugActivity.this, "请求超时", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainDebugActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

        mLocationManager.removeUpdates(mLocationListener);
        mTencentLocationManager.removeUpdates(mTencentLocationListener);
        mBaiduLocationClient.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
