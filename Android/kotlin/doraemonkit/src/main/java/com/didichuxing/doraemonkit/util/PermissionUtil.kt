package com.didichuxing.doraemonkit.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.hardware.Camera
import android.location.LocationManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Process
import android.provider.ContactsContract
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import androidx.annotation.Size
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.InvocationTargetException

/**
 * @desc: 权限检测工具类
 */
object PermissionUtil {
    private const val TAG = "PermissionUtil"

    //音频输入-麦克风
    private const val AUDIO_INPUT = MediaRecorder.AudioSource.MIC

    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    //采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private const val AUDIO_SAMPLE_RATE = 16000

    //声道 单声道
    private const val AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO

    //编码
    private const val AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT
    private const val OP_SYSTEM_ALERT_WINDOW = 24

    /**
     * 判断是否具有悬浮窗权限
     * @param context
     * @return
     */
    fun canDrawOverlays(context: Context): Boolean {
        //android 6.0及以上的判断条件
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else checkOp(context, OP_SYSTEM_ALERT_WINDOW)
        //android 4.4~6.0的判断条件
    }

    private fun checkOp(context: Context, op: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val manager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val clazz: Class<*> = AppOpsManager::class.java
            try {
                val method = clazz.getDeclaredMethod("checkOp", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, String::class.java)
                return AppOpsManager.MODE_ALLOWED == method.invoke(manager, op, Process.myUid(), context.packageName) as Int
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        }
        return true
    }

    /**
     * 请求悬浮窗权限
     * @param context
     */
    fun requestDrawOverlays(context: Context) {
        val intent = Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + context.packageName))
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            //LogHelper.e(TAG, "No activity to handle intent")
        }
    }

    fun isMockLocationEnabled(context: Context): Boolean {
        var isMockLocation = false
        try {
            //if marshmallow
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val opsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                if (opsManager != null) {
                    isMockLocation = opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, Process.myUid(), context.packageName) == AppOpsManager.MODE_ALLOWED
                }
            } else {
                // in marshmallow this will always return true
                isMockLocation = Settings.Secure.getString(context.contentResolver, "mock_location") != "0"
            }
        } catch (e: Exception) {
            return false
        }
        return isMockLocation
    }

    /**
     * TargetVersionSdk大于6.0时的权限检查方法
     *
     * @param context
     * @param perms
     * @return
     */
    fun hasPermissions(context: Context,
                       @Size(min = 1) vararg perms: String): Boolean {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default")

            // DANGER ZONE!!! Changing this will break the library.
            return true
        }

        // Null context may be passed if we have detected Low API (less than M) so getting
        // to this point with a null context should not be possible.
        requireNotNull(context) { "Can't check permissions for null context" }
        for (perm in perms) {
            if (ContextCompat.checkSelfPermission(context, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * 检测是否有sd卡读写权限，不可靠。无sd卡或者磁盘满了的情况下，即使赋予了权限也可能报无权限
     *
     * @return
     */
    fun checkStorageUnreliable(): Boolean {
        var outputStream: FileOutputStream? = null
        var inputStream: FileInputStream? = null
        try {
            val file = File(Environment.getExternalStorageDirectory().toString() + File.separator + ".DoraemonkitTest.kit")
            outputStream = FileOutputStream(file)
            outputStream.write(1)
            outputStream.flush()
            outputStream.close()
            outputStream = null
            inputStream = FileInputStream(file)
            inputStream.read()
            inputStream.close()
            inputStream = null
        } catch (e: Exception) {
            return false
        } finally {
            try {
                outputStream?.close()
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return true
    }

    /**
     * 检测是否有定位权限，不可靠，比如在室内打开app，这种情况下定位模块没有值，会报无权限
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    fun checkLocationUnreliable(context: Context): Boolean {
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val location2 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            location != null || location2 != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 检测是否有相机权限
     *
     * @return
     */
    fun checkCameraUnreliable(): Boolean {
        var camera: Camera? = null
        return try {
            camera = Camera.open()
            camera != null
        } catch (e: Exception) {
            false
        } finally {
            if (camera != null) {
                try {
                    camera.release()
                } catch (e: Exception) {
                }
            }
        }
    }

    /**
     * 检测是否有录音权限
     *
     * @return
     */
    fun checkRecordUnreliable(): Boolean {
        var audioRecord: AudioRecord? = null
        val bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING)
        return try {
            audioRecord = AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, bufferSizeInBytes)
            audioRecord != null && audioRecord.state == AudioRecord.STATE_INITIALIZED
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            if (audioRecord != null) {
                try {
                    audioRecord.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 检测是否有读取设备信息权限
     *
     * @return
     */
    fun checkReadPhoneUnreliable(context: Context): Boolean {
        return try {
            val tm = context
                    .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            @SuppressLint("MissingPermission") val imei = tm.deviceId
            !TextUtils.isEmpty(imei)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 检测是否有读取联系人权限，不可靠。在联系人列表为空时，即使赋予了读取权限也会报没权限
     *
     * @return
     */
    fun checkReadContactUnreliable(context: Context): Boolean {
        var cursor: Cursor? = null
        return try {
            cursor = context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        } finally {
            cursor?.close()
        }
    }
}