package com.didichuxing.doraemondemo.old

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.text.format.Formatter
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.navi.model.*
import com.blankj.utilcode.util.*
import com.didichuxing.doraemondemo.*
import com.didichuxing.doraemondemo.amap.AMapRouterFragment
import com.didichuxing.doraemondemo.comm.CommLauncher
import com.didichuxing.doraemondemo.databinding.ActivityMainBinding
import com.didichuxing.doraemondemo.db.DatabaseHelper
import com.didichuxing.doraemondemo.mc.MCActivity
import com.didichuxing.doraemondemo.module.leak.LeakActivity
import com.didichuxing.doraemondemo.module.retrofit.GithubService
import com.didichuxing.doraemonkit.DoKit
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.*
import java.net.*
import kotlin.coroutines.resume

/**
 * @author jintai
 */
class MainDebugActivityOkhttpV3 : BaseActivity(), CoroutineScope by MainScope() {

    private var okHttpClient: OkHttpClient? = null
    private var mLocationManager: LocationManager? = null

    private lateinit var mAdapter: MainAdapter

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * github 接口
     */
    private var githubService: GithubService? = null

    private var _binding: ActivityMainBinding? = null



    val datas = mutableListOf(
        "显示/隐藏Dokit入口",
        "显示工具面板",
        "弹框测试",
        "系统反射测试",
        "获取已安装的app",
        "截屏",
        "跳转其他Activity",
        "一机多控",
        "NormalWebView",
        "X5WebView",
        "模拟内存泄漏",
        "函数调用耗时(TAG:MethodCostUtil)",
        "获取位置信息(系统)",
        "获取位置信息(高德)",
        "高德路径规划",
        "OkHttp Mock",
        "HttpURLConnection Mock",
        "Retrofit Mock",
        "模拟Crash",
        "创建数据库",
        "上传文件",
        "下载文件"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            mAdapter = MainAdapter(R.layout.item_main_rv, datas)
            it.initView(this)
        }
        mAdapter.setOnItemClickListener { _, _, position ->
            when (datas[position]) {
                "弹框测试" -> {
//                    val bundle = Bundle()
//                    bundle.putString("text", "测试同步异常")
//                    DoKit.launchFloating<McDialogDoKitView>(bundle = bundle)

//                    lifecycleScope.launch {
//                        delay(15000)
//                        Log.i(TAG, "===inner===")
//                    }
//                    Log.i(TAG, "===out===")

                    startActivity(Intent(this, MainDoKitActivity::class.java))

                }
                "系统反射测试" -> {
                    try {
                        val activityClass = Class.forName("dalvik.system.VMRuntime")
                        val field = activityClass.getDeclaredMethod("setHiddenApiExemptions", Array<String>::class.java)
                        field.isAccessible = true
                        Toast.makeText(this, "call success!!", Toast.LENGTH_SHORT).show()
                    } catch (e: Throwable) {
                        Log.e(TAG, "error:", e)
                        Toast.makeText(this, "error: $e", Toast.LENGTH_SHORT).show()
                    }
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
                    packageManager.getInstalledApplications(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) PackageManager.MATCH_UNINSTALLED_PACKAGES
                        else PackageManager.GET_UNINSTALLED_PACKAGES
                    )
                    packageManager.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES)
                }
                "截屏" -> {
//                    lifecycleScope.launch {
//
//                        DoKit.launchFloating(BorderDoKitView::class.java)
//                        val borderDoKitView =
//                            DoKit.getDoKitView(
//                                this@MainDebugActivityOkhttpV3,
//                                BorderDoKitView::class.java
//                            ) as BorderDoKitView
//                        borderDoKitView.showBorder(view)
//                        withContext(Dispatchers.IO) {
//                            delay(200)
//                            val bitmap = ScreenUtils.screenShot(this@MainDebugActivityOkhttpV3)
//                            val output = File(getCrashCacheDir(), "test.png")
//                            DoKitImageUtil.bitmap2File(bitmap, 100, output)
//                        }
//
//                    }

                }
                "跳转其他Activity" -> {
                    startActivity(Intent(this, EmptyActivity::class.java))
                }
                "一机多控" -> {
                    startActivity(Intent(this, MCActivity::class.java))
                }
                "NormalWebView" -> {
                    startActivity(Intent(this, WebViewSystemActivity::class.java))
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
                "获取位置信息(高德)" -> {
                    startAMapLocation()
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
                    lifecycleScope.launch {
                        val result = githubService?.githubUserInfo("jtsky")
                        Log.i(TAG, "result===>${result}")
                    }

                }
                "模拟Crash" -> {
                    checkNotNull(testCrash())
                }
                "创建数据库" -> {
                    val dbHelper = DatabaseHelper(this, "BookStore.db", null, 1)
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


        githubService = retrofit.create(GithubService::class.java)
    }

    private fun ActivityMainBinding.initView(context: Context) {
        tvEnv.text = "${getString(R.string.app_build_types)}:Debug"
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = mAdapter
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
     * 启动高德定位
     */
    private fun startAMapLocation() {
        // 确保调用SDK任何接口前先调用更新隐私合规updatePrivacyShow、updatePrivacyAgree两个接口并且参数值都为true，若未正确设置有崩溃风险
        AMapLocationClient.updatePrivacyShow(this, true, true)
        AMapLocationClient.updatePrivacyAgree(this, true)

        //声明mLocationOption对象
        var mLocationOption: AMapLocationClientOption? = null
        val mlocationClient = AMapLocationClient(this)

        //初始化定位参数
        mLocationOption = AMapLocationClientOption()
        //设置定位监听
        mlocationClient!!.setLocationListener(mapLocationListener)
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.interval = 2000
        //设置定位参数
        mlocationClient!!.setLocationOption(mLocationOption)
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient!!.startLocation()
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
        var len: Int
        val buf = ByteArray(2048)
        val fos = FileOutputStream(saveFile)
        `is`.use { input ->
            fos.use { output ->
                while (input.read(buf).also { len = it } != -1) {
                    output.write(buf, 0, len)
                }
                output.flush()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        okHttpClient!!.dispatcher().cancelAll()
        mLocationManager!!.removeUpdates(mLocationListener)
    }

    companion object {
        const val TAG = "MainDebugActivity"
    }
}
