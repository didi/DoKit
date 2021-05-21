package com.didichuxing.doraemonkit.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.provider.ContactsContract;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @desc: 权限检测工具类
 */
public class DoKitPermissionUtil {
    private static final String TAG = "PermissionUtil";
    //音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    //采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private final static int AUDIO_SAMPLE_RATE = 16000;
    //声道 单声道
    private final static int AUDIO_CHANNEL = android.media.AudioFormat.CHANNEL_IN_MONO;
    //编码
    private final static int AUDIO_ENCODING = android.media.AudioFormat.ENCODING_PCM_16BIT;

    private static final int OP_SYSTEM_ALERT_WINDOW = 24;

    private DoKitPermissionUtil() {
    }

    /**
     * 判断是否具有悬浮窗权限
     * @param context
     * @return
     */
    public static boolean canDrawOverlays(Context context) {
        //android 6.0及以上的判断条件
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        //android 4.4~6.0的判断条件
        return checkOp(context, OP_SYSTEM_ALERT_WINDOW);
    }

    private static boolean checkOp(Context context, int op) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            Class clazz = AppOpsManager.class;
            try {
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Process.myUid(), context.getPackageName());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 请求悬浮窗权限
     * @param context
     */
    public static void requestDrawOverlays(Context context) {
        Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + context.getPackageName()));
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            LogHelper.e(TAG, "No activity to handle intent");
        }
    }



    public static boolean isMockLocationEnabled(Context context) {
        boolean isMockLocation = false;
        try {
            //if marshmallow
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AppOpsManager opsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (opsManager != null) {
                    isMockLocation = (opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), context.getPackageName()) == AppOpsManager.MODE_ALLOWED);
                }
            } else {
                // in marshmallow this will always return true
                isMockLocation = !android.provider.Settings.Secure.getString(context.getContentResolver(), "mock_location").equals("0");
            }
        } catch (Exception e) {
            return false;
        }
        return isMockLocation;
    }

    /**
     * TargetVersionSdk大于6.0时的权限检查方法
     *
     * @param context
     * @param perms
     * @return
     */
    public static boolean hasPermissions(@NonNull Context context,
                                         @Size(min = 1) @NonNull String... perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default");

            // DANGER ZONE!!! Changing this will break the library.
            return true;
        }

        // Null context may be passed if we have detected Low API (less than M) so getting
        // to this point with a null context should not be possible.
        if (context == null) {
            throw new IllegalArgumentException("Can't check permissions for null context");
        }

        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检测是否有sd卡读写权限，不可靠。无sd卡或者磁盘满了的情况下，即使赋予了权限也可能报无权限
     *
     * @return
     */
    public static boolean checkStorageUnreliable() {
        FileOutputStream outputStream = null;
        FileInputStream inputStream = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + ".DoraemonkitTest.kit");
            outputStream = new FileOutputStream(file);
            outputStream.write(1);
            outputStream.flush();
            outputStream.close();
            outputStream = null;

            inputStream = new FileInputStream(file);
            inputStream.read();
            inputStream.close();
            inputStream = null;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 检测是否有定位权限，不可靠，比如在室内打开app，这种情况下定位模块没有值，会报无权限
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public static boolean checkLocationUnreliable(Context context) {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            return location != null || location2 != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检测是否有相机权限
     *
     * @return
     */
    public static boolean checkCameraUnreliable() {
        Camera camera = null;
        try {
            camera = android.hardware.Camera.open();
            return camera != null;
        } catch (Exception e) {
            return false;
        } finally {
            if (camera != null) {
                try {
                    camera.release();
                } catch (Exception e) {

                }
            }
        }
    }

    /**
     * 检测是否有录音权限
     *
     * @return
     */
    public static boolean checkRecordUnreliable() {
        AudioRecord audioRecord = null;
        int bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING);
        try {
            audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, bufferSizeInBytes);
            return audioRecord != null && audioRecord.getState() == AudioRecord.STATE_INITIALIZED;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (audioRecord != null) {
                try {
                    audioRecord.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 检测是否有读取设备信息权限
     *
     * @return
     */
    public static boolean checkReadPhoneUnreliable(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission")
            String imei = tm.getDeviceId();
            return !TextUtils.isEmpty(imei);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检测是否有读取联系人权限，不可靠。在联系人列表为空时，即使赋予了读取权限也会报没权限
     *
     * @return
     */
    public static boolean checkReadContactUnreliable(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
