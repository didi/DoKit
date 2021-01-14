package com.didichuxing.doraemonkit.weex.devtool;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.widget.Toast;

import com.didichuxing.doraemonkit.zxing.activity.CaptureActivity;

import org.apache.weex.WXEnvironment;
import org.apache.weex.WXSDKEngine;

/**
 * @author haojianglong
 * @date 2019-06-25
 */
public class DevToolActivity extends AppCompatActivity {

    private final int REQUEST_CODE_CAMERA = 0x100;
    private final int REQUEST_CODE_SCAN = 0x101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.CAMERA};
                requestPermissions(permissions, REQUEST_CODE_CAMERA);
            } else {
                startScan();
            }
        } else {
            startScan();
        }
    }

    private void startScan() {
        Intent intent = new Intent(this, DevToolScanActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (permissions.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    if (Manifest.permission.CAMERA.equals(permissions[i]) &&
                            grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        startScan();
                        return;
                    }
                }
            }
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN)) {
                String code = data.getStringExtra(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
                if (!TextUtils.isEmpty(code)) {
                    try {
                        Uri uri = Uri.parse(code);
                        handleScanResult(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        finish();
                    }
                } else {
                    handleNoResult();
                }
            } else {
                handleNoResult();
            }
        } else {
            handleNoResult();
        }
    }

    private void handleNoResult() {
        Toast.makeText(getApplicationContext(), "没有扫描到任何内容>_<", Toast.LENGTH_SHORT);
        finish();
    }

    private void handleScanResult(Uri uri) {
        if (WXEnvironment.isApkDebugable()) {
            String devToolUrl = uri.getQueryParameter("_wx_devtool");
            if (!TextUtils.isEmpty(devToolUrl)) {
                WXEnvironment.sRemoteDebugProxyUrl = devToolUrl;
                WXEnvironment.sDebugServerConnectable = true;
                WXSDKEngine.reload(getApplicationContext(), false);
            }
        }
        finish();
    }

}
