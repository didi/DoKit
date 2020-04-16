package com.didichuxing.doraemondemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        findViewById<View>(R.id.tv).setOnClickListener {
            ToastUtils.showShort("aaaaa")
            ToastUtils.showShort("bbbbb")
            //DoraemonKit.show();
        }
    }
}