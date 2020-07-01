package com.didichuxing.doraemonkit.weex.devtool

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.didichuxing.doraemonkit.weex.util.launchActivity
import com.king.zxing.Intents
import org.apache.weex.WXEnvironment
import org.apache.weex.WXSDKEngine

/**
 * Transformed by alvince on 2020/7/1
 *
 * @author haojianglong
 * @date 2019-06-25
 */
class DevToolActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_CAMERA = 197
        private const val REQUEST_CODE_SCAN = 392

        private const val TAG = "DevToolActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startScan()
            return
        }
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
        } else {
            startScan()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        grantResults.takeIf { requestCode == REQUEST_CODE_CAMERA }
            ?.also { results ->
                permissions.takeIf { it.isNotEmpty() }?.forEachIndexed { index, s ->
                    if (s == Manifest.permission.CAMERA
                        && results[index] == PackageManager.PERMISSION_GRANTED
                    ) {
                        startScan()
                        return@also
                    }
                }
                finish()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.also { result ->
            result.takeIf {
                requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK
            }
                ?.getStringExtra(Intents.Scan.RESULT)
                ?.takeIf { it.isNotEmpty() }
                ?.let {
                    try {
                        Uri.parse(it)
                    } catch (ex: Exception) {
                        Log.e(TAG, "Fail to parse scan result: $it", ex)
                        null
                    }
                }
                ?.also { handleScanResult(it) }
                ?: handleNoResult()
        }
    }

    private fun startScan() {
        launchActivity(
            Intent(this, DevToolScanActivity::class.java),
            REQUEST_CODE_SCAN
        )
    }

    private fun handleNoResult() {
        Toast.makeText(applicationContext, "没有扫描到任何内容>_<", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun handleScanResult(uri: Uri) {
        if (WXEnvironment.isApkDebugable()) {
            val devToolUrl = uri.getQueryParameter("_wx_devtool")
            if (!TextUtils.isEmpty(devToolUrl)) {
                WXEnvironment.sRemoteDebugProxyUrl = devToolUrl
                WXEnvironment.sDebugServerConnectable = true
                WXSDKEngine.reload(applicationContext, false)
            }
        }
        finish()
    }

}
