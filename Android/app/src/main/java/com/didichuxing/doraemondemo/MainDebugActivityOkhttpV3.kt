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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.amap.api.location.AMapLocationListener
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ThreadUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.didichuxing.doraemondemo.amap.AMapRouterFragment
import com.didichuxing.doraemondemo.comm.CommLauncher
import com.didichuxing.doraemondemo.dokit.BorderDoKitView
import com.didichuxing.doraemondemo.mc.MCActivity
import com.didichuxing.doraemondemo.retrofit.GithubService
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.constant.CachesKey
import com.didichuxing.doraemonkit.kit.crash.CrashCaptureManager
import com.didichuxing.doraemonkit.kit.mc.all.ui.McDialogDoKitView
import com.didichuxing.doraemonkit.util.DoKitImageUtil
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
import kotlin.coroutines.resume

/**
 * @author jintai
 */
class MainDebugActivityOkhttpV3 : BaseActivity(), View.OnClickListener,
    CoroutineScope by MainScope() {
    private var okHttpClient: OkHttpClient? = null
    private var mLocationManager: LocationManager? = null
    private val UPDATE_UI = 100
    private lateinit var mAdapter: MainAdapter

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


    val datas = mutableListOf(
        "测试",
        "显示/隐藏Dokit入口",
        "显示工具面板",
        "获取已安装的app",
        "截屏",
        "跳转其他Activity",
        "一机多控",
        "NormalWebView",
        "X5WebView",
        "模拟内存泄漏",
        "函数调用耗时(TAG:MethodCostUtil)",
        "获取位置信息(系统)",
        "高德路径规划",
        "OkHttp Mock",
        "HttpURLConnection Mock",
        "Retrofit Mock",
        "模拟Crash",
        "创建数据库",
        "上传文件",
        "下载文件"
    )


    fun getCrashCacheDir(): File {
        val dir: File =
            File(cacheDir.toString() + File.separator + CachesKey.CRASH_HISTORY)
        if (!dir.exists()) {
            dir.mkdir()
        }
        return dir
    }

    suspend fun sleep() = suspendCancellableCoroutine<String> {
        Thread.sleep(5000)
        it.resume("sleep 1000ms")
    }


    fun sleep2(): String {
        Thread.sleep(5000)
        return "sleep 1000ms"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tvEnv = findViewById<TextView>(R.id.tv_env)
        tvEnv.text = "${getString(R.string.app_build_types)}:Debug"
        val rv = findViewById<RecyclerView>(R.id.rv)
        rv.layoutManager = LinearLayoutManager(this)
        mAdapter = MainAdapter(R.layout.item_main_rv, datas)
        rv.adapter = mAdapter
        mAdapter.setOnItemClickListener { _, view, position ->
            when (datas[position]) {
                "测试" -> {
                    val bundle = Bundle()
                    bundle.putString("text", "测试同步异常")
                    DoKit.launchFloating<McDialogDoKitView>(bundle = bundle)
                }
                "显示/隐藏Dokit入口" -> {
                    if (DoKit.isMainIconShow) {
                        DoKit.hide()
                    } else {
                        DoKit.show()
                    }

                }
                "显示工具面板" -> {
                    DoKit.showToolPanel()
                }
                "获取已安装的app" -> {
                    packageManager.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES)
                }
                "截屏" -> {
                    lifecycleScope.launch {

                        DoKit.launchFloating(BorderDoKitView::class.java)
                        val borderDoKitView =
                            DoKit.getDoKitView(
                                this@MainDebugActivityOkhttpV3,
                                BorderDoKitView::class.java
                            ) as BorderDoKitView
                        borderDoKitView.showBorder(view)
                        withContext(Dispatchers.IO) {
                            delay(200)
                            val bitmap = ScreenUtils.screenShot(this@MainDebugActivityOkhttpV3)
                            val output = File(getCrashCacheDir(), "test.png")
                            DoKitImageUtil.bitmap2File(bitmap, 100, output)
                        }

                    }

                }
                "跳转其他Activity" -> {
                    startActivity(Intent(this, SecondActivity::class.java))
                }
                "一机多控" -> {
                    startActivity(Intent(this, MCActivity::class.java))
                }
                "NormalWebView" -> {
                    startActivity(Intent(this, WebViewNormalActivity::class.java))
                }
                "X5WebView" -> {
                    startActivity(Intent(this, WebViewX5Activity::class.java))
                }

                "模拟内存泄漏" -> {
                    startActivity(Intent(this, LeakActivity::class.java))
                }

                "函数调用耗时(TAG:MethodCostUtil)" -> {
                    test1()
                }
                "获取位置信息(系统)" -> {
                    startNormaLocation()
                }
                "高德路径规划" -> {
                    CommLauncher.startActivity(AMapRouterFragment::class.java, this)
                }
                "OkHttp Mock" -> {
                    val jsonObject = JSONObject()
                    jsonObject.put("c", "cc")
                    jsonObject.put("d", "dd")
                    OkGo.post<String>("https://wanandroid.com/user_article/list/0/json?b=bb&a=aa")
                        .upJson(jsonObject)
                        .execute(object : StringCallback() {
                            override fun onSuccess(response: Response<String>?) {
                                response?.let {
                                    Log.i(TAG, "okhttp====onSuccess===>" + it.body())
                                }
                            }

                        })


//                    OkGo.post<String>("https://wanandroid.com/user_article/list/0/json?b=bb&a=aa")
//                        .params("c", "cc")
//                        .params("d", "dd")
//                        .execute()
//                    OkGo.get<String>("https://wanandroid.com/user_article/list/0/json?a=aa&b=bb")
//                        //.upJson(json.toString())
//                        .execute(object : StringCallback() {
//                            override fun onSuccess(response: Response<String>?) {
//                                response?.let {
//                                    Log.i(
//                                        MainDebugActivityOkhttpV3.TAG,
//                                        "okhttp====onSuccess===>" + it.body()
//                                    )
//                                }
//                            }
//
//                            override fun onError(response: Response<String>?) {
//                                response?.let {
//                                    Log.i(
//                                        MainDebugActivityOkhttpV3.TAG,
//                                        "okhttp====onError===>" + it.message()
//                                    )
//                                }
//                            }
//
//                        })
                }
                "HttpURLConnection Mock" -> {
                    requestByGet("https://wanandroid.com/user_article/list/0/json")
                }
                "Retrofit Mock" -> {
                    githubService?.githubUserInfo("jtsky")
                        ?.subscribeOn(Schedulers.io())
                        ?.subscribe(
                            {
                                Log.i(
                                    MainDebugActivityOkhttpV3.TAG,
                                    "githubUserInfo===>${it.login}"
                                )
                            },
                            {
                                Log.e(
                                    MainDebugActivityOkhttpV3.TAG,
                                    "Request failed by retrofit mock",
                                    it
                                )
                            }
                        )
                }
                "模拟Crash" -> {
                    testCrash()!!.length
                }
                "创建数据库" -> {
                    val dbHelper = MyDatabaseHelper(this, "BookStore.db", null, 1)
                    dbHelper.writableDatabase
                    dbHelper.close()
                }
                "上传文件" -> {
                    requestByFile(true)
                }
                "下载文件" -> {
                    requestByFile(false)
                }
                else -> {
                }
            }
        }
        findViewById<View>(R.id.btn_load_img).setOnClickListener(this)

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
                val coilImageUrl =
                    "https://cdn.nlark.com/yuque/0/2020/png/252337/1587091196083-assets/web-upload/62122ab5-986b-4662-be88-d3007a5e31c5.png"
                Picasso.get().load(picassoImgUrl)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.mipmap.cat)
                    .error(R.mipmap.cat)
                    .into(findViewById<View>(R.id.iv_picasso) as ImageView)
                Glide.with(this@MainDebugActivityOkhttpV3)
                    .asBitmap()
                    .load(glideImageUrl)
                    .placeholder(R.mipmap.cat)
                    .error(R.mipmap.cat)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .transform(CircleCrop())
                    .into((findViewById<View>(R.id.iv_glide) as ImageView))
                //coil
                findViewById<ImageView>(R.id.iv_coil).apply {
                    val request = coil.request.ImageRequest.Builder(this.context)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .transformations(CircleCropTransformation())
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .data(coilImageUrl)
                        .target(this)
                        .build()
                    imageLoader.enqueue(request)
                }
                //imageLoader
                val imageLoader = ImageLoader.getInstance()
                imageLoader.displayImage(
                    imageLoaderImageUrl,
                    findViewById<View>(R.id.iv_imageloader) as ImageView
                )
                //fresco
                val frescoImageView = findViewById<SimpleDraweeView>(R.id.iv_fresco)
                frescoImageView.setImageURI(Uri.parse(frescoImageUrl))
                val imagePipeline = Fresco.getImagePipeline()
                // combines above two lines
                imagePipeline.clearCaches()
            }

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
                Toast.makeText(this@MainDebugActivityOkhttpV3, e.message, Toast.LENGTH_LONG)
                    .show()
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }

    companion object {
        const val TAG = "MainDebugActivity"

    }
}
