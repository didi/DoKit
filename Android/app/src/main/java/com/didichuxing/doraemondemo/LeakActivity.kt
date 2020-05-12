package com.didichuxing.doraemondemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

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