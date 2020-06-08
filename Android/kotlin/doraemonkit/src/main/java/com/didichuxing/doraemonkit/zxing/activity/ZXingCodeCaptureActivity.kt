package com.didichuxing.doraemonkit.zxing.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.king.zxing.CaptureActivity

/**
 * @author guofeng007 2020/6/8
 */
class ZXingCodeCaptureActivity : CaptureActivity() {
    override fun onResultCallback(resultString: String): Boolean {
        captureHelper.playBeep(true)
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(this, "Scan failed!", Toast.LENGTH_SHORT).show()
        } else {
            val resultIntent = Intent()
            val bundle = Bundle()
            bundle.putString(INTENT_EXTRA_KEY_QR_SCAN, resultString)
            resultIntent.putExtras(bundle)
            this.setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
        return true
    }

    companion object {
        const val INTENT_EXTRA_KEY_QR_SCAN = "result"
    }
}