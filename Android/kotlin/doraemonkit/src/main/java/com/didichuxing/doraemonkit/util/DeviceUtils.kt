package com.didichuxing.doraemonkit.util

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.text.format.Formatter
import android.util.Log
import com.blankj.utilcode.util.DeviceUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileFilter
import java.io.FileReader
import java.io.IOException
import java.util.regex.Pattern

/**
 * Created by zhangweida on 2018/6/26.
 */
object DeviceUtils {
  private const val TAG = "DeviceUtils"
  private const val PREF_DEVICE = "com.kuaidi.daijia.driver.device_pref"
  private const val KEY_UUID = "key_uuid"
  private const val BRAND_HUAWEI = "huawei"
  private const val BRAND_HONOR = "honor"
  private const val BRAND_SAMSUNG = "samsung"
  private const val BRAND_XIAOMI = "xiaomi"
  private const val BRAND_HONGMI = "hongmi"
  private const val IMEI = ""
  private const val IMSI = ""
  private val PHONE_NUMBER: String? = null
  private var ROOTED: Boolean? = null// Default to return 1 core// Get directory containing CPU info
  // Filter to only list the devices we care about
  // Return the number of cores (virtual CPU devices)
// Check if filename is "cpu", followed by a single digit number

  /**
   * 获取CPU个数
   */
  val coreNum: Int
    get() {
      class CpuFilter : FileFilter {
        override fun accept(pathname: File): Boolean {
          // Check if filename is "cpu", followed by a single digit number
          return Pattern.matches("cpu[0-9]", pathname.name)
        }
      }
      return try {
        // Get directory containing CPU info
        val dir = File("/sys/devices/system/cpu/")
        // Filter to only list the devices we care about
        val files = dir.listFiles(CpuFilter())
        // Return the number of cores (virtual CPU devices)
        files?.size ?: 0
      } catch (e: Exception) {
        Log.e(TAG, "getCoreNum", e)
        // Default to return 1 core
        1
      }
    }

  /**
   * @param context
   * @return 手机总内存(兆)
   */
  fun getTotalMemory(context: Context?): Long {
    val str1 = "/proc/meminfo" // 系统内存信息文件
    val str2: String
    val arrayOfString: Array<String>
    var initialMemory: Long = 0
    try {
      val localFileReader = FileReader(str1)
      val localBufferedReader = BufferedReader(
        localFileReader, 8192
      )
      str2 = localBufferedReader.readLine() // 读取meminfo第一行，系统总内存大小
      if (!TextUtils.isEmpty(str2)) {
        arrayOfString = str2.split("\\s+".toRegex()).toTypedArray()
        initialMemory =
          Integer.valueOf(arrayOfString[1]) / 1024.toLong() // 获得系统总内存，单位是KB，乘以1024转换为Byte
      }
      localBufferedReader.close()
    } catch (e: IOException) {
    }
    return initialMemory // Byte转换为KB或者MB，内存大小规格化
  }

  /**
   * @param context
   * @return 手机当前可用内存(兆)
   */
  fun getAvailMemory(context: Context): Long { // 获取android当前可用内存大小
    val am =
      context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val mi = ActivityManager.MemoryInfo()
    am.getMemoryInfo(mi)
    return mi.availMem / 1024 / 1024
  }

  /**
   * 版本名
   */
  fun getVersionName(context: Context): String {
    val pi =
      getPackageInfo(context)
    return if (pi != null) {
      pi.versionName
    } else {
      ""
    }
  }

  /**
   * 版本号
   */
  fun getVersionCode(context: Context): Int {
    val pi =
      getPackageInfo(context)
    return pi?.versionCode ?: 0
  }

