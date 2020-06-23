package com.didichuxing.doraemondemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.didichuxing.doraemonkit.DoraemonKit
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {
    companion object {
        val TAG = "SecondActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        tv.setOnClickListener {
            DoraemonKit.hide()
        }
    }
}