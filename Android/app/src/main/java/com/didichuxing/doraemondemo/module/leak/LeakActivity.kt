package com.didichuxing.doraemondemo.module.leak

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.didichuxing.doraemondemo.App
import com.didichuxing.doraemondemo.R

/**
 * 模拟内存泄漏的activity
 */
class LeakActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leak)
        App.leakActivity = this
    }
}