  fun getPackageInfo(context: Context): PackageInfo? {
    var pi: PackageInfo? = null
    try {
      val pm = context.packageManager
      pi = pm.getPackageInfo(
        context.packageName,
        PackageManager.GET_CONFIGURATIONS
      )
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return pi
  }

  fun getPackageInfoForPermission(context: Context): PackageInfo? {
    var pi: PackageInfo? = null
    try {
      val pm = context.packageManager
      pi = pm.getPackageInfo(
        context.packageName,
        PackageManager.GET_PERMISSIONS
      )
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return pi
  }

  /**
   * @return 手机链接wifi的路由器的名字
   */
  fun getWifiSSID(context: Context): String {
    val mWifi = context.applicationContext
      .getSystemService(Context.WIFI_SERVICE) as WifiManager
    if (mWifi.isWifiEnabled) {
      val wifiInfo = mWifi.connectionInfo
      return wifiInfo.ssid
    }
    return ""
  }

  /**
   * @return 手机链接wifi的路由器的mac地址
   */
  fun getWifiBSSID(context: Context): String {
    val mWifi = context.applicationContext
      .getSystemService(Context.WIFI_SERVICE) as WifiManager
    if (mWifi.isWifiEnabled) {
      val wifiInfo = mWifi.connectionInfo
      return wifiInfo.bssid
    }
    return ""
  }

  /**
   * @param permission
   * @param defalutValue 如果检测不到()检测发生异常的默认值
   * @return App是否拥有permission权限
   */
  fun checkPermission(
    context: Context,
    permission: String?,
    defalutValue: Boolean
  ): Boolean {
    var permit = false
    // 如果在判断的时候报错（有些机型），则默认不满足此权限
    permit = try {
      PackageManager.PERMISSION_GRANTED == context
        .checkCallingOrSelfPermission(permission!!)
    } catch (e: Exception) {
      defalutValue
    }
    return permit
  }

  /**
   * 判断wifi是否开启
   *
   * @param context
   * @return
   */
  fun isWifiEnabled(context: Context): Boolean {
    val wm = context.applicationContext
      .getSystemService(Context.WIFI_SERVICE) as WifiManager
    return (wm != null
        && (wm.wifiState == WifiManager.WIFI_STATE_ENABLED || wm.wifiState == WifiManager.WIFI_STATE_ENABLING))
  }

  /**
   * Is wifi connected
   *
   * @param context
   * @return
   */
  fun isWifiConnected(context: Context): Boolean {
    val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetInfo = connectivityManager.activeNetworkInfo
    return (activeNetInfo != null && activeNetInfo.isConnected
        && activeNetInfo.type == ConnectivityManager.TYPE_WIFI)
  }

  fun hasJellyBeanMr2(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
  }

  fun hasJellyBeanMr1(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
  }

  /**
   * 判断是否为某品牌生产的手机
   */
  fun isProduceByBrand(brand: String?): Boolean {
    var result = false
    // 判断厂家
    if (Build.MANUFACTURER != null) {
      if (Build.MANUFACTURER.toLowerCase().contains(brand!!)) {
        result = true
      }
    }
    // 判断品牌
    if (!result && Build.BRAND != null) {
      if (Build.BRAND.toLowerCase().contains(brand!!)) {
        result = true
      }
    }
    return result
  }

  val isProduceByXiaomi: Boolean
    get() = isProductInBrands(
      BRAND_XIAOMI,
      BRAND_HONGMI
    )

  val isProduceByHuaWei: Boolean
    get() = isProductInBrands(
      BRAND_HUAWEI,
      BRAND_HONOR
    )

  val isProduceBySamsung: Boolean
    get() = isProduceByBrand(BRAND_SAMSUNG)

  fun isProductInBrands(vararg brands: String?): Boolean {
    for (brand in brands) {
      if (isProduceByBrand(brand)) {
        return true
      }
    }
    return false
  }

  fun isSimReady(context: Context): Boolean {
    try {
      val mgr = context
        .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
      Log.d(
        TAG,
        "[isSimReady]" + mgr.simState
      )
      return (TelephonyManager.SIM_STATE_READY == mgr.simState
          || TelephonyManager.SIM_STATE_NETWORK_LOCKED == mgr.simState)
    } catch (e: Exception) {
      Log.e(TAG, "[isSimReady]$e")
    }
    return false
  }

  /**
   * 判断当前手机是否有ROOT权限
   *
   * @return 是否ROOT
   */
  fun isRoot(context: Context?): Boolean {
    val rooted = ROOTED
    if (rooted != null) {
      return rooted
    }
    try {
      ROOTED =
        DeviceUtils.isDeviceRooted()
      if (ROOTED!!) {
        Log.w(TAG, "Device rooted.")
      }
    } catch (e: Exception) {
      Log.e(TAG, "Check root failed.", e)
      ROOTED = false
    }
    return ROOTED?: false
  }

  fun hasFrontCamera(): Boolean {
    val cameraCount = Camera.getNumberOfCameras()
    val info = CameraInfo()
    for (i in 0 until cameraCount) {
      Camera.getCameraInfo(i, info)
      if (CameraInfo.CAMERA_FACING_FRONT == info.facing) {
        return true
      }
    }
    return false
  }

  /**
   * @return Number of bytes available on external storage
   */
  val externalAvailableSpaceInBytes: Long
    get() {
      var availableSpace = -1L
      try {
        val stat = StatFs(
          Environment.getExternalStorageDirectory()
            .path
        )
        availableSpace = (stat.availableBlocks.toLong()
            * stat.blockSize.toLong())
      } catch (e: Exception) {
        e.printStackTrace()
      }
      return availableSpace
    }

  /**
   * 获得SD卡总大小
   *
   * @return
   */
  private fun getSDTotalSize(context: Context): String {
    val path = Environment.getExternalStorageDirectory()
    val stat = StatFs(path.path)
    val blockSize = stat.blockSize.toLong()
    val totalBlocks = stat.blockCount.toLong()
    return Formatter.formatFileSize(context, blockSize * totalBlocks)
  }

  /**
   * 获得sd卡剩余容量，即可用大小
   *
   * @return
   */
  private fun getSDAvailableSize(context: Context): String {
    val path = Environment.getExternalStorageDirectory()
    val stat = StatFs(path.path)
    val blockSize = stat.blockSize.toLong()
    val availableBlocks = stat.availableBlocks.toLong()
    return Formatter.formatFileSize(context, blockSize * availableBlocks)
  }

  /**
   * 获得机身内存总大小
   *
   * @return
   */
  private fun getRomTotalSize(context: Context): String {
    val path = Environment.getDataDirectory()
    val stat = StatFs(path.path)
    val blockSize = stat.blockSize.toLong()
    val totalBlocks = stat.blockCount.toLong()
    return Formatter.formatFileSize(context, blockSize * totalBlocks)
  }

  /**
   * 获得机身可用内存
   *
   * @return
   */
  private fun getRomAvailableSize(context: Context): String {
    val path = Environment.getDataDirectory()
    val stat = StatFs(path.path)
    val blockSize = stat.blockSize.toLong()
    val availableBlocks = stat.availableBlocks.toLong()
    return Formatter.formatFileSize(context, blockSize * availableBlocks)
  }

  fun getSDCardSpace(context: Context): String {
    return try {
      val free =
        getSDAvailableSize(context)
      val total =
        getSDTotalSize(context)
      "$free/$total"
    } catch (e: Exception) {
      "-/-"
    }
  }

  fun getRomSpace(context: Context): String {
    return try {
      val free =
        getRomAvailableSize(context)
      val total =
        getRomTotalSize(context)
      "$free/$total"
    } catch (e: Exception) {
      "-/-"
    }
  }
}