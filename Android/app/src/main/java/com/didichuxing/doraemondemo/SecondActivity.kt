package com.didichuxing.doraemondemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.didichuxing.doraemonkit.DoraemonKit
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        tv.setOnClickListener {
            DoraemonKit.show()
        }
    }
}