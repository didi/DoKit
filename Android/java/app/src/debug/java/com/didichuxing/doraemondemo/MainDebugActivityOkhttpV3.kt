package com.didichuxing.doraemondemo

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.text.format.Formatter
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.amap.api.location.AMapLocationListener
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ThreadUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.didichuxing.doraemondemo.amap.AMapRouterFragment
import com.didichuxing.doraemondemo.comm.CommLauncher
import com.didichuxing.doraemondemo.mc.MCActivity
import com.didichuxing.doraemondemo.retrofit.GithubService
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.util.LogHelper
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.net.*

/**
 * @author jintai
 */
class MainDebugActivityOkhttpV3 : BaseActivity(), View.OnClickListener,
    CoroutineScope by MainScope() {
    private var okHttpClient: OkHttpClient? = null
    private var mLocationManager: LocationManager? = null
    private val UPDATE_UI = 100

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * github 接口
     */
    private var githubService: GithubService? = null

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                100 -> (findViewById<View>(R.id.iv_picasso) as ImageView).setImageBitmap(msg.obj as Bitmap)
                else -> {
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tvEnv = findViewById<TextView>(R.id.tv_env)
        tvEnv.text = "${getString(R.string.app_build_types)}:Debug"
        findViewById<View>(R.id.btn_jump).setOnClickListener(this)
        findViewById<View>(R.id.btn_webview).setOnClickListener(this)
        findViewById<View>(R.id.btn_installed_app).setOnClickListener(this)
        findViewById<View>(R.id.btn_x5_webview).setOnClickListener(this)
        findViewById<View>(R.id.btn_method_cost).setOnClickListener(this)
        findViewById<View>(R.id.btn_jump_leak).setOnClickListener(this)
        findViewById<View>(R.id.btn_mc).setOnClickListener(this)
        findViewById<View>(R.id.btn_app_launch_stack).setOnClickListener(this)
        findViewById<View>(R.id.btn_show_tool_panel).setOnClickListener(this)
        findViewById<View>(R.id.btn_location).setOnClickListener(this)
        findViewById<View>(R.id.btn_path_amap).setOnClickListener(this)
        findViewById<View>(R.id.btn_location_map2).setOnClickListener(this)
        findViewById<View>(R.id.btn_load_img).setOnClickListener(this)
        findViewById<View>(R.id.btn_okhttp_mock).setOnClickListener(this)
        findViewById<View>(R.id.btn_connection_mock).setOnClickListener(this)
        //        findViewById(R.id.btn_rpc_mock).setOnClickListener(this);
        findViewById<View>(R.id.btn_retrofit_mock).setOnClickListener(this)
        findViewById<View>(R.id.btn_test_crash).setOnClickListener(this)
        findViewById<View>(R.id.btn_show_hide_icon).setOnClickListener(this)
        findViewById<View>(R.id.btn_create_database).setOnClickListener(this)
        findViewById<View>(R.id.btn_upload_test).setOnClickListener(this)
        findViewById<View>(R.id.btn_download_test).setOnClickListener(this)
        okHttpClient = OkHttpClient().newBuilder().build()
        //获取定位服务
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //高德定位服务
        EasyPermissions.requestPermissions(
            PermissionRequest.Builder(
                this, 200,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).build()
        )
        //初始化
        val config = ImageLoaderConfiguration.Builder(this)
            .build()
        ImageLoader.getInstance().init(config)

        githubService = retrofit.create(GithubService::class.java)

        lifecycleScope.launch {

        }

//        AopTest().test()
    }

    private fun test1() {
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        test2()
    }

    private fun test2() {
        try {
            Thread.sleep(200)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        test3()
    }

    private fun test3() {
        try {
            Thread.sleep(200)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        test4()
    }

    private fun test4() {
        try {
            Thread.sleep(200)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private val mLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val string = "lat====>" + location.latitude + "  lng====>" + location.longitude
            Log.i(TAG, "系统定位====>$string")
        }

        override fun onProviderDisabled(arg0: String) {}
        override fun onProviderEnabled(arg0: String) {}
        override fun onStatusChanged(arg0: String, arg1: Int, arg2: Bundle) {}
    }

    /**
     * 启动普通定位
     */
    @SuppressLint("MissingPermission")
    private fun startNormaLocation() {
        mLocationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0f,
            mLocationListener
        )
    }

    /**
     * 启动高德定位服务
     */
    private var mapLocationListener = AMapLocationListener { aMapLocation ->
        val errorCode = aMapLocation.errorCode
        val errorInfo = aMapLocation.errorInfo
        Log.i(
            TAG,
            "高德定位===lat==>" + aMapLocation.latitude + "   lng==>" + aMapLocation.longitude + "  errorCode===>" + errorCode + "   errorInfo===>" + errorInfo
        )
    }


    @SuppressLint("MissingPermission")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_method_cost -> {
                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                val gpsStatus = locationManager.getGpsStatus(null)
                LogHelper.i("sss", "gpsStatus==>$gpsStatus")
                //test1()
            }
            R.id.btn_show_tool_panel ->                 //直接调起工具面板
                DoKit.showToolPanel()
            R.id.btn_jump -> startActivity(Intent(this, SecondActivity::class.java))
            R.id.btn_installed_app -> {
                val apps =
                    packageManager.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES)
            }
            R.id.btn_webview -> startActivity(Intent(this, WebViewNormalActivity::class.java))
            R.id.btn_x5_webview -> startActivity(Intent(this, WebViewX5Activity::class.java))
            R.id.btn_jump_leak -> startActivity(Intent(this, LeakActivity::class.java))
            R.id.btn_mc -> startActivity(Intent(this, MCActivity::class.java))
            R.id.btn_app_launch_stack -> {
                //MethodStackUtil.getInstance().toJson()
            }
            R.id.btn_location -> startNormaLocation()
            R.id.btn_path_amap -> CommLauncher.startActivity(AMapRouterFragment::class.java)
//            R.id.btn_location_map2 -> startActivity(Intent(
//                    this, MapShowingLocationActivity::class.java))
            R.id.btn_load_img -> {
                //Glide 加载
                val picassoImgUrl =
                    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2F2c.zol-img.com.cn%2Fproduct%2F124_500x2000%2F748%2FceZOdKgDAFsq2.jpg&refer=http%3A%2F%2F2c.zol-img.com.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1621652979&t=850e537c70eaa7753e892bc8b4d05f57"
                val glideImageUrl =
                    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2F1812.img.pp.sohu.com.cn%2Fimages%2Fblog%2F2009%2F11%2F18%2F18%2F8%2F125b6560a6ag214.jpg&refer=http%3A%2F%2F1812.img.pp.sohu.com.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1621652979&t=76d35d35d9c510f1c24a422e9e02fd46"
                val frescoImageUrl =
                    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fyouimg1.c-ctrip.com%2Ftarget%2Ftg%2F035%2F063%2F726%2F3ea4031f045945e1843ae5156749d64c.jpg&refer=http%3A%2F%2Fyouimg1.c-ctrip.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1621652979&t=7150aaa2071d512cf2f6b556e126dd66"
                val imageLoaderImageUrl =
                    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fyouimg1.c-ctrip.com%2Ftarget%2Ftg%2F004%2F531%2F381%2F4339f96900344574a0c8ca272a7b8f27.jpg&refer=http%3A%2F%2Fyouimg1.c-ctrip.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1621652979&t=b7e83ecc987c64cc31079469d292eb56"
                Picasso.get().load(picassoImgUrl)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.mipmap.dk_health_bg)
                    .error(R.mipmap.dk_health_bg)
                    .into(findViewById<View>(R.id.iv_picasso) as ImageView)
                Glide.with(this@MainDebugActivityOkhttpV3)
                    .asBitmap()
                    .load(glideImageUrl)
                    .placeholder(R.mipmap.dk_health_bg)
                    .error(R.mipmap.dk_health_bg)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .transform(CircleCrop())
                    .into((findViewById<View>(R.id.iv_glide) as ImageView))
                val imageLoader = ImageLoader.getInstance()
                imageLoader.displayImage(
                    imageLoaderImageUrl,
                    findViewById<View>(R.id.iv_imageloader) as ImageView
                )
                val frescoImageView = findViewById<SimpleDraweeView>(R.id.iv_fresco)
                frescoImageView.setImageURI(Uri.parse(frescoImageUrl))
                val imagePipeline = Fresco.getImagePipeline()
                // combines above two lines
                imagePipeline.clearCaches()
            }
            R.id.btn_okhttp_mock -> {
                //DokitOkGo.<String>get("https://www.tianqiapi.com/api")
//                DokitOkGo.post<String>("http://www.v2ex.com/api/topics/hot.json?aaa=aaa&bbb=bbb")
//                        .params("ccc", "ccc")
//                        .params("ddd", "ddd")
//                        .execute(object : StringCallback() {
//                            override fun onSuccess(response: Response<String>) {
//                                Log.i(TAG, "okhttp====onSuccess===>" + response.body())
//                            }
//
//                            override fun onError(response: Response<String>) {
//                                Log.i(TAG, "okhttp====onError===>" + response.message())
//                            }
//                        })

                val json = JSONObject()
                //json
                OkGo.get<String>("https://wanandroid.com/user_article/list/0/json")
                    //.upJson(json.toString())
                    .execute(object : StringCallback() {
                        override fun onSuccess(response: Response<String>?) {
                            response?.let {
                                Log.i(TAG, "okhttp====onSuccess===>" + it.body())
                            }
                        }

                        override fun onError(response: Response<String>?) {
                            response?.let {
                                Log.i(TAG, "okhttp====onError===>" + it.message())
                            }
                        }

                    })


            }


            R.id.btn_connection_mock -> {
                //requestByGet("https://www.v2ex.com/api/topics/hot.json");
                //requestByGet("https://gank.io/api/today?a=哈哈&b=bb")
                requestByGet("https://wanandroid.com/user_article/list/0/json")
                //requestByGet("https://ant.pingan.com.cn/c.gif?title=非车险产品dock&url=http://com.pingan.lifecircle/feiche_AllProductPage&eventName=pageview&refererUrlTime=400&referer=http://com.pingan.lifecircle/personal_MainPage&siteId=QRM79jeIRrN2B565&userProperty=%7B\"userIdType\":\"aopsId\"%7D&resolution=1080*2340&userAgent=Dalvik/2.1.0(Linux;U;Android9;RedmiNote8ProMIUI/V11.0.6.0.PGGCNXM)&language=zh&apiv=2&seStartTime=1592363736562&eventTime=1592363794558&userId=138068825&netType=4G&customerVar=%7B\"sdkVersion\":\"2.2.8.6\",\"macAddress\":\"A4:45:19:38:4A:5A\",\"gpsFlag\":\"0\",\"imei\":\"\"%7D&uaOs=Android&uaOsMajor=&uaOsMinor=&uaDevice=RedmiNote8Pro&downloadChannel=ch1&appVersion=1.13.1&deviceId=b69a044c294af87f&sessionId=8416a37db255daa4&projectId=OCUTeffvo02r7nmj")
            }
            R.id.btn_retrofit_mock -> {
                githubService?.githubUserInfo("jtsky")
                    ?.subscribeOn(Schedulers.io())
                    ?.subscribe(
                        { Log.i(TAG, "githubUserInfo===>${it.login}") },
                        { Log.e(TAG, "Request failed by retrofit mock", it) }
                    )
            }
            R.id.btn_test_crash -> testCrash()!!.length
            R.id.btn_show_hide_icon -> if (DoKit.isMainIconShow) {
                DoKit.hide()
            } else {
                DoKit.show()
            }
            R.id.btn_create_database -> {
                val dbHelper = MyDatabaseHelper(this, "BookStore.db", null, 1)
                dbHelper.writableDatabase
                dbHelper.close()
            }
            R.id.btn_upload_test -> requestByFile(true)
            R.id.btn_download_test -> requestByFile(false)
            else -> {
            }
        }
    }

    private fun testCrash(): String? {
        return null
    }

    private fun requestByGet(path: String) {
        ThreadUtils.executeByIo(object : ThreadUtils.SimpleTask<String?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): String {
                try {
                    val url = URL(path.trim())
                    //打开连接
                    val urlConnection = url.openConnection() as HttpURLConnection
                    //urlConnection.setRequestProperty("token", "10051:abc");
                    //urlConnection.setRequestProperty("Content-type", "application/json");
                    //int log = urlConnection.getResponseCode();
                    //得到输入流
                    val `is` = urlConnection.inputStream
                    return ConvertUtils.inputStream2String(`is`, "utf-8")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return "error"
            }


            override fun onSuccess(result: String?) {
                Log.i(TAG, "httpUrlConnection====response===>===>$result")
            }
        })
    }


    /**
     * 模拟上传或下载文件
     *
     * @param upload true上传 false下载
     */
    private fun requestByFile(upload: Boolean) {
        val dialog = ProgressDialog.show(this, null, null)
        dialog.setCancelable(true)
        var request: Request? = null
        if (upload) {
            try {
                //模拟一个1M的文件用来上传
                val length = 1L * 1024 * 1024
                val temp = File(filesDir, "test.tmp")
                if (!temp.exists() || temp.length() != length) {
                    val accessFile = RandomAccessFile(temp, "rwd")
                    accessFile.setLength(length)
                    temp.createNewFile()
                }
                request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse(temp.name), temp))
                    .url("http://wallpaper.apc.360.cn/index.php?c=WallPaper&a=getAppsByOrder&order=create_time&start=0&count=1&from=360chrome")
                    .build()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            //下载一个2M的文件
            request = Request.Builder()
                .get()
                .url("http://cdn1.lbesec.com/products/history/20131220/privacyspace_rel_2.2.1617.apk")
                .build()
        }
        val call = okHttpClient!!.newCall(request!!)
        val startTime = SystemClock.uptimeMillis()
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                dialog.cancel()
                onHttpFailure(e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    onFailure(call, IOException(response.message()))
                    return
                }
                val body = response.body()
                if (!upload) {
                    inputStream2File(body!!.byteStream(), File(filesDir, "test.apk"))
                }
                dialog.cancel()
                val requestLength = if (upload) call.request().body()!!.contentLength() else 0
                val responseLength = if (body!!.contentLength() < 0) 0 else body.contentLength()
                val endTime = SystemClock.uptimeMillis() - startTime
                val speed = (if (upload) requestLength else responseLength) / endTime * 1000
                val message = String.format(
                    "请求大小：%s，响应大小：%s，耗时：%dms，均速：%s/s",
                    Formatter.formatFileSize(applicationContext, requestLength),
                    Formatter.formatFileSize(applicationContext, responseLength),
                    endTime,
                    Formatter.formatFileSize(applicationContext, speed)
                )
                runOnUiThread {
                    Log.d("onResponse", message)
                    Toast.makeText(this@MainDebugActivityOkhttpV3, message, Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
    }

    private fun onHttpFailure(e: IOException) {
        e.printStackTrace()
        runOnUiThread {
            if (e is UnknownHostException) {
                Toast.makeText(this@MainDebugActivityOkhttpV3, "网络异常", Toast.LENGTH_SHORT).show()
            } else if (e is SocketTimeoutException) {
                Toast.makeText(this@MainDebugActivityOkhttpV3, "请求超时", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainDebugActivityOkhttpV3, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun inputStream2File(`is`: InputStream, saveFile: File) {
        try {
            var len: Int
            val buf = ByteArray(2048)
            val fos = FileOutputStream(saveFile)
            while (`is`.read(buf).also { len = it } != -1) {
                fos.write(buf, 0, len)
            }
            fos.flush()
            fos.close()
            `is`.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        okHttpClient!!.dispatcher().cancelAll()
        mLocationManager!!.removeUpdates(mLocationListener)
    }

    private fun requestImage(urlStr: String) {
        try {
            //
            val url = URL(urlStr)
            // http    https
            // ftp
            val urlConnection = url.openConnection() as HttpURLConnection
            //http get post
            urlConnection.requestMethod = "GET"
            urlConnection.connectTimeout = 5000
            urlConnection.readTimeout = 5000
            val responseCode = urlConnection.responseCode
            if (responseCode == 200) {
                val bitmap = BitmapFactory.decodeStream(urlConnection.inputStream)
                //更新 ui
                mHandler.sendMessage(mHandler.obtainMessage(UPDATE_UI, bitmap))
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val TAG = "MainDebugActivity"

    }
}